package com.example.templete_design

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.MediaController
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.arthenica.mobileffmpeg.FFmpeg
import com.example.templete_design.databinding.ActivityEditVideoBinding
import java.io.File


class EditVideoActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityEditVideoBinding.inflate(layoutInflater)
    }
    private var mUri: Uri? = null
    private val adapter = ColorAdapter(::itemOnClick)
    val colors = listOf(
        Color(0xFF0000),  // Red
        Color(0x00FF00),  // Green
        Color(0x0000FF),  // Blue
        Color(0xFFFF00),  // Yellow
        Color(0xFF00FF),  // Magenta
        Color(0x00FFFF)   // Cyan
        // Add more colors as needed
    )

    @SuppressLint("MissingInflatedId")
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
        setUpOnClick()
        setUpRecyclerView()
        mUri?.let { setUpVideoPreview(it) }
    }

    private fun setUpRecyclerView() {
        val layoutManager =LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        binding.rcvColor.layoutManager = layoutManager
        binding.rcvColor.adapter = adapter
        adapter.submitList(colors)
    }

    private fun getDataIntent() {
        mUri = Uri.parse(intent.getStringExtra("uri"))
    }

    private fun setUpOnClick() {
        binding.llConfirmEditVideo.setOnClickListener { showDialogConfirm() }
        binding.btnAction.setOnClickListener { }
        binding.btnFilter.setOnClickListener { }
        binding.btnFilter.setOnClickListener { }
        binding.btnText.setOnClickListener {
            binding.textViewOverlay.visibility = View.VISIBLE
            binding.llEditText.visibility = View.VISIBLE
            binding.llEditeVideo.visibility = View.INVISIBLE
            initText()
            moveText()
        }
    }

    private fun initText() {
        binding.textViewOverlay.text = binding.editTextText.text
        binding.editTextText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) =
                Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.textViewOverlay.text = s
            }

            override fun afterTextChanged(s: Editable?) = Unit
        })
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun moveText() {
        binding.textViewOverlay.setOnTouchListener(object : View.OnTouchListener {
            private var dX = 0f
            private var dY = 0f
            private var initialX = 0f
            private var initialY = 0f

            override fun onTouch(view: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        dX = view.x - event.rawX
                        dY = view.y - event.rawY

                        // Lấy vị trí ban đầu của VideoView trên màn hình
                        val location = IntArray(2)
                        binding.previewViewEdit.getLocationOnScreen(location)
                        initialX = location[0].toFloat()
                        initialY = location[1].toFloat()

                        return true
                    }

                    MotionEvent.ACTION_MOVE -> {
                        val newX = event.rawX + dX
                        val newY = event.rawY + dY
                        val parentWidth = binding.previewViewEdit.width
                        val parentHeight = binding.previewViewEdit.height

                        // Giới hạn vị trí di chuyển của TextView trong VideoView
                        val rightLimit = initialX + parentWidth - view.width
                        val bottomLimit = initialY + parentHeight - view.height

                        view.x =
                            if (newX < initialX) initialX else if (newX > rightLimit) rightLimit else newX
                        view.y =
                            if (newY < initialY) initialY else if (newY > bottomLimit) bottomLimit else newY

                        return true
                    }
                }
                return false
            }
        })
    }

    private fun editTextVideo(
        text: String,
        x: Int,
        y: Int,
        fontSize: Int,
        fontColor: String,
        fontFile: String
    ) {
//            val text = "動画編集 動画編集 動画編集"
//            val x = 100
//            val y = 100
//            val fontSize = 24
//            val fontColor = "0xFF0000"
//            val fontFile = getFontPath(R.font.hiragino_kaku_gothic_pro_w3)
        // Get the external storage directory for the app
        val outputDir = getExternalFilesDir(Environment.DIRECTORY_MOVIES)
        if (outputDir != null && !outputDir.exists()) {
            outputDir.mkdirs()
        }

        val inputVideo = getRealPathFromURI(mUri)
        val outputVideo = File(outputDir, "output.mp4").absolutePath

        val cmd = arrayOf(
            "-y",
            "-i",
            inputVideo,
            "-vf",
            "drawtext=text='$text':x=$x:y=$y:fontsize=$fontSize:fontcolor=$fontColor:fontfile=$fontFile",
            "-c:a",
            "aac",
            outputVideo
        )

        val result = FFmpeg.execute(cmd)

        if (result == 0) {
            val outputVideoUri = Uri.fromFile(File(outputVideo))
            setUpVideoPreview(outputVideoUri)
        } else {
            Toast.makeText(this, "Thất bại", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getFontPath(resourceId: Int): String {
        val fontUri = ResourcesCompat.getFont(this, resourceId)?.let { font ->
            val tempFile = File.createTempFile("font", ".ttf", cacheDir)
            tempFile.outputStream().use { output ->
                resources.openRawResource(resourceId).use { input ->
                    input.copyTo(output)
                }
            }
            tempFile.absolutePath
        }
        return fontUri ?: ""
    }

    private fun getRealPathFromURI(uri: Uri?): String {
        var filePath = ""
        uri?.let {
            val cursor: Cursor? = contentResolver.query(it, null, null, null, null)
            cursor?.use { c ->
                if (c.moveToFirst()) {
                    val columnIndex = c.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
                    filePath = c.getString(columnIndex)
                }
            }
        }
        return filePath
    }

    private fun setUpVideoPreview(uri: Uri) {
        binding.previewViewEdit.setVideoURI(uri)
        binding.previewViewEdit.setMediaController(MediaController(this))
        binding.previewViewEdit.requestFocus()
        binding.previewViewEdit.start()
    }

    private fun showDialogConfirm() {
        val dialog = Dialog(this, R.style.CustomDialogTheme)
        dialog.setContentView(R.layout.layout_diglog_confirm_edit_video)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        dialog.findViewById<LinearLayout>(R.id.ll_cancel).setOnClickListener {
            dialog.dismiss()
        }
        dialog.findViewById<LinearLayout>(R.id.ll_ok).setOnClickListener {
            startActivity(Intent(this, VideoManagerActivity::class.java))
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun itemOnClick(color: Color){

    }
}