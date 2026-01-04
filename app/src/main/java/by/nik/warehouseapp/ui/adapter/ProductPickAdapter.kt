package by.nik.warehouseapp.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import by.nik.warehouseapp.R
import by.nik.warehouseapp.model.ProductItem

class ProductPickAdapter(
    private val items: List<ProductItem>,
    private val onClick: (ProductItem) -> Unit
) : RecyclerView.Adapter<ProductPickAdapter.VH>() {

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val iv: ImageView = v.findViewById(R.id.iv)
        val tvName: TextView = v.findViewById(R.id.tvName)
        val tvMeta: TextView = v.findViewById(R.id.tvMeta)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product_pick, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.iv.setImageResource(item.imageRes)
        holder.tvName.text = item.name
        holder.tvMeta.text = "Арт: ${item.article} | ШК: ${item.barcode} | Н-н: ${item.nn}"

        holder.itemView.setOnClickListener { onClick(item) }
    }

    override fun getItemCount(): Int = items.size
}
