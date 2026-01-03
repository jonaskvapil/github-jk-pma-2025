package com.example.investdiary.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.investdiary.model.Investment
import com.example.investdiary.repository.InvestmentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class InvestmentViewModel : ViewModel() {
    private val repository = InvestmentRepository()

    private val _investments = MutableStateFlow<List<Investment>>(emptyList())
    val investments: StateFlow<List<Investment>> = _investments.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadInvestments()
    }

    private fun loadInvestments() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.getAllInvestments().collect { investmentList ->
                    _investments.value = investmentList
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _errorMessage.value = "Chyba při načítání dat: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    fun addInvestment(investment: Investment) {
        viewModelScope.launch {
            repository.addInvestment(investment).onSuccess {
                _errorMessage.value = null
            }.onFailure { e ->
                _errorMessage.value = "Chyba při přidávání: ${e.message}"
            }
        }
    }

    fun deleteInvestment(id: String) {
        viewModelScope.launch {
            repository.deleteInvestment(id).onFailure { e ->
                _errorMessage.value = "Chyba při mazání: ${e.message}"
            }
        }
    }

    // Celková hodnota portfolia
    fun getTotalValue(): Double {
        return _investments.value.sumOf { it.currentValue }
    }

    // Celkový profit/ztráta
    fun getTotalProfitLoss(): Double {
        return _investments.value.sumOf { it.profitLoss }
    }
}
