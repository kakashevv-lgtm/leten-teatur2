package com.pupesh.barcodes

import android.Manifest
import android.content.Context
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.core.CameraSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collectLatest

class ScanActivity : AppCompatActivity() {

    private lateinit var vm: ScanViewModel
    private lateinit var previewView: PreviewView
    private lateinit var lastText: TextView
    private lateinit var statusText: TextView
    private lateinit var exporter: Exporter
    private var beep: MediaPlayer? = null

    private val reqCam = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) startCamera() else Toast.makeText(this, "Camera permission required", Toast.LENGTH_LONG).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)

        vm = ViewModelProvider(this)[ScanViewModel::class.java]
        previewView = findViewById(R.id.previewView)
        lastText = findViewById(R.id.lastText)
        statusText = findViewById(R.id.statusText)
        exporter = Exporter(this)

        findViewById<Button>(R.id.exportBtn).setOnClickListener {
            exporter.exportCsv(vm.items.value)
        }
        findViewById<Button>(R.id.clearBtn).setOnClickListener {
            vm.clear(); statusText.text = "Cleared"; lastText.text = getString(R.string.last)
        }

        beep = MediaPlayer.create(this, R.raw.beep)

        lifecycleScope.launchWhenStarted {
            vm.items.collectLatest { list ->
                if (list.isNotEmpty()) lastText.text = "Last: ${list.last()}"
            }
        }

        reqCam.launch(Manifest.permission.CAMERA)
    }

    private fun startCamera() {
        val providerFuture = ProcessCameraProvider.getInstance(this)
        providerFuture.addListener({
            val provider = providerFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }
            val analysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build().also {
                    it.setAnalyzer(ContextCompat.getMainExecutor(this),
                        BarcodeAnalyzer { code ->
                            val dup = vm.onBarcode(code)
                            runOnUiThread {
                                if (dup) {
                                    statusText.text = "Duplicate: $code"
                                    soundAndVibrate()
                                    Toast.makeText(this, "Duplicate: $code", Toast.LENGTH_SHORT).show()
                                } else {
                                    statusText.text = "Added: $code"
                                }
                            }
                        })
                }
            provider.unbindAll()
            provider.bindToLifecycle(this, CameraSelector.DEFAULT_BACK_CAMERA, preview, analysis)
        }, ContextCompat.getMainExecutor(this))
    }

    private fun soundAndVibrate() {
        val v = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            v.vibrate(VibrationEffect.createOneShot(60, VibrationEffect.DEFAULT_AMPLITUDE))
        else @Suppress("DEPRECATION") v.vibrate(60)
        beep?.let { if (!it.isPlaying) it.start() }
    }
}
