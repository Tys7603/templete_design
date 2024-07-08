package com.example.templete_design

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.templete_design.databinding.ItemColorBinding

class ColorAdapter(
    private val mListener: (Color) -> Unit
) : ListAdapter<Color, ColorAdapter.ColorViewHolder>(POST_COMPARATOR) {

    companion object {
        private val POST_COMPARATOR = object : DiffUtil.ItemCallback<Color>() {
            override fun areItemsTheSame(oldItem: Color, newItem: Color): Boolean =
                oldItem.hex == newItem.hex

            override fun areContentsTheSame(oldItem: Color, newItem: Color): Boolean =
                oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        val binding = ItemColorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ColorViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    inner class ColorViewHolder(private val binding: ItemColorBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(color: Color) {
            binding.bgColor.setBackgroundColor(color.hex)
            binding.root.setOnClickListener { mListener.invoke(color) }
        }
    }
}