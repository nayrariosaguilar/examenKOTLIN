package ipar.cri0625

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.bson.types.ObjectId
import org.bson.Document
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
import com.mongodb.kotlin.client.coroutine.MongoClient
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import com.mongodb.MongoException
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import jdk.internal.misc.Signal.handle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import nayriosprojecte.composeapp.generated.resources.Res
import nayriosprojecte.composeapp.generated.resources.compose_multiplatform
import org.bson.BsonInt64
import org.bson.codecs.pojo.annotations.BsonId

object DatabaseConfig {
    val name: String = "Login.db"
    val development: Boolean = true
}
@Composable
@Preview
fun App(sqlDriver: SqlDriver) {
    val databaseSqlite = Database(sqlDriver)
    Database.Schema.create(sqlDriver)

    var database: MongoDatabase? by remember { mutableStateOf(null) }
    var error: String? by remember { mutableStateOf(null) }

    if(DatabaseConfig.development){
        loginUser(databaseSqlite)
    }

    DisposableEffect(Unit) {
        CoroutineScope(Dispatchers.Default).launch {
            try {
                val connectionString = "mongodb+srv://nayrios2004:Adri888@prueba.7rxv1.mongodb.net/?retryWrites=true&w=majority&appName=Prueba"
                database = setupConnection(connectionString, "test")
            } catch (me: MongoException) {
                error = me.message
            }
        }
        onDispose { }
    }
    MaterialTheme {
        val controller = rememberNavController()
        NavHost(controller, startDestination = HomeRoute){
            composable<HomeRoute>{ HomeScreen(controller) }


        }

    }
}

@Serializable
object HomeRoute
@Serializable
object Login
@Serializable
object AñadirURL
@Serializable
object VerURL

fun loginUser(db: Database){
    db.loginQueries.insert("nayrios2004", "Adri888")
}

data class URL(
    @BsonId
    val id: ObjectId,
    val url: String
)
//CONEXION
suspend fun setupConnection(connectionString: String, databaseName: String): MongoDatabase {
    val client = MongoClient.create(connectionString)
    val database: MongoDatabase = client.getDatabase(databaseName)
    val command = Document("ping", BsonInt64(1))
    try {
        database.runCommand(command)
        println("Connected to database")
    } catch (e: Exception) {
        println("Error connecting to database")
        throw e
    }
    return database
}
//INSERTA VER URLS
@Composable
fun ButacaScreen(db: Database, controller: NavController) {
    val butacas = db.butacaQueries.select().executeAsList()
    Column {
        Nav(controller)
        LazyColumn {
            items(butacas) { butaca ->
                Text("La numero de butaca es: ")
                Text(butaca.numero.toString())
            }
        }

    }
//}
//@Composable
//fun SessionScreen(db: Database, controller: NavController){
//    val sessions = db.sessionQueries.select().executeAsList()
//    Column {
//        Nav(controller)
//        LazyColumn {
//            items(sessions){ session ->
//               Text("el dia de la session es: ")
//                Text(session.dia.toString())
//            } }
//    }
//}
//
//@Composable
//fun PrecioScreen(db: Database, controller: NavController){
//
//    val preus = db.preuQueries.select().executeAsList()
//
//    Column {
//        Nav(controller)
//        LazyColumn {
//            items(preus){ preu ->
//                Text("el precio de tu espectaculo es: ")
//                Text(preu.preu)
//            } }
//    }
//}
//
//@Composable
//fun CompraEntradaScreen(db: Database, controller: NavController){
//    val zonas = db.zonaQueries.select().executeAsList()
//    val espectacles= db.espectacleQueries.select().executeAsList()
//    val preus = db.preuQueries.select().executeAsList()
//    //AQUI PONDRE LA INFORMACIÓN DE LA ENTRADA
//    val entradas = db.entradaQueries.select().executeAsList()
//
//    Column {
//        Nav(controller)
//
//
//        LazyColumn {
//            items(entradas){ entrada ->
//                Text("La numero de TU butaca es: ")
//                Text(entrada.butaca.toString())
//                Text("La numero de TU session es: ")
//                Text(entrada.sessio.toString())
//            } }
//        LazyColumn {
//            items(espectacles){ espectacle ->
//                Text("Detalles del espectaculo ")
//                Text(espectacle.nom)
//                Text(espectacle.description.toString())
//            } }
//        LazyColumn {
//            items(preus){ preu ->
//                Text("el precio de tu espectaculo es: ")
//                Text(preu.preu)
//            } }
//        LazyColumn {
//            items(zonas){ zona ->
//                Text("La zona assignada es: ")
//                Text(zona.nom)
//            } }
//    }
//}
//@Composable
//fun PrecioScreen2(db: Database, controller: NavController){
//    val preusdb = db.preuQueries.select().executeAsList()
//    Column {
//        Nav(controller)
//        val sessio = remember {
//            mutableStateOf("")
//        }
//        val zona = remember { mutableStateOf("") }
//        val result = remember { mutableStateOf("") }
//        Row {
//            TextField(
//                value = sessio.value,
//                placeholder = { Text("Introduce la sessión") },
//                onValueChange = { sessio.value = it },
//                modifier = Modifier.fillMaxWidth()
//            )
//        }
//        Row {
//            TextField(
//                value = zona.value,
//                placeholder = { Text("Introduce la zona") },
//                onValueChange = { zona.value = it },
//                modifier = Modifier.fillMaxWidth()
//            )
//        }
//
//        androidx.compose.material3.Button(
//            onClick = {
//                val sessioId = sessio.value.toLongOrNull()
//                val zonaId = zona.value.toLongOrNull()
//                if(sessioId!=null && zonaId != null){
//                    val preuEncontrado = preusdb.find { it.sessio==sessioId && it.zona==zonaId }
//                    result.value = "el precio es: $preuEncontrado"
//                }
//            },
//            modifier = Modifier.padding(vertical = 40.dp)
//        ) {
//            androidx.compose.material3.Text("go")
//        }
//        Spacer(modifier = Modifier.height(10.dp))
//
//        androidx.compose.material3.Text(text = result.value)
//    }
//}
//@Composable
//fun CompraEntrada(db: Database, controller: NavController){
//
//    val entradas = db.entradaQueries.select().executeAsList()
//
//    Column {
//        Nav(controller)
//
//        Text("ENTRADAS NO DISPONIBLES:")
//        Text("Butacas NO DISPONIBLES")
//        LazyRow {
//            items(entradas){
//                    entrada ->
//                Row(
//                    modifier = Modifier
//                        .padding(4.dp)
//                        .border(1.dp, color = MaterialTheme.colors.primary, CircleShape)
//                        .padding(4.dp).fillMaxWidth()
//
//                ){
//                    Text(entrada.butaca.toString(),modifier = Modifier.padding(all = 4.dp),style = MaterialTheme.typography.button)
//                }
//            } }
//        Text("Sesiones no disponibles")
//        LazyRow {
//            items(entradas){
//                    entrada ->
//                Row(
//                    modifier = Modifier
//                        .padding(4.dp)
//                        .border(1.dp, color = MaterialTheme.colors.primary, CircleShape)
//                        .padding(4.dp).fillMaxWidth()
//                ){
//                    Text(entrada.sessio.toString(),modifier = Modifier.padding(all = 4.dp),style = MaterialTheme.typography.button)
//                }
//            } }
//
//
//        val sessio = remember {
//            mutableStateOf("")
//        }
//        val butaca = remember { mutableStateOf("") }
//        val result = remember { mutableStateOf("") }
//        Row {
//            TextField(
//                value = sessio.value,
//                placeholder = { Text("Introduce la sessio") },
//                onValueChange = { sessio.value = it },
//                modifier = Modifier.fillMaxWidth()
//            )
//        }
//        Row {
//            TextField(
//                value = butaca.value,
//                placeholder = { Text("Introduce la butaca") },
//                onValueChange = { butaca.value = it },
//                modifier = Modifier.fillMaxWidth()
//            )
//        }
//
//        androidx.compose.material3.Button(
//            onClick = {
//                val sessioId = sessio.value.toLongOrNull()
//                val butacaId = butaca.value.toLongOrNull()
//                if(sessioId!=null && butacaId != null){
//                   // val butacaId= db.butacaQueries.insert(zonaId) algo de la zxona porque no lo inserta bien
//                    val entrada = db.entradaQueries.insert(butacaId,sessioId)
//                    if(entrada!= null){
//                        result.value = "Felicidades has finalizado tu compra"
//                    }else{
//                        result.value = "Introduciste mal los datos"
//                    }
//
//                }
//            },
//            modifier = Modifier.padding(vertical = 40.dp)
//        ) {
//            androidx.compose.material3.Text("go")
//        }
//        Spacer(modifier = Modifier.height(10.dp))
//
//        androidx.compose.material3.Text(text = result.value)
//    }
//}


    @Composable
    fun Nav(controller: NavController) {
        TopAppBar(
            title = { Text("Barra navegació") },
            actions = {
                IconButton(onClick = { controller.navigate(HomeRoute) }) {
                    Icon(imageVector = Icons.Filled.Home, contentDescription = "Home")
                }
            }
        )
    }

    @Composable
    fun HomeScreen(controller: NavController) {
        Column {
            Nav(controller)
//        Button(onClick = {controller.navigate(EspectacleRoute)}){
//            Text("ListarEspectaculos")
//        }
//        Button(onClick = {controller.navigate(ZonaRoute)}){
//            Text("ListarZona")
//        }
//        Button(onClick = {controller.navigate(ButacaRoute)}){
//            Text("ListarButaca")
//        }
//        Button(onClick = {controller.navigate(SessionRoute)}){
//            Text("ListarSession")
//        }
//        Button(onClick = {controller.navigate(PrecioRoute)}){
//            Text("Descubre tu precio")
//        }
//        Button(onClick = {controller.navigate(CompraRoute)}){
//            Text("Descubre los detalles de toda tu compra")
//        }

        }
    }
}