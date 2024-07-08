package com.example.templete_design

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class BottomSheetPublicationScope(
    private var mListener : (String) -> Unit
) : BottomSheetDialogFragment() {

    private val sharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(requireContext())
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val mBottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        val view = LayoutInflater.from(context).inflate(R.layout.layout_bottom_sheet_publication_scope, null)
        mBottomSheetDialog.setContentView(view)
        initData(view)
        onclick(view)
        return mBottomSheetDialog
    }

    @SuppressLint("CutPasteId")
    private fun initData(view : View) {
        val s =  sharedPreferences.getInt("tv",0)
        when(s){
            0 ->{
                view.findViewById<TextView>(R.id.tv_check_one).visibility = View.VISIBLE
                view.findViewById<TextView>(R.id.tv_check_two).visibility = View.INVISIBLE
            }
            1 -> {
                view.findViewById<TextView>(R.id.tv_check_two).visibility = View.VISIBLE
                view.findViewById<TextView>(R.id.tv_check_one).visibility = View.INVISIBLE
            }
        }
    }

    private fun onclick(view : View) {
        view.findViewById<ConstraintLayout>(R.id.cl_one).setOnClickListener {
            mListener.invoke(view.findViewById<TextView>(R.id.tv_one).text.toString())
            view.findViewById<TextView>(R.id.tv_check_one).visibility = View.VISIBLE
            view.findViewById<TextView>(R.id.tv_check_two).visibility = View.INVISIBLE
            sharedPreferences.edit().putInt("tv", 0).apply()
            dismiss()
        }

        view.findViewById<ConstraintLayout>(R.id.cl_two).setOnClickListener {
            mListener.invoke(view.findViewById<TextView>(R.id.tv_two).text.toString())
            view.findViewById<TextView>(R.id.tv_check_two).visibility = View.VISIBLE
            view.findViewById<TextView>(R.id.tv_check_one).visibility = View.INVISIBLE
            sharedPreferences.edit().putInt("tv", 1).apply()
            dismiss()
        }
    }
}