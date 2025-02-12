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

    val username = "nayrios2004"
    val password = "Adri888"

    DisposableEffect(Unit) {
        CoroutineScope(Dispatchers.Default).launch {
            val isValidUser = validateUser(databaseSqlite, username, password)
            try {
                val connectionString = "mongodb+srv://:Adri888@prueba.7rxv1.mongodb.net/?retryWrites=true&w=majority&appName=Prueba"
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

suspend fun logout(db: Database){
    //db.close
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

fun validateUser(db: Database, username: String, password: String): Boolean {
    val result = db.loginQueries.select(username).executeAsOneOrNull()
    return result != null && result.password == password
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
