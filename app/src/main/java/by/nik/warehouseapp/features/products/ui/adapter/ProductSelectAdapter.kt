package by.nik.warehouseapp.features.products.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import by.nik.warehouseapp.R
import by.nik.warehouseapp.features.products.data.entity.ProductEntity

class ProductSelectAdapter(
    private val items: MutableList<ProductEntity>,
    private val onClick: (ProductEntity) -> Unit
) : RecyclerView.Adapter<ProductSelectAdapter.VH>() {

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvMeta: TextView = itemView.findViewById(R.id.tvMeta)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val p = items[position]
        holder.tvName.text = p.name
        holder.tvMeta.text = "Код: ${p.nomenclatureCode} | Арт: ${p.article} | ШК: ${p.barcode ?: "—"}"
        holder.itemView.setOnClickListener { onClick(p) }
    }

    override fun getItemCount(): Int = items.size

    fun update(newItems: List<ProductEntity>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}
