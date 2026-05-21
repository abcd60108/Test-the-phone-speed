package com.example

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.AppDatabase
import com.example.data.BenchmarkRecord
import com.example.data.BenchmarkRepository
import com.example.ui.*
import com.example.ui.theme.MyApplicationTheme
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.cos
import kotlin.math.sin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Setup Database & Repository
        val database = AppDatabase.getDatabase(this)
        val repository = BenchmarkRepository(database.benchmarkDao())

        setContent {
            MyApplicationTheme {
                val viewModel: BenchmarkViewModel by viewModels {
                    ViewModelProviderFactory(repository, applicationContext)
                }
                
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF060913) // Custom ultra-dark cyber tech background
                ) {
                    SpeedTestAppScreen(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun SpeedTestAppScreen(viewModel: BenchmarkViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFF060913)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Background glowing tech grids or spots
            Canvas(modifier = Modifier.fillMaxSize()) {
                // Top gradient bubble
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF00FFCC).copy(alpha = 0.07f), Color.Transparent),
                        center = Offset(size.width * 0.2f, size.height * 0.1f),
                        radius = size.width * 0.8f
                    ),
                    center = Offset(size.width * 0.2f, size.height * 0.1f),
                    radius = size.width * 0.8f
                )
                // Bottom gradient bubble
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFFFF007F).copy(alpha = 0.05f), Color.Transparent),
                        center = Offset(size.width * 0.8f, size.height * 0.8f),
                        radius = size.width * 0.9f
                    ),
                    center = Offset(size.width * 0.8f, size.height * 0.8f),
                    radius = size.width * 0.9f
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 32.dp, top = 8.dp)
            ) {
                // 1. App Header with Specs
                item {
                    AppHeader(deviceInfo = state.deviceInfo)
                }

                // 2. Primary Speed Dial Gauge Panel
                item {
                    BenchmarkGaugePanel(
                        state = state,
                        onStartClick = { viewModel.startBenchmark() },
                        onResetClick = { viewModel.resetToIdle() },
                        onCompareClick = { viewModel.toggleCompareSheet(true) }
                    )
                }

                // 3. Sub-Benchmark Progress Metrics Grid
                item {
                    Column {
                        Text(
                            text = "即時跑分分析",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        MetricsGrid(state = state)
                    }
                }

                // 4. Comparison Mini-Card
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF11172A)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(16.dp))
                    ) {
                        Row(
                            modifier = Modifier
                                .clickable { viewModel.toggleCompareSheet(true) }
                                .padding(16.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(Color(0xFFFFB300).copy(alpha = 0.15f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = "Rank",
                                        tint = Color(0xFFFFB300)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "手機效能排行榜",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "對比熱門旗艦機，看看誰更勝一籌",
                                        color = Color.Gray,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowRight,
                                contentDescription = "View detail",
                                tint = Color.LightGray
                            )
                        }
                    }
                }

                // 5. Test Run Log History Title
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "跑分歷史紀錄 (${state.benchmarkHistory.size})",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        if (state.benchmarkHistory.isNotEmpty()) {
                            TextButton(
                                onClick = { viewModel.clearAllHistory() },
                                colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFFF007F))
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("清除全部", fontSize = 12.sp)
                            }
                        }
                    }
                }

                // 6. Test Run Log History items
                if (state.benchmarkHistory.isEmpty()) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1224)),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color(0xFF1E293B).copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 32.dp, horizontal = 16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "Empty",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "尚未有跑分紀錄",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "點擊上方的「立即跑分」按鈕來進行手機性能跑分與網速評測！",
                                    color = Color.Gray,
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                            }
                        }
                    }
                } else {
                    items(state.benchmarkHistory) { record ->
                        HistoryRecordRow(record = record, onDelete = { viewModel.deleteRecord(record.id) })
                    }
                }
            }

            // Bottom Sheets / Overlays (Compare Sheet)
            if (state.showCompareSheet) {
                CompareMetricsDialog(
                    userScore = if (state.currentState == TestState.COMPLETED) state.overallScore else if (state.benchmarkHistory.isNotEmpty()) state.benchmarkHistory.first().overallScore else 0,
                    onDismiss = { viewModel.toggleCompareSheet(false) }
                )
            }
        }
    }
}

@Composable
fun AppHeader(deviceInfo: DeviceInfo) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF12182F)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(16.dp))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color(0xFF00FFCC).copy(alpha = 0.15f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Build,
                        contentDescription = "Chip Info",
                        tint = Color(0xFF00FFCC),
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "SpeedyPhone",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        fontFamily = FontFamily.Serif
                    )
                    Text(
                        text = "極致手機硬體跑分 & 測速",
                        color = Color(0xFF00FFCC),
                        fontWeight = FontWeight.Medium,
                        fontSize = 11.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
            Divider(color = Color(0xFF1E293B), thickness = 1.dp)
            Spacer(modifier = Modifier.height(14.dp))
            
            // Grid of device specifications
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SpecColumn(title = "裝置品牌 / 型號", value = "${deviceInfo.manufacturer} ${deviceInfo.model}", modifier = Modifier.weight(1f))
                SpecColumn(title = "Android 版本", value = "v${deviceInfo.androidVersion}", modifier = Modifier.weight(0.5f))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SpecColumn(title = "處理器核心", value = "${deviceInfo.processorCores} 核 CPU", modifier = Modifier.weight(1f))
                SpecColumn(title = "總配載 RAM", value = String.format("%.1f GB", deviceInfo.totalRamGb), modifier = Modifier.weight(0.5f))
            }
        }
    }
}

@Composable
fun SpecColumn(title: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(text = title, color = Color.Gray, fontSize = 10.sp)
        Text(text = value, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, maxLines = 1)
    }
}

@Composable
fun BenchmarkGaugePanel(
    state: BenchmarkUiState,
    onStartClick: () -> Unit,
    onResetClick: () -> Unit,
    onCompareClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0E1326)),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                border = BorderStroke(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        colors = if (state.currentState != TestState.IDLE && state.currentState != TestState.COMPLETED) {
                            listOf(Color(0xFF00FFCC), Color(0xFFFF007F))
                        } else {
                            listOf(Color(0xFF1E293B), Color(0xFF1E293B))
                        }
                    )
                ),
                shape = RoundedCornerShape(20.dp)
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Gauge header status
            val statusText = when (state.currentState) {
                TestState.IDLE -> "手機狀態良好 • 隨時可進行測試"
                TestState.CPU -> "正在評估 CPU 多核心運算性能..."
                TestState.RAM -> "正在測試 記憶體匯流排 吞吐速度..."
                TestState.STORAGE -> "正在進行 儲存空間快取 讀寫測試..."
                TestState.GPU -> "正在測量 GL圖形繪製 Frame Rate (FPS)..."
                TestState.NETWORK -> "正在檢測 行動網路 / Wi-Fi 連線速率..."
                TestState.COMPLETED -> "評測完成！綜合硬體性能得分為："
            }
            val statusColor = if (state.currentState == TestState.COMPLETED) Color(0xFF00FFCC) else if (state.currentState != TestState.IDLE) Color(0xFFFF007F) else Color.LightGray

            Text(
                text = statusText,
                color = statusColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Gauge Dial Canvas
            Box(
                modifier = Modifier
                    .size(220.dp)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                // Ticking/pulsing dial needle animation
                val scaleValue by animateFloatAsState(
                    targetValue = if (state.currentState != TestState.IDLE) 1.05f else 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1200, easing = LinearEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "pulse"
                )

                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .drawBehind {
                            // Dial background track
                            drawArc(
                                color = Color(0xFF1E293B),
                                startAngle = 140f,
                                sweepAngle = 260f,
                                useCenter = false,
                                style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                            )
                        }
                ) {
                    val angle = 140f + (state.liveGaugeValue / 100f) * 260f
                    // Glowing progress track
                    drawArc(
                        brush = Brush.sweepGradient(
                            colors = listOf(Color(0xFF00FFCC), Color(0xFFFF007F), Color(0xFF00FFCC)),
                            center = center
                        ),
                        startAngle = 140f,
                        sweepAngle = (state.liveGaugeValue / 100f) * 260f,
                        useCenter = false,
                        style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // Tick design
                    val numTicks = 30
                    val radius = size.width / 2f
                    for (i in 0..numTicks) {
                        val tickAngle = 140f + (i.toFloat() / numTicks) * 260f
                        val rad = Math.toRadians(tickAngle.toDouble())
                        val tickStart = Offset(
                            x = center.x + (radius - 22.dp.toPx()) * cos(rad).toFloat(),
                            y = center.y + (radius - 22.dp.toPx()) * sin(rad).toFloat()
                        )
                        val tickEnd = Offset(
                            x = center.x + (radius - 16.dp.toPx()) * cos(rad).toFloat(),
                            y = center.y + (radius - 16.dp.toPx()) * sin(rad).toFloat()
                        )
                        drawContext.canvas.drawLine(
                            p1 = tickStart,
                            p2 = tickEnd,
                            paint = androidx.compose.ui.graphics.Paint().apply {
                                color = if (tickAngle <= angle) Color(0xFF00FFCC).copy(alpha = 0.5f) else Color(0xFF334155)
                                strokeWidth = 1.dp.toPx()
                            }
                        )
                    }

                    // Meter needle pointer
                    val needleRad = Math.toRadians(angle.toDouble())
                    val needleLength = radius - 35.dp.toPx()
                    val needleEnd = Offset(
                        x = center.x + needleLength * cos(needleRad).toFloat(),
                        y = center.y + needleLength * sin(needleRad).toFloat()
                    )
                    // Needle cap
                    drawCircle(color = Color(0xFFFF007F), radius = 6.dp.toPx(), center = center)
                    drawLine(
                        color = Color(0xFFFF007F),
                        start = center,
                        end = needleEnd,
                        strokeWidth = 3.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }

                // Inner content inside the dial
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    if (state.currentState == TestState.IDLE) {
                        Text(
                            text = "READY",
                            color = Color.White,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "未跑分",
                            color = Color.Gray,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    } else if (state.currentState == TestState.COMPLETED) {
                        Text(
                            text = String.format("%,d", state.overallScore),
                            color = Color(0xFF00FFCC),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.testTag("overall_score_text")
                        )
                        Box(
                            modifier = Modifier
                                .background(Color(0xFF00FFCC).copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = when {
                                    state.overallScore >= 12000 -> "頂級旗艦"
                                    state.overallScore >= 8000 -> "中高階性能"
                                    state.overallScore >= 5000 -> "中階普通"
                                    else -> "入門基礎"
                                },
                                color = Color(0xFF00FFCC),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        // Display live speeds depending on active test state
                        val liveMainText = when (state.currentState) {
                            TestState.CPU -> state.cpuSpeed
                            TestState.RAM -> state.ramSpeed
                            TestState.STORAGE -> state.storageSpeed
                            TestState.GPU -> "${state.gpuFps} FPS"
                            TestState.NETWORK -> state.networkSpeed
                            else -> "Testing"
                        }
                        
                        Text(
                            text = liveMainText,
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = when (state.currentState) {
                                TestState.CPU -> "CPU FLOPS"
                                TestState.RAM -> "記憶體頻寬"
                                TestState.STORAGE -> "快取讀寫"
                                TestState.GPU -> "畫面更新率"
                                TestState.NETWORK -> "網路連線速度"
                                else -> "評測中"
                            },
                            color = Color.Gray,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Control Buttons
            if (state.currentState == TestState.IDLE) {
                Button(
                    onClick = onStartClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF007F)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(48.dp)
                        .testTag("run_benchmark_button"),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                ) {
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Run")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("立即跑分", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            } else if (state.currentState == TestState.COMPLETED) {
                Row(
                    modifier = Modifier.fillMaxWidth(0.9f),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onResetClick,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFF1E293B)),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Retry")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("重新測試", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onCompareClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00FFCC)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Icon(imageVector = Icons.Default.List, contentDescription = "Rank", tint = Color.Black)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("比對排名", color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                // Benchmarking actively
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(
                        color = Color(0xFF00FFCC),
                        strokeWidth = 3.dp,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "切勿在跑分期間關閉此應用程式",
                        color = Color.Gray,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
fun MetricsGrid(state: BenchmarkUiState) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            // CPU Card
            MetricInfoCard(
                title = "CPU 多核心運算",
                icon = Icons.Default.Home, // Simplified fallback representations
                progress = state.cpuProgress,
                score = state.cpuScore,
                desc = if (state.currentState == TestState.CPU) "正在分配核心算力..." else if (state.cpuScore > 0) "${state.cpuScore} 分 (${state.cpuSpeed})" else "未測試",
                accentColor = Color(0xFF2979FF),
                isActive = state.currentState == TestState.CPU,
                modifier = Modifier.weight(1f)
            ) {
                // Custom live visualization: Blinking chip cores
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    state.activeCpuCores.take(8).forEach { active ->
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(
                                    if (active) Color(0xFF2979FF) else Color(0xFF1E293B)
                                )
                        )
                    }
                }
            }

            // RAM Card
            MetricInfoCard(
                title = "RAM 記憶體吞吐",
                icon = Icons.Default.Share,
                progress = state.ramProgress,
                score = state.ramScore,
                desc = if (state.currentState == TestState.RAM) "正在高頻讀取記憶體..." else if (state.ramScore > 0) "${state.ramScore} 分 (${state.ramSpeed})" else "未測試",
                accentColor = Color(0xFF00FFCC),
                isActive = state.currentState == TestState.RAM,
                modifier = Modifier.weight(1f)
            ) {
                // High frequency linear visualizer
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .background(Color(0xFF1A2235), RoundedCornerShape(4.dp))
                ) {
                    val animatedWidth by animateFloatAsState(
                        targetValue = state.ramProgress,
                        animationSpec = spring(stiffness = Spring.StiffnessLow),
                        label = "ram_width"
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(animatedWidth)
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(Color(0xFF00FFCC).copy(alpha = 0.5f), Color(0xFF00FFCC))
                                ),
                                shape = RoundedCornerShape(4.dp)
                            )
                    )
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            // STORAGE Card
            MetricInfoCard(
                title = "快取與硬碟 讀寫",
                icon = Icons.Default.Menu,
                progress = state.storageProgress,
                score = state.storageScore,
                desc = if (state.currentState == TestState.STORAGE) state.storageSpeed else if (state.storageScore > 0) "${state.storageScore} 分" else "未測試",
                accentColor = Color(0xFFFFB300),
                isActive = state.currentState == TestState.STORAGE,
                modifier = Modifier.weight(1f)
            ) {
                // Storage write bar indicator
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(6.dp)
                            .background(
                                if (state.storageProgress > 0) Color(0xFFFFB300) else Color(0xFF1E293B),
                                RoundedCornerShape(3.dp)
                            )
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(6.dp)
                            .background(
                                if (state.storageProgress >= 0.5f) Color(0xFFFFB300) else Color(0xFF1E293B),
                                RoundedCornerShape(3.dp)
                            )
                    )
                }
            }

            // GPU Game Render Card
            MetricInfoCard(
                title = "圖形 FPS 渲染",
                icon = Icons.Default.Star,
                progress = state.gpuProgress,
                score = state.gpuScore,
                desc = if (state.currentState == TestState.GPU) "正在渲染 3D 物理多點粒子..." else if (state.gpuScore > 0) "${state.gpuScore} 分 (${state.gpuFps} FPS)" else "未測試",
                accentColor = Color(0xFFFF007F),
                isActive = state.currentState == TestState.GPU,
                modifier = Modifier.weight(1f)
            ) {
                // 3D Particles dynamic canvas rendering
                if (state.currentState == TestState.GPU) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(30.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF070B16))
                    ) {
                        // Drawing small moving objects
                        val bounceAnim by rememberInfiniteTransition(label = "").animateFloat(
                            initialValue = 0f,
                            targetValue = 1f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(1500, easing = LinearEasing),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "gpu_render"
                        )
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawCircle(
                                color = Color(0xFFFF007F),
                                radius = 4.dp.toPx(),
                                center = Offset(
                                    x = size.width * bounceAnim,
                                    y = size.height * (0.2f + 0.6f * bounceAnim)
                                )
                            )
                            drawCircle(
                                color = Color(0xFF00FFCC),
                                radius = 3.dp.toPx(),
                                center = Offset(
                                    x = size.width * (1f - bounceAnim),
                                    y = size.height * (0.8f - 0.5f * bounceAnim)
                                )
                            )
                        }
                    }
                } else {
                    Box(modifier = Modifier.height(1.dp))
                }
            }
        }

        // Network Benchmark Card
        MetricInfoCard(
            title = "連線傳輸速率與延遲",
            icon = Icons.Default.Warning,
            progress = state.networkProgress,
            score = state.networkScore,
            desc = if (state.currentState == TestState.NETWORK) "Downloading... Ping: ${String.format("%.0f", state.networkPing)}ms" else if (state.networkScore > 0) "${state.networkScore} 分 (${state.networkSpeed})" else "未測試",
            accentColor = Color(0xFFE040FB),
            isActive = state.currentState == TestState.NETWORK,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (state.currentState == TestState.NETWORK) {
                // Network visual signal wave animation
                val loadingTransition = rememberInfiniteTransition(label = "")
                val widthWave by loadingTransition.animateFloat(
                    initialValue = 0.2f,
                    targetValue = 1.0f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1000, easing = EaseInOutSine),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "network_wave"
                )
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(14.dp)
                ) {
                    val segments = 10
                    for (i in 0 until segments) {
                        val strokeHeight = size.height * (i.toFloat() / segments) * widthWave
                        drawLine(
                            color = Color(0xFFE040FB).copy(alpha = (i.toFloat() / segments)),
                            start = Offset(x = i * (size.width / segments), y = size.height),
                            end = Offset(x = i * (size.width / segments), y = size.height - strokeHeight),
                            strokeWidth = 3.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    }
                }
            } else {
                Box(modifier = Modifier.height(1.dp))
            }
        }
    }
}

@Composable
fun MetricInfoCard(
    title: String,
    icon: ImageVector,
    progress: Float,
    score: Int,
    desc: String,
    accentColor: Color,
    isActive: Boolean,
    modifier: Modifier = Modifier,
    additionalContent: @Composable () -> Unit = {}
) {
    val alphaAnim by rememberInfiniteTransition(label = "").animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "active_pulsing"
    )

    Card(
        colors = CardDefaults.cardColors(containerColor = if (isActive) Color(0xFF1A1F36) else Color(0xFF101524)),
        shape = RoundedCornerShape(14.dp),
        modifier = modifier
            .border(
                width = 1.dp,
                color = if (isActive) accentColor.copy(alpha = alphaAnim) else Color(0xFF1E293B).copy(alpha = 0.8f),
                shape = RoundedCornerShape(14.dp)
            )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    color = Color.LightGray,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
                if (isActive) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(accentColor, CircleShape)
                    )
                } else if (score > 0) {
                    Box(
                        modifier = Modifier
                            .background(accentColor.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = "PASS",
                            color = accentColor,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = desc,
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            additionalContent()
        }
    }
}

@Composable
fun HistoryRecordRow(
    record: BenchmarkRecord,
    onDelete: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1528)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(12.dp))
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(getScoreColor(record.overallScore).copy(alpha = 0.15f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (record.overallScore / 1000).toString() + "K",
                        color = getScoreColor(record.overallScore),
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = String.format("%,d 分", record.overallScore),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .background(getScoreColor(record.overallScore).copy(alpha = 0.15f), RoundedCornerShape(3.dp))
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = getRatingText(record.overallScore),
                                color = getScoreColor(record.overallScore),
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Text(
                        text = formatTimestamp(record.timestamp),
                        color = Color.Gray,
                        fontSize = 11.sp
                    )
                }
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier.testTag("delete_record_button_${record.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color.Gray.copy(alpha = 0.8f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun CompareMetricsDialog(
    userScore: Int,
    onDismiss: () -> Unit
) {
    // Elegant system specs comparison sheet dialog
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF0F152C),
        shape = RoundedCornerShape(20.dp),
        title = {
            Text(
                text = "📱 效能對比星級排行",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    text = "在此項目中，會將您的手機與市面上主流效能機型作客觀算力分數比對：",
                    color = Color.LightGray,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )

                val devices = listOf(
                    CompareDevice("您的手機", userScore, true),
                    CompareDevice("Snapdragon 8 Elite (旗艦天花板)", 15600, false),
                    CompareDevice("Apple A18 Pro (熱門旗艦)", 14300, false),
                    CompareDevice("Snapdragon 8 Gen 2 (高階效能機)", 10500, false),
                    CompareDevice("Dimensity 8200 (中階主流款)", 7200, false),
                    CompareDevice("Helio G99 (入門基礎手機)", 3300, false)
                ).sortedByDescending { it.score }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    devices.forEach { device ->
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = device.name + if (device.isUser) " (本機)" else "",
                                    color = if (device.isUser) Color(0xFF00FFCC) else Color.White,
                                    fontWeight = if (device.isUser) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = String.format("%,d 分", device.score),
                                    color = if (device.isUser) Color(0xFF00FFCC) else Color.LightGray,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            // Progress bar
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .background(Color(0xFF1E293B), RoundedCornerShape(4.dp))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .fillMaxWidth(device.score / 16000f)
                                        .background(
                                            if (device.isUser) Color(0xFF00FFCC) else Color(0xFFFF007F).copy(alpha = 0.5f),
                                            RoundedCornerShape(4.dp)
                                        )
                                )
                            }
                        }
                    }
                }

                if (userScore > 0) {
                    val beatPercent = when {
                        userScore >= 15000 -> 99
                        userScore >= 12000 -> 95
                        userScore >= 10000 -> 88
                        userScore >= 8000 -> 76
                        userScore >= 5000 -> 45
                        else -> 12
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF00FFCC).copy(alpha = 0.1f), RoundedCornerShape(10.dp))
                            .border(1.dp, Color(0xFF00FFCC).copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "🎉 評測報告：您的手機效能擊敗了全球約 ${beatPercent}% 的 Android 裝置！運行主流複雜 3D 遊戲與多工處理皆游刃有餘。",
                            color = Color(0xFF10F2C2),
                            fontSize = 12.sp,
                            lineHeight = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF007F)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("關閉視窗", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    )
}

data class CompareDevice(val name: String, val score: Int, val isUser: Boolean)

// Helper functions for scoring styles
fun getScoreColor(score: Int): Color {
    return when {
        score >= 12000 -> Color(0xFF00FFCC) // cyan
        score >= 8000 -> Color(0xFF2979FF)  // cobalt blue
        score >= 5000 -> Color(0xFFFFB300)  // amber
        else -> Color(0xFFFF007F)           // magenta
    }
}

fun getRatingText(score: Int): String {
    return when {
        score >= 12000 -> "極致流暢"
        score >= 8000 -> "性能卓越"
        score >= 5000 -> "中規中矩"
        else -> "入門配置"
    }
}

fun formatTimestamp(timestamp: Long): String {
    val date = Date(timestamp)
    val format = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())
    return format.format(date)
}
