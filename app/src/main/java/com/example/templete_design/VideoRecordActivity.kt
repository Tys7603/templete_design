package com.example.templete_design

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.MediaController
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.templete_design.databinding.ActivityVideoRecordBinding

class VideoRecordActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityVideoRecordBinding.inflate(layoutInflater)
    }
    private var mUri : Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        mUri = Uri.parse("content://media/external_primary/video/media/1000000076")
        setUpOnClick()
    }

    private fun setUpOnClick() {
        binding.button.setOnClickListener { checkAndRequestPermissions() }
        binding.button2.setOnClickListener { confirmVideo() }
    }

    private fun recordVideo() {
        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        activityResultLauncher.launch(intent)
    }

    private fun confirmVideo() {
        if (mUri != null){
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("uri", mUri.toString())
            startActivity(intent)
        }else{
            Toast.makeText(this, "Video is not capture", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkAndRequestPermissions() {
        val cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        val recordAudioPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)

        val manageStoragePermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            ContextCompat.checkSelfPermission(this, Manifest.permission.MANAGE_EXTERNAL_STORAGE)
        } else {
            PackageManager.PERMISSION_GRANTED
        }
        val readStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        val writeStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        val permissionsToRequest = mutableListOf<String>()

        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.CAMERA)
        }
        if (recordAudioPermission != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.RECORD_AUDIO)
        }
//        if (manageStoragePermission != PackageManager.PERMISSION_GRANTED) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                permissionsToRequest.add(Manifest.permission.MANAGE_EXTERNAL_STORAGE)
//            }
//        }
//        if (readStoragePermission != PackageManager.PERMISSION_GRANTED) {
//            permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
//        }
//        if (writeStoragePermission != PackageManager.PERMISSION_GRANTED) {
//            permissionsToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
//        }

        if (permissionsToRequest.isNotEmpty()) {
            requestPermissionsLauncher.launch(permissionsToRequest.toTypedArray())
        } else {
            recordVideo()
        }
    }

    private val activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){results: ActivityResult? ->
        if (results!!.resultCode == Activity.RESULT_OK){
            mUri = results.data!!.data
            binding.previewView.setVideoURI(mUri)
            binding.previewView.setMediaController(MediaController(this))
            binding.previewView.requestFocus()
            binding.previewView.start()
            Log.d("TAG", ": $mUri")
        }else{
            mUri = null
            Toast.makeText(this, "Video is not capture", Toast.LENGTH_SHORT).show()
        }
    }

    private val requestPermissionsLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        val allGranted = permissions.entries.all { it.value }
        if (allGranted) {
            recordVideo()
        } else {
            Toast.makeText(this, "Permissions denied", Toast.LENGTH_SHORT).show()
        }
    }
}