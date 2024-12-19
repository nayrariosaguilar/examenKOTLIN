package ipar.cri0625

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import app.cash.sqldelight.db.SqlDriver
import ipar.cri0625.data.Database
import androidx.navigation.NavController
import androidx.navigation.NavHost
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.foundation.lazy.items

import ipar.cri0625.data.Espectacle
import kotlinx.serialization.Serializable
import nayriosprojecte.composeapp.generated.resources.Res
import nayriosprojecte.composeapp.generated.resources.compose_multiplatform

object DatabaseConfig {
    val name: String = "granTeatre.db"
    val development: Boolean = true
}
@Composable
@Preview
fun App(sqlDriver: SqlDriver) {
    val database = Database(sqlDriver)
    Database.Schema.create(sqlDriver)

    if(DatabaseConfig.development){
       insertDatabase(database)
    }
    MaterialTheme {
        val controller = rememberNavController()
        NavHost(controller, startDestination = HomeRoute){
            composable<HomeRoute>{ HomeScreen(controller) }
            composable<EspectacleRoute>{ EspectacleScreen(database, controller) }
            composable<ZonaRoute>{ ZonaScreen(database, controller) }
            composable<ButacaRoute>{ ButacaScreen(database, controller) }
            composable<SessionRoute>{ SessionScreen(database, controller) }
        }

    }
}

@Serializable
object HomeRoute
@Serializable
object EspectacleRoute
@Serializable
object ZonaRoute
@Serializable
object ButacaRoute
@Serializable
object SessionRoute



fun insertDatabase(db: Database){
    //CREACIÓN DE TODOS LOS INSERTS CON ORDEN DE CREACIÓN
   db.espectacleQueries.insert("Gran espectacle","descripcion")
   db.sessionQueries.insert(12,1)
   db.preuQueries.insert("12.99",1,1)
    db.zonaQueries.insert("zona1")
    db.butacaQueries.insert(1)
    db.entradaQueries.insert(1,1)
}

@Composable
fun EspectacleScreen(db: Database, controller: NavController){
    val espectacles = db.espectacleQueries.select().executeAsList()
    Column {
        Nav(controller)
        LazyColumn {
            items(espectacles){ espectacle ->
                Text(espectacle.nom)
                Text(espectacle.description.toString())
            } }

    }
}

@Composable
fun ZonaScreen(db: Database, controller: NavController){
    val zonas = db.zonaQueries.select().executeAsList()
    Column {
        Nav(controller)
        LazyColumn {
            items(zonas){ zona ->
                Text(zona.nom)
            } }

    }
}
@Composable
fun ButacaScreen(db: Database, controller: NavController){
    val butacas = db.butacaQueries.select().executeAsList()
    Column {
        Nav(controller)
        LazyColumn {
            items(butacas){ butaca ->
                Text("La zona assignada es: ")
                Text(butaca.zona.toString())
            } }

    }
}
@Composable
fun SessionScreen(db: Database, controller: NavController){
    val sessions = db.sessionQueries.select().executeAsList()
    Column {
        Nav(controller)
        LazyColumn {
            items(sessions){ session ->
               Text("el codigo del espectaculo es: ")
                Text(session.codi.toString())
            } }

    }
}


@Composable
fun Nav(controller: NavController){
    TopAppBar(
        title = {Text("Barra navegació")},
        actions = {
            IconButton(onClick = {controller.navigate(HomeRoute)}){
                Icon(imageVector = Icons.Filled.Home, contentDescription = "Home")
            }
        }
    )
}
@Composable
fun HomeScreen(controller: NavController){
    Column{
        Nav(controller)
        Text("Espectaculos")
        Button(onClick = {controller.navigate(EspectacleRoute)}){
            Text("ListarEspectaculos")
        }
        Button(onClick = {controller.navigate(EspectacleRoute)}){
            Text("ListarZona")
        }
        Button(onClick = {controller.navigate(EspectacleRoute)}){
            Text("ListarButaca")
        }
        Button(onClick = {controller.navigate(EspectacleRoute)}){
            Text("ListarSession")
        }
    }
}