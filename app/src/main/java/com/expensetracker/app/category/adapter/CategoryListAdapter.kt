package com.expensetracker.app.category.adapter

import android.content.Context
import android.graphics.Typeface
import android.inputmethodservice.InputMethodService
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.expensetracker.app.R
import com.expensetracker.app.databinding.CategoryListItemBinding
import com.expensetracker.core.models.Category

const val TAG = "Cate List Adapter =>log"

class CategoryListAdapter<T : Category>(
    private val categories: MutableList<Pair<T, Boolean>>,
    private val onItemRemoved: (T, Int) -> Unit = { a, b -> },
    private val onSaveTrigger: (String, T, Int) -> Unit = { a, b, c -> },
) : RecyclerView.Adapter<CategoryListAdapter<T>.ViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CategoryListAdapter<T>.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CategoryListItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding,parent)
    }

    override fun getItemCount(): Int = categories.size

    override fun onBindViewHolder(holder: CategoryListAdapter<T>.ViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class ViewHolder(binding: CategoryListItemBinding,val parent: ViewGroup) :
        RecyclerView.ViewHolder(binding.root) {
        private val removeBtn: AppCompatImageView = binding.removeIcon
        private val fieldText: EditText = binding.fieldText
        private val editBtn: AppCompatImageView = binding.editCategoryBtn
        private val discardBtn: AppCompatImageView = binding.editCategoryDiscardBtn
        private val doneBtn: AppCompatImageView = binding.editCategoryDoneBtn
        fun bind(position: Int) {
            val data: Pair<T, Boolean> = categories[position]

            removeBtn.setOnClickListener {
                Log.d(TAG, "bind: delete pos $position ${categories[position]}")
                onItemRemoved(data.first, position)
            }

            discardBtn.setOnClickListener {
                categories[position] = data.first to false
                notifyItemChanged(position)
            }

            editBtn.setOnClickListener {
                categories[position] = data.first to true
                notifyItemChanged(position)
            }

            doneBtn.setOnClickListener {
                categories[position] = data.first to false
                if (fieldText.text.toString().isEmpty()
                    || data.first.name.toString() == fieldText.text.toString()
                ) notifyItemChanged(position)
                else onSaveTrigger(fieldText.text.toString(), data.first, position)
            }

            fieldText.setText(data.first.name.toString())

            if (data.second) { // Is Editable
                discardBtn.visibility = View.VISIBLE
                doneBtn.visibility = View.VISIBLE

                fieldText.isEnabled = true
                fieldText.isFocusable = true
                fieldText.requestFocus()
                fieldText.setSingleLine()
                editBtn.visibility = View.GONE
            } else {
                discardBtn.visibility = View.GONE
                doneBtn.visibility = View.GONE

                fieldText.clearFocus()
                fieldText.isEnabled = false
                fieldText.setTextAppearance(R.style.AppTextTheme)

                editBtn.visibility = View.VISIBLE
            }
        }
    }
}