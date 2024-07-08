package com.example.templete_design

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class VideoManagerActivity : AppCompatActivity() {
    private val sharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(this)
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_video_manager)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initData()

        findViewById<LinearLayout>(R.id.ll_publication_scope).setOnClickListener {
            val bottomSheetPublicationScope = BottomSheetPublicationScope(::itemClick)
            bottomSheetPublicationScope.show(supportFragmentManager, bottomSheetPublicationScope.tag)
        }

        findViewById<LinearLayout>(R.id.ll_post).setOnClickListener {
            if (findViewById<Switch>(R.id.switch1).isChecked) {
                findViewById<LinearLayout>(R.id.ll_post).isClickable = false
                shareLink("https://example/test")
            } else {
                Toast.makeText(this, "Chua chon chia se link", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<TextView>(R.id.tv_back).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

    }

    private fun initData() {
        val s =  sharedPreferences.getInt("tv",0)
        when(s){
            0 ->{
                findViewById<TextView>(R.id.tv_publication_scope).text = "全体公開"

            }
            1 -> {
                findViewById<TextView>(R.id.tv_publication_scope).text = "ファン限定"
            }
        }
    }

    private fun itemClick(s : String){
        findViewById<TextView>(R.id.tv_publication_scope).text = s
    }

    private fun shareLink(url: String) {
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, url)
            type = "text/plain"
        }
        launcher.launch(Intent.createChooser(intent,""))
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { _ ->
        findViewById<LinearLayout>(R.id.ll_post).isClickable = true
    }
}