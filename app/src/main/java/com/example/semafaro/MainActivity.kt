package com.example.semafaro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.semafaro.ui.theme.SemafaroTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SemaforoApp()
                }
            }
        }
    }
}

// Estados possíveis do semáforo
enum class EstadoSemaforo {
    VERMELHO,
    AMARELO,
    VERDE,
    AMARELO_PISCANTE
}

@Composable
fun SemaforoApp() {
    // Estado atual do semáforo
    var estadoAtual by remember { mutableStateOf(EstadoSemaforo.VERMELHO) }

    // Controla se o modo normal ou piscante está ativo
    var modoPiscante by remember { mutableStateOf(false) }

    // Estado para controlar a visibilidade da luz no modo piscante
    var luzVisivel by remember { mutableStateOf(true) }

    // Escopo de corrotina para o Composable
    val escopo = rememberCoroutineScope()

    // Efeito para alternar os estados do semáforo no modo normal
    LaunchedEffect(modoPiscante) {
        if (!modoPiscante) {
            // Ciclo normal do semáforo
            while (true) {
                estadoAtual = EstadoSemaforo.VERMELHO
                delay(3000) // Vermelho por 3 segundos

                estadoAtual = EstadoSemaforo.VERDE
                delay(5000) // Verde por 5 segundos

                estadoAtual = EstadoSemaforo.AMARELO
                delay(2000) // Amarelo por 2 segundos
            }
        } else {
            // Modo amarelo piscante
            estadoAtual = EstadoSemaforo.AMARELO_PISCANTE

            // Corrotina para piscar a luz amarela
            while (true) {
                luzVisivel = !luzVisivel
                delay(500) // Pisca a cada 0.5 segundos
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Container do semáforo
        Box(
            modifier = Modifier
                .width(120.dp)
                .height(300.dp)
                .background(Color.DarkGray)
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Luz vermelha
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                modoPiscante -> Color.Gray // Desligado no modo piscante
                                estadoAtual == EstadoSemaforo.VERMELHO -> Color.Red
                                else -> Color.Gray
                            }
                        )
                )

                // Luz amarela
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                modoPiscante && luzVisivel -> Color.Yellow
                                estadoAtual == EstadoSemaforo.AMARELO -> Color.Yellow
                                else -> Color.Gray
                            }
                        )
                )

                // Luz verde
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                modoPiscante -> Color.Gray // Desligado no modo piscante
                                estadoAtual == EstadoSemaforo.VERDE -> Color.Green
                                else -> Color.Gray
                            }
                        )
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Botão para alternar entre modo normal e piscante
        Button(
            onClick = {
                modoPiscante = !modoPiscante
            }
        ) {
            Text(
                text = if (modoPiscante) "Voltar ao Modo Normal" else "Ativar Amarelo Piscante"
            )
        }
    }
}