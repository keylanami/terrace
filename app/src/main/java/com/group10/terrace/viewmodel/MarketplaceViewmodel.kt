package com.group10.terrace.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group10.terrace.model.CartItem
import com.group10.terrace.model.Order
import com.group10.terrace.model.Product
import com.group10.terrace.model.UserAddress
import com.group10.terrace.repository.AuthRepository
import com.group10.terrace.repository.MarketplaceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MarketplaceViewModel : ViewModel() {

    private val repository = MarketplaceRepository()
    private val authRepo = AuthRepository()

    // ── Products ──────────────────────────────────────────────────────────
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products

    // ── Cart ──────────────────────────────────────────────────────────────
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems

    // ── Order History ─────────────────────────────────────────────────────
    private val _orderHistory = MutableStateFlow<List<Order>>(emptyList())
    val orderHistory: StateFlow<List<Order>> = _orderHistory

    // ── Checkout State ────────────────────────────────────────────────────
    private val _checkoutState = MutableStateFlow<CheckoutState>(CheckoutState.Idle)
    val checkoutState: StateFlow<CheckoutState> = _checkoutState

    // ── Checkout: alamat terpilih ─────────────────────────────────────────
    private val _selectedAddress = MutableStateFlow<UserAddress?>(null)
    val selectedAddress: StateFlow<UserAddress?> = _selectedAddress

    // ── Checkout: gunakan poin ────────────────────────────────────────────
    private val _usePoints = MutableStateFlow(false)
    val usePoints: StateFlow<Boolean> = _usePoints

    // ── Checkout: metode pembayaran ───────────────────────────────────────
    private val _paymentMethod = MutableStateFlow<PaymentMethod>(PaymentMethod.QRIS)
    val paymentMethod: StateFlow<PaymentMethod> = _paymentMethod

    // ── Alamat user (real-time listener) ─────────────────────────────────
    private val _userAddresses = MutableStateFlow<List<UserAddress>>(emptyList())
    val userAddresses: StateFlow<List<UserAddress>> = _userAddresses

    // ── User available points ─────────────────────────────────────────────
    private val _userCurrentPoints = MutableStateFlow(0)
    val userCurrentPoints: StateFlow<Int> = _userCurrentPoints

    companion object {
        const val ONGKOS_KIRIM = 10_000.0
    }

    init {
        loadProducts()
    }

    private fun loadProducts() {
        repository.getProducts { productList ->
            _products.value = productList
        }
    }

    fun loadCart(userId: String) {
        repository.getCartItems(userId) { items ->
            _cartItems.value = items
        }
    }

    fun loadOrderHistory(userId: String) {
        repository.getOrderHistory(userId) { orders ->
            _orderHistory.value = orders
        }
    }

    fun loadUserAddresses(userId: String) {
        authRepo.listenToAddresses(userId) { addresses ->
            _userAddresses.value = addresses
            if (_selectedAddress.value == null && addresses.isNotEmpty()) {
                _selectedAddress.value = addresses.first()
            }
        }
    }

    fun loadUserPoints(userId: String) {
        authRepo.getCurrentUser { user ->
            _userCurrentPoints.value = user?.currentPoint ?: 0
        }
    }

    // ── Checkout actions ──────────────────────────────────────────────────

    fun selectAddress(address: UserAddress) {
        _selectedAddress.value = address
    }

    fun toggleUsePoints() {
        _usePoints.value = !_usePoints.value
    }

    fun selectPaymentMethod(method: PaymentMethod) {
        _paymentMethod.value = method
    }

    fun addAddress(userId: String, address: UserAddress, onResult: (Boolean) -> Unit) {
        authRepo.addAddress(userId, address, onResult)
    }

    // ── Price calculation ─────────────────────────────────────────────────

    val subtotal: Double
        get() = _cartItems.value.sumOf { it.price * it.quantity }

    fun potonganPoin(userPoints: Int): Double {
        if (!_usePoints.value) return 0.0
        // max diskon poin = 50% dari subtotal
        val maxDiskon = subtotal * 0.5
        return minOf(userPoints.toDouble(), maxDiskon)
    }

    fun totalPembayaran(userPoints: Int): Double {
        return (subtotal + ONGKOS_KIRIM - potonganPoin(userPoints)).coerceAtLeast(0.0)
    }

    // ── Checkout proses ───────────────────────────────────────────────────

    fun processCheckout(userId: String) {
        if (_cartItems.value.isEmpty()) return
        if (_selectedAddress.value == null) {
            _checkoutState.value = CheckoutState.Error("Pilih alamat pengiriman terlebih dahulu.")
            return
        }

        _checkoutState.value = CheckoutState.Loading

        val total = totalPembayaran(_userCurrentPoints.value)

        viewModelScope.launch {
            val result = repository.processCheckoutSimulator(userId, _cartItems.value, total)
            if (result.isSuccess) {
                _checkoutState.value = CheckoutState.Success(result.getOrNull() ?: "Berhasil!")
            } else {
                _checkoutState.value = CheckoutState.Error(result.exceptionOrNull()?.message ?: "Transaksi Gagal")
            }
        }
    }

    fun resetCheckoutState() {
        _checkoutState.value = CheckoutState.Idle
    }

    // ── Cart actions ──────────────────────────────────────────────────────

    fun addToCart(userId: String, product: Product, quantity: Int) {
        repository.addOrUpdateCart(userId, product, quantity) { }
    }

    fun removeFromCart(userId: String, cartItemId: String) {
        repository.removeFromCart(userId, cartItemId) { }
    }

    fun updateQuantity(userId: String, cartItem: CartItem, newQty: Int) {
        if (newQty <= 0) {
            removeFromCart(userId, cartItem.cartItemId)
            return
        }
        _cartItems.value = _cartItems.value.map {
            if (it.cartItemId == cartItem.cartItemId) it.copy(quantity = newQty) else it
        }
        val dummyProduct = Product(
            id = cartItem.productId,
            name = cartItem.productName,
            price = cartItem.price,
            stock = cartItem.maxStock,
            imageUrl = cartItem.imageUrl
        )
        repository.addOrUpdateCart(userId, dummyProduct, newQty) { }
    }

    fun seedDatabase(context: android.content.Context) {
        repository.uploadKatalogKeFirestore(context)
    }
}

enum class PaymentMethod { QRIS, BANK_VIRTUAL_ACCOUNT }

sealed class CheckoutState {
    object Idle : CheckoutState()
    object Loading : CheckoutState()
    data class Success(val message: String) : CheckoutState()
    data class Error(val message: String) : CheckoutState()
}