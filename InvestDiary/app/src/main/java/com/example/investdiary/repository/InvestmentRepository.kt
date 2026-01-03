package com.example.investdiary.repository

import com.example.investdiary.model.Investment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class InvestmentRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val investmentsCollection = firestore.collection("investments")

    // Získání všech investic (real-time)
    fun getAllInvestments(): Flow<List<Investment>> = callbackFlow {
        val listener = investmentsCollection
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val investments = snapshot?.documents?.mapNotNull {
                    it.toObject(Investment::class.java)?.copy(id = it.id)
                } ?: emptyList()
                trySend(investments)
            }
        awaitClose { listener.remove() }
    }

    // Přidání nové investice
    suspend fun addInvestment(investment: Investment): Result<String> {
        return try {
            val docRef = investmentsCollection.add(investment).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Smazání investice
    suspend fun deleteInvestment(id: String): Result<Unit> {
        return try {
            investmentsCollection.document(id).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Update investice
    suspend fun updateInvestment(investment: Investment): Result<Unit> {
        return try {
            investmentsCollection.document(investment.id)
                .set(investment).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
