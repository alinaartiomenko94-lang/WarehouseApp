package by.nik.warehouseapp.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import by.nik.warehouseapp.R
import by.nik.warehouseapp.model.ReturnDocument

class ReturnListAdapter(
    private val items: List<ReturnDocument>,
    private val onClick: (ReturnDocument) -> Unit
) : RecyclerView.Adapter<ReturnListAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvInvoice: TextView = view.findViewById(R.id.tvInvoice)
        val tvContractor: TextView = view.findViewById(R.id.tvContractor)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_return, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.tvInvoice.text = "ТТН №${item.invoice}"
        holder.tvContractor.text = item.contractor
        holder.tvStatus.text = item.status.name

        holder.itemView.setOnClickListener {
            onClick(item)
        }
    }

    override fun getItemCount() = items.size

}