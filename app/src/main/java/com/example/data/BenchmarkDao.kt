package com.example.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import kotlinx.coroutines.flow.Flow

@Dao
interface BenchmarkDao {
    @Query("SELECT * FROM benchmark_records ORDER BY timestamp DESC")
    fun getAllRecords(): Flow<List<BenchmarkRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: BenchmarkRecord)

    @Query("DELETE FROM benchmark_records WHERE id = :id")
    suspend fun deleteRecordById(id: Int)

    @Query("DELETE FROM benchmark_records")
    suspend fun clearAll()
}
