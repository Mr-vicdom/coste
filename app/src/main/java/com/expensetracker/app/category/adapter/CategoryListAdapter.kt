package com.expensetracker.app.category.adapter

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.expensetracker.app.databinding.CategoryListItemBinding
import com.expensetracker.core.models.Category

const val TAG = "Cate List Adapter =>log"

class CategoryListAdapter<T: Category>(
    private val categories: MutableList<T>,
    private val onItemRemoved: (T) -> Unit = {},
    private val onItemChanged: (String, T) -> Unit = { a,b -> }
): RecyclerView.Adapter<CategoryListAdapter<T>.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryListAdapter<T>.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CategoryListItemBinding.inflate(inflater,parent,false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = categories.size

    override fun onBindViewHolder(holder: CategoryListAdapter<T>.ViewHolder, position: Int) {
        holder.bind(categories[position])
    }

    inner class ViewHolder(binding: CategoryListItemBinding): RecyclerView.ViewHolder(binding.root) {
        private val removeBtn : CardView = binding.removeIcon
        private val fieldText : EditText = binding.fieldText
        fun bind(data: T){

            removeBtn.setOnClickListener {
                onItemRemoved(data)
            }
            fieldText.setText(data.name.toString())
            fieldText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int,
                ) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    onItemChanged(s.toString(),data)
                    Log.d(TAG, "onTextChanged: $s")
                }

                override fun afterTextChanged(s: Editable?) {}
            })
        }
    }
}