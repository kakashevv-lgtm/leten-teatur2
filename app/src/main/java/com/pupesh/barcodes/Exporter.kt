package com.pupesh.barcodes

import android.net.Uri
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets

class Exporter(private val activity: ComponentActivity) {
    private var pending: List<String>? = null
    private val createCsv = activity.registerForActivityResult(
        ActivityResultContracts.CreateDocument("text/csv")
    ) { uri: Uri? ->
        val data = pending ?: return@registerForActivityResult
        pending = null
        if (uri != null) writeCsv(uri, data)
    }

    fun exportCsv(rows: List<String>) {
        pending = rows
        createCsv.launch("barcodes_${System.currentTimeMillis()}.csv")
    }

    private fun writeCsv(uri: Uri, rows: List<String>) {
        activity.contentResolver.openOutputStream(uri)?.use { out ->
            OutputStreamWriter(out, StandardCharsets.UTF_8).use { w ->
                w.appendLine("Index,Barcode")
                rows.forEachIndexed { i, code -> w.appendLine("${i + 1},$code") }
            }
        }
        Toast.makeText(activity, "Exported ${rows.size} rows.", Toast.LENGTH_SHORT).show()
    }
}
