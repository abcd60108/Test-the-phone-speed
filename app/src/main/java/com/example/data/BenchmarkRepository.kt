package com.example.data

import kotlinx.coroutines.flow.Flow

class BenchmarkRepository(private val benchmarkDao: BenchmarkDao) {
    val allRecords: Flow<List<BenchmarkRecord>> = benchmarkDao.getAllRecords()

    suspend fun insert(record: BenchmarkRecord) {
        benchmarkDao.insertRecord(record)
    }

    suspend fun deleteById(id: Int) {
        benchmarkDao.deleteRecordById(id)
    }

    suspend fun clearAll() {
        benchmarkDao.clearAll()
    }
}
