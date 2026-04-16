package com.group10.terrace.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group10.terrace.model.CartItem
import com.group10.terrace.model.Product
import com.group10.terrace.repository.MarketplaceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MarketplaceViewModel : ViewModel() {

    private val repository = MarketplaceRepository()

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems

    private val _checkoutState = MutableStateFlow<CheckoutState>(CheckoutState.Idle)
    val checkoutState: StateFlow<CheckoutState> = _checkoutState

    val totalCartPrice: Double
        get() = _cartItems.value.sumOf { it.price * it.quantity }

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

    fun addToCart(userId: String, product: Product, quantity: Int) {
        repository.addOrUpdateCart(userId, product, quantity) { success ->
        }
    }

    fun removeFromCart(userId: String, cartItemId: String) {
        repository.removeFromCart(userId, cartItemId) { }
    }

    fun processCheckout(userId: String) {
        if (_cartItems.value.isEmpty()) return

        _checkoutState.value = CheckoutState.Loading

        viewModelScope.launch {
            val result = repository.processCheckoutSimulator(userId, _cartItems.value, totalCartPrice)

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

    fun seedDatabase(context: com.google.api.Context) {
        repository.uploadKatalogKeFirestore(context)
    }
}

sealed class CheckoutState {
    object Idle : CheckoutState()
    object Loading : CheckoutState()
    data class Success(val message: String) : CheckoutState()
    data class Error(val message: String) : CheckoutState()
}