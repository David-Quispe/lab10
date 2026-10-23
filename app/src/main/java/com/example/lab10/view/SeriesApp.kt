package com.example.lab10.view

import android.content.Context
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.lab10.R
import com.example.lab10.ui.theme.Lab10Theme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeriesApp(series: SeriesViewModel = viewModel()) {
    val context = LocalContext.current
    val preferences = remember { context.getSharedPreferences("series_appearance", Context.MODE_PRIVATE) }
    var dark by rememberSaveable { mutableStateOf(preferences.getBoolean("dark", true)) }
    val nav = rememberNavController()
    val entry by nav.currentBackStackEntryAsState()
    val route = entry?.destination?.route ?: "inicio"
    val editor = route.startsWith("serie") && route != "series"
    val state by series.state.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) { series.refresh() }
    Lab10Theme(darkTheme = dark) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            if (!editor) Icon(painterResource(R.drawable.ic_collection), null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(23.dp))
                            Text("series.", fontSize = 26.sp, fontWeight = FontWeight.Bold, letterSpacing = (-1).sp)
                        }
                    },
                    navigationIcon = {
                        if (editor) IconButton(onClick = { nav.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            dark = !dark
                            preferences.edit().putBoolean("dark", dark).apply()
                        }) {
                            Icon(
                                painterResource(if (dark) R.drawable.ic_theme_sun else R.drawable.ic_theme_moon),
                                if (dark) "Activar tema claro" else "Activar tema oscuro",
                                modifier = Modifier.size(22.dp),
                            )
                        }
                        Spacer(Modifier.width(10.dp))
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
                )
            },
            bottomBar = {
                if (!editor) Column {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    NavigationBar(containerColor = MaterialTheme.colorScheme.background, tonalElevation = 0.dp) {
                        NavigationBarItem(
                            selected = route == "inicio",
                            onClick = { nav.navigate("inicio") { popUpTo("inicio"); launchSingleTop = true } },
                            icon = { Icon(painterResource(R.drawable.ic_home), null) },
                            label = { Text("Inicio") },
                        )
                        NavigationBarItem(
                            selected = route == "series",
                            onClick = { nav.navigate("series") { popUpTo("inicio"); launchSingleTop = true } },
                            icon = { Icon(painterResource(R.drawable.ic_collection), null) },
                            label = { Text("Colección") },
                        )
                    }
                }
            },
        ) { padding ->
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.TopCenter) {
                NavHost(
                    navController = nav, startDestination = "inicio",
                    modifier = Modifier.widthIn(max = 760.dp).fillMaxWidth().fillMaxHeight(),
                    enterTransition = { fadeIn(tween(180)) },
                    exitTransition = { fadeOut(tween(120)) },
                ) {
                    composable("inicio") {
                        ScreenInicio(state,
                            onCollection = { nav.navigate("series") },
                            onNew = { nav.navigate("serieNuevo") },
                            onEdit = { nav.navigate("serieVer/$it") },
                            onRetry = { series.refresh() },
                        )
                    }
                    composable("series") {
                        LaunchedEffect(Unit) { series.refresh(state.notice) }
                        ContenidoSeriesListado(state,
                            onRefresh = { series.refresh() },
                            onEdit = { nav.navigate("serieVer/$it") },
                            onDelete = series::requestDelete,
                            onCreate = { nav.navigate("serieNuevo") },
                        )
                        ContenidoSerieEliminar(state, series::delete, series::cancelDelete)
                    }
                    composable("serieNuevo") {
                        ContenidoSerieEditar(0, onSaved = {
                            series.refresh("Serie registrada correctamente.")
                            nav.popBackStack()
                        }, onCancel = { nav.popBackStack() })
                    }
                    composable("serieVer/{id}", arguments = listOf(navArgument("id") { type = NavType.IntType })) { destination ->
                        ContenidoSerieEditar(destination.arguments?.getInt("id") ?: 0, onSaved = {
                            series.refresh("Serie actualizada correctamente.")
                            nav.popBackStack()
                        }, onCancel = { nav.popBackStack() })
                    }
                }
            }
        }
    }
}
