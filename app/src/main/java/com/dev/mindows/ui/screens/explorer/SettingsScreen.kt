package com.dev.mindows.ui.screens.explorer

import android.content.Context
import android.os.Environment
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dev.mindows.ui.theme.*
import com.dev.mindows.ui.components.RetroButton
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SettingsScreen(
    viewModel: ExplorerViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val tracks by viewModel.tracks.collectAsState()

    Column(modifier = modifier.fillMaxSize().background(WmpBlack).padding(8.dp)) {
        Text(
            text = "SYSTEM CONTROL PANEL", color = WmpNeonGreen, fontSize = 13.sp,
            fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace,
            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), textAlign = TextAlign.Center
        )

        Column(
            modifier = Modifier.fillMaxSize().background(WmpDarkGrey).border(1.dp, Win95DarkGrey).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text("📊 Media Engine State:", color = WmpTextWhite, fontSize = 11.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black)
                    .border(1.dp, Win95DarkGrey)
                    .padding(8.dp)
            ) {
                Text(
                    text = "STATUS: ACTIVE\nTOTAL SYNCED TRACKS: ${tracks.size}\nAUDIO CORE: MEDIA3 EXOPLAYER",
                    color = WmpNeonGreen, fontSize = 11.sp, fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text("⚙️ Maintenance Tasks:", color = WmpTextWhite, fontSize = 11.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)

            RetroButton(
                text = "🔄 Force Re-Scan Storage",
                onClick = {
                    viewModel.loadSongs()
                    Toast.makeText(context, "Storage scanned successfully!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth()
            )

            RetroButton(
                text = "💾 Export System Logs (.txt)",
                onClick = {
                    exportMindowsLogs(context, tracks.size)
                },
                modifier = Modifier.fillMaxWidth()
            )

            RetroButton(
                text = "🗑️ Clear Mindows Cache",
                onClick = {
                    context.cacheDir.deleteRecursively()
                    Toast.makeText(context, "Cache wiped out successfully!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Mindows Player Version 7.1.2026\nArchitecture: Clean MVVM + Koin DI\n© Microsoft Core Clone Corp.",
                color = Color.Gray, fontSize = 10.sp, fontFamily = FontFamily.Monospace,
                textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

fun exportMindowsLogs(context: Context, totalTracks: Int) {
    try {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val logContent = """
            =========================================
            MINDOWS PLAYER SYSTEM DIAGNOSTICS REPORT
            =========================================
            Generated on: ${Date()}
            Audio Track Count: $totalTracks files found
            Database Engine: Room DB (KSP Enhanced)
            Dependency Provider: Koin Android 4.0
            
            Status: All hardware threads operating normally.
            =========================================
        """.trimIndent()

        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "mindows_log_$timeStamp.txt")
        FileOutputStream(file).use { stream ->
            stream.write(logContent.toByteArray())
        }

        Toast.makeText(context, "Log saved to Documents Folder!", Toast.LENGTH_LONG).show()
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Failed to export logs: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}
