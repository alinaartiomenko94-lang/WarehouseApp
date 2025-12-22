package by.nik.warehouseapp.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import by.nik.warehouseapp.R
import by.nik.warehouseapp.model.ReturnProduct

class ReturnProductAdapter(
    private val items: MutableList<ReturnProduct>,
    private val listener: OnProductClickListener
) : RecyclerView.Adapter<ReturnProductAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvCode: TextView = view.findViewById(R.id.tvCode)
        val tvQty: TextView = view.findViewById(R.id.tvQty)
        val tvDefect: TextView = view.findViewById(R.id.tvDefect)
        val btnDelete: ImageView = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_return_product, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.tvCode.text = item.code
        holder.tvQty.text = "Количество: ${item.quantity}"
        holder.tvDefect.text = "Брак: ${item.defect}"

        holder.itemView.setOnClickListener {
            listener.onProductClick(item, position)
        }

        holder.btnDelete.setOnClickListener {
            listener.onProductDelete(item, position)
        }
    }

    override fun getItemCount(): Int = items.size

    fun addItem(item: ReturnProduct) {
        items.add(item)
        notifyItemInserted(items.size - 1)
    }

    fun updateItem(position: Int, item: ReturnProduct) {
        if (position < 0 || position >= items.size) return

        items[position] = item
        notifyItemChanged(position)
    }

    fun removeItem(position: Int) {
        if (position < 0 || position >= items.size) return
        items.removeAt(position)
        notifyItemRemoved(position)
    }

    interface OnProductClickListener {
        fun onProductClick(product: ReturnProduct, position: Int)
        fun onProductDelete(product: ReturnProduct, position: Int)
    }
}