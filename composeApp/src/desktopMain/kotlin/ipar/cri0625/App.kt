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
import kotlinx.coroutines.flow.toList
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

    val user = "nayrios2004"
    val psswd = "Adri888"

    DisposableEffect(Unit) {
        CoroutineScope(Dispatchers.Default).launch {
           val isValidUser = validateUser(databaseSqlite, user, psswd)
            if(isValidUser){
                try {
                    val connectionString = "mongodb+srv://$user:$psswd@prueba.7rxv1.mongodb.net/?retryWrites=true&w=majority&appName=Prueba"
                    database = setupConnection(connectionString, "test")
                } catch (me: MongoException) {
                    error = me.message
                }
            }

        }
        onDispose { }
    }
    MaterialTheme {
        val controller = rememberNavController()
        NavHost(controller, startDestination = HomeRoute){
            composable<HomeRoute>{ HomeScreen(controller) }
            composable<Login>{LoginScreen(controller,databaseSqlite)}
            composable<Adentro>{InsideScreen(controller,database!!)}
            composable<AñadirURL>{UrlInsert(database!!, controller)}
            composable<VerURL>{URlList(database!!,controller)}
        }

    }
}

@Serializable
object HomeRoute
@Serializable
object Adentro
@Serializable
object Login
@Serializable
object AñadirURL
@Serializable
object VerURL
@Serializable
object borrarURL
@Serializable
object Deslogin


fun loginUser(db: Database){
    db.loginQueries.insert("nayrios2004", "Adri888")
}

data class URL(
    @BsonId
    val id: ObjectId,
    val url: String
)

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
     Button(onClick = {controller.navigate(Login)}){
          Text("Login APP urls")
            }

        }
    }


@Composable
fun InsideScreen(controller: NavController, database: MongoDatabase) {
    Column {
        Nav(controller)

        Button(onClick = { controller.navigate(AñadirURL) }) {
            Text("Insertar URL")
        }
        Button(onClick = { controller.navigate(VerURL) }) {
            Text("Ver URL's")
        }
        Button(onClick = { controller.navigate(Deslogin) }) {
            Text("Deslogin")
        }
        //BORRAR lo de la base de datos local, cuando compruebo al incio veo que no hay nada me lo permite
        //dejar datos por defecto en la interfaz

    }
}


fun validateUser(database: Database, username: String, password: String): Boolean {
    val user = database.loginQueries.select(username).executeAsOneOrNull()
    return user != null && user.psswd == password
}

/**Metodo para mostrar las urls**/
@Composable
fun URlList(database: MongoDatabase,controller: NavController) {
    Nav(controller)
    var isLoading by remember { mutableStateOf(true) }
    var urls by remember { mutableStateOf(listOf<URL>()) }

    LaunchedEffect(isLoading) {
        if (isLoading) {
            urls =
                database.getCollection<URL>( "Urls").find<URL>().toList()
            isLoading = false
        }
    }
    Column {

        Text("URLS")
        //Spacer(modifier = Modifier.height(20.dp))
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            LazyColumn {
                items(urls) { url ->
                    Text(url.url)
                }
            }
        }
    }
}

//Que no este vacia
@Composable
fun UrlInsert(database: MongoDatabase, controller: NavController) {
    Nav(controller)
    var insert by remember { mutableStateOf(false) }
    var url: URL by remember { mutableStateOf(URL(id = ObjectId(), url = "")) }


    LaunchedEffect(insert) {
        if (insert) {
            database.getCollection<URL>("Urls").insertOne(url)
            url = URL(id = ObjectId(), url = "")
            insert = false
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize().padding(10.dp)
    ) {
        if (insert) {
            CircularProgressIndicator()
        } else {
            Text(
                text = "URL",
                style = MaterialTheme.typography.h3,
                color = MaterialTheme.colors.primary
            )
            Spacer(modifier = Modifier.height(10.dp))
            TextField(
                value = url.url,
                onValueChange = { url = url.copy(url = it) },
                label = { Text("Name") },
                placeholder = { Text("URL") },)
        }
        Button(onClick = { insert = true}) { Text("Insert") }
    }
}

@Composable
fun LoginScreen(controller: NavController, db: Database) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loginError by remember { mutableStateOf<String?>(null) }

    Column(

    ) {
        Text("Login", style = MaterialTheme.typography.h4)

        TextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Usuario") }
        )

        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") }
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (validateUser(db, username, password)) {
                   controller.navigate(Adentro)
                } else {
                    loginError = "Usuario o contraseña incorrectos"
                }
            }
        ) {
            Text("Iniciar sesión")
        }

        loginError?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colors.error)
        }
    }
}
//
@Composable
fun LogoutScreen(controller: NavController, database: MongoDatabase) {
    var dropBD by remember { mutableStateOf(false) }
    var url: URL by remember { mutableStateOf(URL(id = ObjectId(), url = "")) }
    var loginError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(dropBD) {
        if (dropBD) {
             dropBD = database.getCollection<>()
            url = URL(id = ObjectId(), url = "")
            dropBD = false
        }
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize().padding(10.dp)
    ) {
        if (dropBD) {
            CircularProgressIndicator()
        }
        Button(onClick = { dropBD = true}) { Text("Insert") }
    }
}


