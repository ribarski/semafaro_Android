package com.example.semafaro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.*

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

enum class EstadoSemaforo {
    VERMELHO, AMARELO, VERDE, AMARELO_PISCANTE
}

@Composable
fun SemaforoApp() {
    var estadoAtual by remember { mutableStateOf(EstadoSemaforo.VERMELHO) }
    var modoPiscante by remember { mutableStateOf(false) }
    var luzVisivel by remember { mutableStateOf(true) }
    var progresso by remember { mutableStateOf(0f) }

    val tamanhoTraco = 10f
    val tamanhoEspaco = 20f

    val duracaoVermelho = 3000
    val duracaoVerde = 5000
    val duracaoAmarelo = 2000

    val scope = rememberCoroutineScope()
    var job by remember { mutableStateOf<Job?>(null) }

    // Inicia a animação automaticamente
    LaunchedEffect(Unit) {
        job = scope.launch {
            while (true) {
                estadoAtual = EstadoSemaforo.VERMELHO
                for (i in 0..100) {
                    progresso = i / 100f
                    delay(duracaoVermelho / 100L)
                }

                estadoAtual = EstadoSemaforo.VERDE
                for (i in 0..100) {
                    progresso = i / 100f
                    delay(duracaoVerde / 100L)
                }

                estadoAtual = EstadoSemaforo.AMARELO
                for (i in 0..100) {
                    progresso = i / 100f
                    delay(duracaoAmarelo / 100L)
                }
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
                LuzSemaforo(
                    corLuz = Color.Red,
                    ativa = !modoPiscante && estadoAtual == EstadoSemaforo.VERMELHO,
                    progresso = progresso,
                    mostrarProgresso = true,
                    tamanhoTraco = tamanhoTraco,
                    tamanhoEspaco = tamanhoEspaco,
                    estadoAtual = estadoAtual
                )

                LuzSemaforo(
                    corLuz = Color.Yellow,
                    ativa = (!modoPiscante && estadoAtual == EstadoSemaforo.AMARELO) ||
                            (modoPiscante && luzVisivel),
                    progresso = if (!modoPiscante && estadoAtual == EstadoSemaforo.AMARELO) progresso else 0f,
                    mostrarProgresso = !modoPiscante || estadoAtual != EstadoSemaforo.AMARELO_PISCANTE,
                    tamanhoTraco = tamanhoTraco,
                    tamanhoEspaco = tamanhoEspaco,
                    estadoAtual = estadoAtual
                )

                LuzSemaforo(
                    corLuz = Color.Green,
                    ativa = !modoPiscante && estadoAtual == EstadoSemaforo.VERDE,
                    progresso = progresso,
                    mostrarProgresso = true,
                    tamanhoTraco = tamanhoTraco,
                    tamanhoEspaco = tamanhoEspaco,
                    estadoAtual = estadoAtual
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                modoPiscante = !modoPiscante
                progresso = 0f

                job?.cancel()

                job = scope.launch {
                    if (modoPiscante) {
                        estadoAtual = EstadoSemaforo.AMARELO_PISCANTE
                        while (true) {
                            luzVisivel = !luzVisivel
                            delay(500)
                        }
                    } else {
                        while (true) {
                            estadoAtual = EstadoSemaforo.VERMELHO
                            for (i in 0..100) {
                                progresso = i / 100f
                                delay(duracaoVermelho / 100L)
                            }

                            estadoAtual = EstadoSemaforo.VERDE
                            for (i in 0..100) {
                                progresso = i / 100f
                                delay(duracaoVerde / 100L)
                            }

                            estadoAtual = EstadoSemaforo.AMARELO
                            for (i in 0..100) {
                                progresso = i / 100f
                                delay(duracaoAmarelo / 100L)
                            }
                        }
                    }
                }
            }
        ) {
            Text(text = if (modoPiscante) "Voltar ao Modo Normal" else "Ativar Amarelo Piscante")
        }
    }
}

@Composable
fun LuzSemaforo(
    corLuz: Color,
    ativa: Boolean,
    progresso: Float,
    mostrarProgresso: Boolean = true,
    tamanhoTraco: Float,
    tamanhoEspaco: Float,
    estadoAtual: EstadoSemaforo
) {
    val fundo = Color.Black

    val intensidade = when {
        ativa && estadoAtual == EstadoSemaforo.AMARELO_PISCANTE -> 1f
        ativa -> 0.4f
        else -> 0f
    }

    Box(
        modifier = Modifier
            .size(80.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(fundo)
        )

        if (intensidade > 0f) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(corLuz.copy(alpha = intensidade))
            )
        }

        if (ativa && mostrarProgresso) {
            Canvas(modifier = Modifier.size(80.dp)) {
                val strokeWidth = 6.dp.toPx()
                val padding = strokeWidth / 2 + 6.dp.toPx()

                val tracejado = PathEffect.dashPathEffect(
                    floatArrayOf(tamanhoTraco, tamanhoEspaco),
                    0f
                )

                drawArc(
                    color = corLuz,
                    startAngle = -90f,
                    sweepAngle = 360 * progresso,
                    useCenter = false,
                    topLeft = Offset(padding, padding),
                    size = Size(size.width - 2 * padding, size.height - 2 * padding),
                    style = Stroke(
                        width = strokeWidth,
                        cap = StrokeCap.Round,
                        pathEffect = tracejado
                    )
                )
            }
        }
    }
}
