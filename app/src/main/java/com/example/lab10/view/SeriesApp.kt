package com.example.lab10.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeriesApp(series: SeriesViewModel = viewModel()) {
    val nav = rememberNavController()
    val entry by nav.currentBackStackEntryAsState()
    val route = entry?.destination?.route ?: "inicio"
    val editor = route.startsWith("serie") && route != "series"
    val state by series.state.collectAsStateWithLifecycle()
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Series App", fontWeight = FontWeight.Bold) }, navigationIcon = {
                if (editor) IconButton(onClick = { nav.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver") }
            })
        },
        bottomBar = {
            if (!editor) NavigationBar {
                NavigationBarItem(selected = route == "inicio", onClick = { nav.navigate("inicio") { popUpTo("inicio"); launchSingleTop = true } }, icon = { Icon(Icons.Default.Home, "Inicio") }, label = { Text("Inicio") })
                NavigationBarItem(selected = route == "series", onClick = { nav.navigate("series") { popUpTo("inicio"); launchSingleTop = true } }, icon = { Icon(Icons.Default.Favorite, "Series") }, label = { Text("Series") })
            }
        },
        floatingActionButton = {
            if (route == "series") ExtendedFloatingActionButton(onClick = { nav.navigate("serieNuevo") }, icon = { Icon(Icons.Default.Add, "Añadir") }, text = { Text("Nueva serie") })
        },
    ) { padding ->
        NavHost(navController = nav, startDestination = "inicio", modifier = Modifier.padding(padding)) {
            composable("inicio") {
                Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    Text("Tus series,\nen un solo lugar.", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
                    Text("Consulta tu colección, añade estrenos y actualiza los datos de tus series.", style = MaterialTheme.typography.bodyLarge)
                    Button(onClick = { nav.navigate("series") }, modifier = Modifier.fillMaxWidth()) { Text("Ver series") }
                    HorizontalDivider()
                    Text("Laboratorio 10", style = MaterialTheme.typography.titleMedium)
                    Text("Kotlin · Jetpack Compose · Retrofit\nConectado a la API de Django.", style = MaterialTheme.typography.bodyMedium)
                }
            }
            composable("series") {
                LaunchedEffect(Unit) { series.refresh(state.notice) }
                ContenidoSeriesListado(state, { series.refresh() }, { nav.navigate("serieVer/$it") }, series::requestDelete)
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
