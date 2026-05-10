package com.example.fincurr.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.fincurr.R
import com.example.fincurr.data.model.TransactionEntity
import com.example.fincurr.data.model.TransactionType
import com.example.fincurr.databinding.ItemTransactionBinding
import com.example.fincurr.utils.Formatter

class TransactionAdapter(
    private var currency: String,
    private val onClick: (TransactionEntity) -> Unit
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    private val items = mutableListOf<TransactionEntity>()

    fun submitList(list: List<TransactionEntity>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    fun updateCurrency(code: String) {
        currency = code
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding = ItemTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TransactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class TransactionViewHolder(private val binding: ItemTransactionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: TransactionEntity) {
            binding.textTitle.text = item.note
            binding.textCategory.text = item.category
            binding.textDate.text = Formatter.formatDate(item.timestamp)
            val amountText = Formatter.formatCurrency(item.amount, currency)
            binding.textAmount.text = if (item.type == TransactionType.DEBIT) "-$amountText" else amountText
            val color = if (item.type == TransactionType.DEBIT) {
                ContextCompat.getColor(binding.root.context, R.color.colorError)
            } else {
                ContextCompat.getColor(binding.root.context, R.color.colorSuccess)
            }
            binding.textAmount.setTextColor(color)
            binding.root.setOnClickListener { onClick(item) }
        }
    }
}
