package com.example.kredithai.presentations.screens

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import com.example.kredithai.data.dao.DividaDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavHostController,
    dividaDao: DividaDao,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showConfirmationDialog by remember { mutableStateOf(false) }
    var confirmationAction by remember { mutableStateOf({}) }
    var confirmationTitle by remember { mutableStateOf("") }
    var confirmationMessage by remember { mutableStateOf("") }

    var isProcessing by remember { mutableStateOf(false) }
    var processingMessage by remember { mutableStateOf("") }


    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            isProcessing = true
            processingMessage = "Importando dados..."
            scope.launch {
                try {
                    importCSV(context, uri, dividaDao)
                    isProcessing = false
                    Toast.makeText(context, "Importação concluída com sucesso!", Toast.LENGTH_LONG).show()
                } catch (e: Exception) {
                    isProcessing = false
                    Toast.makeText(context, "Erro na importação: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }



    val saveLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        uri?.let {
            isProcessing = true
            processingMessage = "Salvando arquivo..."
            scope.launch {
                try {
                    val file = exportToCSV(context, dividaDao)
                    context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                        file.inputStream().copyTo(outputStream)
                    }
                    file.delete()
                    isProcessing = false
                    Toast.makeText(context, "Arquivo salvo com sucesso!", Toast.LENGTH_LONG).show()
                } catch (e: Exception) {
                    isProcessing = false
                    Toast.makeText(context, "Erro ao salvar arquivo: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configurações") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {

            SettingsSection(
                title = "Gerenciamento de Dados",
                icon = Icons.Default.Storage
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    SettingItem(
                        title = "Exportar dados para CSV",
                        subtitle = "Crie um arquivo para compartilhar ou backup",
                        icon = Icons.Default.FileUpload,
                        onClick = {
                            isProcessing = true
                            processingMessage = "Exportando dados..."
                            scope.launch {
                                try {
                                    val file = exportToCSV(context, dividaDao)
                                    isProcessing = false
                                    saveLauncher.launch(file.name)
                                } catch (e: Exception) {
                                    isProcessing = false
                                    Toast.makeText(
                                        context,
                                        "Erro ao exportar: ${e.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }
                    )

                    SettingItem(
                        title = "Importar dados de CSV",
                        subtitle = "Adicione dados a partir de um arquivo CSV",
                        icon = Icons.Default.FileDownload,
                        onClick = {
                                importLauncher.launch("*/*")
                        }
                    )



                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            SettingsSection(
                title = "Segurança",
                icon = Icons.Default.Security
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    SettingItem(
                        title = "Apagar todos os dados",
                        subtitle = "Remove permanentemente todas as dívidas cadastradas",
                        icon = Icons.Default.DeleteForever,
                        iconTint = MaterialTheme.colorScheme.error,
                        onClick = {
                            confirmationTitle = "ATENÇÃO!"
                            confirmationMessage = "Esta ação irá apagar TODOS os dados do aplicativo permanentemente. Esta ação não pode ser desfeita. Deseja continuar?"
                            confirmationAction = {
                                isProcessing = true
                                processingMessage = "Apagando dados..."
                                scope.launch {
                                    try {
                                        clearAllData(dividaDao)
                                        isProcessing = false
                                        Toast.makeText(context, "Todos os dados foram apagados!", Toast.LENGTH_LONG).show()
                                    } catch (e: Exception) {
                                        isProcessing = false
                                        Toast.makeText(context, "Erro ao apagar dados: ${e.message}", Toast.LENGTH_LONG).show()
                                    }
                                }
                            }
                            showConfirmationDialog = true
                        }
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))


            SettingsSection(
                title = "Sobre o Aplicativo",
                icon = Icons.Outlined.Info
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Kredithai",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Versão 1.0.0",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Text(
                            text = "© 2025 Kredithai",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        TextButton(onClick = { /* Abrir site */ }) {
                            Text("Visite nosso site")
                        }

                        TextButton(onClick = { /* Abrir política de privacidade */ }) {
                            Text("Política de privacidade")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (showConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmationDialog = false },
            title = { Text(confirmationTitle) },
            text = { Text(confirmationMessage) },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmationDialog = false
                        confirmationAction()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showConfirmationDialog = false }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (isProcessing) {
        Dialog(
            onDismissRequest = {},
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false
            )
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .width(200.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CircularProgressIndicator()

                    Text(
                        text = processingMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsSection(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        content()
    }
}


@Composable
fun SettingItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    iconTint: Color = MaterialTheme.colorScheme.primary
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge
                )

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}



suspend fun exportToCSV(context: Context, dividaDao: DividaDao): File {
    val dividas = dividaDao.getAllDividas()
    val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
    val timestamp = dateFormat.format(Date())
    val fileName = "kredithai_export_$timestamp.csv"

    val exportDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
    val file = File(exportDir, fileName)

    withContext(Dispatchers.IO) {
        FileWriter(file).use { writer ->
            writer.append("ID,Nome Completo,CPF/CNPJ,Telefone,Endereço,Valor da Dívida,Data de Vencimento,Status,Sazonalidade,Data de Pagamento,Descrição,Juros\n")

            dividas.forEach { divida ->
                val dataVencimento = SimpleDateFormat(
                    "dd/MM/yyyy",
                    Locale.getDefault()
                ).format(Date(divida.dataVencimento))
                val dataPagamento = if (divida.dataPagamento != null) {
                    SimpleDateFormat(
                        "dd/MM/yyyy",
                        Locale.getDefault()
                    ).format(Date(divida.dataPagamento))
                } else {
                    ""
                }

                writer.append("${divida.uid},")
                writer.append("\"${divida.nomeCompleto ?: ""}\",")
                writer.append("\"${divida.cpfCnpj ?: ""}\",")
                writer.append("\"${divida.telefone ?: ""}\",")
                writer.append("\"${divida.endereco ?: ""}\",")
                writer.append("${divida.valorDivida},")
                writer.append("\"$dataVencimento\",")
                writer.append("\"${divida.status}\",")
                writer.append("\"${divida.sazonalidade}\",")
                writer.append("\"$dataPagamento\",")
                writer.append("\"${divida.descricao ?: ""}\",")
                writer.append("${divida.juros ?: 0}\n")
            }
        }
    }

    return file
}




suspend fun importCSV(context: Context, uri: Uri, dividaDao: DividaDao) {
    try {
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            val reader = inputStream.bufferedReader()
            val lines = reader.readLines()

            val dataLines = if (lines.size > 1) lines.subList(1, lines.size) else emptyList()

            var importCount = 0

            for (line in dataLines) {
                try {
                    val fields = line.split(",")

                    if (fields.size < 5) continue

                    val nomeCompleto = fields.getOrNull(1)?.trim('"') ?: ""
                    val cpfCnpj = fields.getOrNull(2)?.trim('"') ?: ""
                    val telefone = fields.getOrNull(3)?.trim('"') ?: ""
                    val endereco = fields.getOrNull(4)?.trim('"') ?: ""
                    val valorDivida = fields.getOrNull(5)?.toDoubleOrNull() ?: 0.0

                    val dataVencimentoStr = fields.getOrNull(6)?.trim('"') ?: ""
                    val dataVencimento = if (dataVencimentoStr.isNotEmpty()) {
                        try {
                            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(dataVencimentoStr)?.time
                        } catch (e: Exception) {
                            System.currentTimeMillis()
                        }
                    } else {
                        System.currentTimeMillis()
                    } ?: System.currentTimeMillis()

                    val status = fields.getOrNull(7)?.trim('"') ?: "Pendente"
                    val sazonalidade = fields.getOrNull(8)?.trim('"') ?: "Mensal"

                    val dataPagamentoStr = fields.getOrNull(9)?.trim('"') ?: ""
                    val dataPagamento = if (dataPagamentoStr.isNotEmpty()) {
                        try {
                            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(dataPagamentoStr)?.time
                        } catch (e: Exception) {
                            null
                        }
                    } else {
                        null
                    }

                    val descricao = fields.getOrNull(10)?.trim('"') ?: ""
                    val juros = fields.getOrNull(11)?.toIntOrNull() ?: 0

                    val divida = com.example.kredithai.data.models.DividaModel(
                        nomeCompleto = nomeCompleto.ifEmpty { null },
                        cpfCnpj = cpfCnpj.ifEmpty { null },
                        telefone = telefone.ifEmpty { null },
                        endereco = endereco.ifEmpty { null },
                        valorDivida = valorDivida,
                        dataVencimento = dataVencimento,
                        status = status,
                        sazonalidade = sazonalidade,
                        dataPagamento = dataPagamento,
                        descricao = descricao.ifEmpty { null },
                        juros = juros
                    )

                    dividaDao.insert(divida)
                    importCount++
                } catch (e: Exception) {
                    continue
                }
            }

            if (importCount == 0) {
                throw IOException("Nenhum registro foi importado. Verifique o formato do arquivo.")
            }
        } ?: throw IOException("Não foi possível abrir o arquivo")
    } catch (e: Exception) {
        throw IOException("Erro ao importar dados: ${e.localizedMessage}")
    }
}

suspend fun clearAllData(dividaDao: DividaDao) {
    val dividas = dividaDao.getAllDividas()
    dividas.forEach { divida ->
        dividaDao.delete(divida)
    }
}
