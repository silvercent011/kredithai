package com.example.kredithai.presentations.service

import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.room.Room
import androidx.work.workDataOf
import com.example.kredithai.data.dao.DividaDao
import com.example.kredithai.data.db.DividaDB
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
                    "dd/MM/yyyy", Locale.getDefault()
                ).format(Date(divida.dataVencimento))
                val dataPagamento = if (divida.dataPagamento != null) {
                    SimpleDateFormat(
                        "dd/MM/yyyy", Locale.getDefault()
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


class ExportWorker(
    context: Context, workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {


    private val db = Room.databaseBuilder(
        applicationContext, DividaDB::class.java, "database-dividas"
    ).build()

    override suspend fun doWork(): Result {


        val exportUriString = inputData.getString("export_uri")
        if (exportUriString.isNullOrEmpty()) {
            return Result.failure(workDataOf("ERROR" to "URI de exportação não informado"))
        }
        val exportUri = Uri.parse(exportUriString)

        return try {
            val file = exportToCSV(applicationContext, db.dividaDao())

            applicationContext.contentResolver.openOutputStream(exportUri)?.use { outputStream ->
                file.inputStream().copyTo(outputStream)
            } ?: throw Exception("Não foi possível abrir o output stream.")

            val output = workDataOf("exported_file_path" to exportUri.toString())
            Result.success(output)
        } catch (e: Exception) {
            Result.failure(workDataOf("ERROR" to e.localizedMessage))
        }
    }
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
                            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(
                                dataVencimentoStr
                            )?.time
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
                            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(
                                dataPagamentoStr
                            )?.time
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


class ImportWorker(
    context: Context,
    workerParams: WorkerParameters,

    ) : CoroutineWorker(context, workerParams) {

    val db = Room.databaseBuilder(
        applicationContext, DividaDB::class.java, "database-dividas"
    ).build()

    override suspend fun doWork(): Result {
        val uriString = inputData.getString("import_uri") ?: return Result.failure()
        val uri = Uri.parse(uriString)
        return try {
            withContext(Dispatchers.IO) {
                importCSV(applicationContext, uri, db.dividaDao())
            }
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}


suspend fun clearAllData(dividaDao: DividaDao) {
    val dividas = dividaDao.getAllDividas()
    dividas.forEach { divida ->
        dividaDao.delete(divida)
    }
}