package com.example.templete_design

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.MediaController
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.templete_design.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private var mUri : Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        getDataIntent()
        setUpOnclick()
        setUpVideoPreview()
    }

    private fun getDataIntent() {
        mUri = intent.getStringExtra("uri")?.toUri()
    }

    private fun setUpOnclick() {
        binding.llEditVideo.setOnClickListener {
            if (mUri != null){
                val intent = Intent(this, EditVideoActivity::class.java)
                intent.putExtra("uri", mUri.toString())
                startActivity(intent)
            }else{
                Toast.makeText(this, "Video is not capture", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setUpVideoPreview() {
        binding.previewViewMain.setVideoURI(mUri)
        binding.previewViewMain.setMediaController(MediaController(this))
        binding.previewViewMain.requestFocus()
        binding.previewViewMain.start()
    }

}