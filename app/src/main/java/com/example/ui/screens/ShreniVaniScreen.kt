package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.LiveAiBadge
import com.example.ui.components.MotifBand
import com.example.ui.theme.ShreniBorder
import com.example.ui.theme.ShreniGoldAccent
import com.example.ui.theme.ShreniMaroonSecondary
import com.example.ui.theme.ShreniSuccessGreen
import com.example.ui.theme.ShreniTerracottaPrimary
import com.example.viewmodel.ShreniScanViewModel
import com.example.viewmodel.ShreniScreen
import kotlinx.coroutines.delay

enum class VaniPhase {
    IDLE,
    LISTENING,
    THINKING,
    READY
}

data class VoiceLanguageSample(
    val code: String,
    val label: String,
    val transcript: String,
    val translation: String
)

val VOICE_LANGUAGES = listOf(
    VoiceLanguageSample(
        code = "mr",
        label = "मराठी (Marathi)",
        transcript = "ही माझ्या हाताने विणलेली पैठणी शैलीतील पारंपरिक साडी आहे, शुद्ध रेशीम आणि सोनेरी जरी वापरून तयार केली आहे.",
        translation = "This is a handmade Paithani-style traditional saree handwoven using pure silk and fine golden zari threads."
    ),
    VoiceLanguageSample(
        code = "hi",
        label = "हिंदी (Hindi)",
        transcript = "यह मेरे हाथ से बनाया गया टेराकोटा दीपक है, नदी की शुद्ध चिकनी मिट्टी से पारंपरिक चाक पर बनाया गया है।",
        translation = "This is a handcrafted terracotta oil lamp molded on a traditional potter's wheel using pure river clay."
    ),
    VoiceLanguageSample(
        code = "en",
        label = "English",
        transcript = "This is a handmade Paithani-inspired traditional textile created using pure silk and gold zari on a pit loom.",
        translation = "Authentic handloom textile created with generational weaving knowledge and certified natural dyes."
    )
)

@Composable
fun ShreniVaniScreen(
    viewModel: ShreniScanViewModel
) {
    var selectedLang by remember { mutableStateOf(VOICE_LANGUAGES[0]) }
    var phase by remember { mutableStateOf(VaniPhase.IDLE) }

    // Pulsing animation for microphone
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    LaunchedEffect(phase) {
        if (phase == VaniPhase.LISTENING) {
            delay(2200)
            phase = VaniPhase.THINKING
        } else if (phase == VaniPhase.THINKING) {
            delay(1500)
            phase = VaniPhase.READY
            viewModel.updateVoiceDescription(selectedLang.transcript)
        }
    }

    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp
            ) {
                Column {
                    MotifBand()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { viewModel.navigateBack() },
                            modifier = Modifier.testTag("vani_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Shreni Vani",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Speak naturally in your native language",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        }
                        LiveAiBadge(label = "Voice AI")
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Language selection row
            Column {
                Text(
                    text = "Select Language",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    VOICE_LANGUAGES.forEach { lang ->
                        val isSelected = selectedLang.code == lang.code
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) ShreniTerracottaPrimary else MaterialTheme.colorScheme.surface
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) ShreniTerracottaPrimary else ShreniBorder,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    selectedLang = lang
                                    viewModel.setSelectedLanguage(lang.code)
                                    phase = VaniPhase.IDLE
                                }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = lang.label.split(" ")[0],
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // Big pulsing mic card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, ShreniBorder, RoundedCornerShape(24.dp))
                    .padding(vertical = 36.dp, horizontal = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .scale(if (phase == VaniPhase.LISTENING) pulseScale else 1f)
                            .clip(CircleShape)
                            .background(
                                when (phase) {
                                    VaniPhase.LISTENING -> ShreniMaroonSecondary
                                    VaniPhase.THINKING -> ShreniGoldAccent
                                    VaniPhase.READY -> ShreniSuccessGreen
                                    else -> ShreniTerracottaPrimary
                                }
                            )
                            .clickable {
                                if (phase == VaniPhase.IDLE || phase == VaniPhase.READY) {
                                    phase = VaniPhase.LISTENING
                                }
                            }
                            .testTag("vani_mic_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        if (phase == VaniPhase.THINKING) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(48.dp),
                                color = Color.White,
                                strokeWidth = 3.dp
                            )
                        } else {
                            Icon(
                                imageVector = if (phase == VaniPhase.READY) Icons.Default.Check else Icons.Default.Mic,
                                contentDescription = "Microphone",
                                tint = Color.White,
                                modifier = Modifier.size(52.dp)
                            )
                        }
                    }

                    Text(
                        text = when (phase) {
                            VaniPhase.IDLE -> "Tap microphone to describe your craft"
                            VaniPhase.LISTENING -> "Shreni Vani is listening… Speak now"
                            VaniPhase.THINKING -> "Understanding & translating your description…"
                            VaniPhase.READY -> "Craft description captured successfully!"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = "Artisans can speak freely in Marathi, Hindi, or English. AI automatically translates and enriches product details.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }

            // Transcription & Translation output
            if (phase == VaniPhase.READY) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .border(1.dp, ShreniBorder, RoundedCornerShape(20.dp))
                        .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Detected Language:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = selectedLang.label,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = ShreniTerracottaPrimary
                            )
                        }

                        Text(
                            text = "Original Voice Transcript:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = selectedLang.transcript,
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = "AI Translation & Storytelling:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = selectedLang.translation,
                            fontSize = 13.sp,
                            lineHeight = 19.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Button(
                    onClick = {
                        viewModel.updateVoiceDescription(selectedLang.transcript)
                        viewModel.navigateTo(ShreniScreen.SCAN_HOME)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("vani_continue_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = ShreniTerracottaPrimary),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "Continue to Shreni Scan with this Voice Note",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
