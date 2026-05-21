package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "benchmark_records")
data class BenchmarkRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val overallScore: Int,
    val cpuScore: Int,
    val ramScore: Int,
    val storageScore: Int,
    val gpuScore: Int,
    val networkDownloadSpeed: Double, // in Mbps
    val networkUploadSpeed: Double,   // in Mbps
    val networkPing: Double           // in ms
)
