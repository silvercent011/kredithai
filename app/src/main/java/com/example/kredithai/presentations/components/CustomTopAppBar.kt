import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopAppBar(
    title: String,
    currentRoute: String?,
    navController: NavController,
    onPersonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = { Text(
            text = title,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis) },
        navigationIcon = {
            if (currentRoute != "home") {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Voltar"
                    )
                }
            }
        },
        actions = {
            IconButton(onClick = onPersonClick) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Perfil"
                )
            }
        },
        modifier = modifier
    )
}