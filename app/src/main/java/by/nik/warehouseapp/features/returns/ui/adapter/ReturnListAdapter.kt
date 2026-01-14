package by.nik.warehouseapp.features.returns.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import by.nik.warehouseapp.R
import by.nik.warehouseapp.features.returns.model.ReturnDocument
import by.nik.warehouseapp.features.returns.model.ReturnStatus
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

class ReturnListAdapter(
    private val items: MutableList<ReturnDocument>,
    private val onOpenReturn: (Long) -> Unit
) : RecyclerView.Adapter<ReturnListAdapter.ViewHolder>() {

    private val expandedIds = mutableSetOf<Long>()

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitleTtn: TextView = view.findViewById(R.id.tvTitleTtn)
        val tvCounterparty: TextView = view.findViewById(R.id.tvCounterparty)
        val tvDocType: TextView = view.findViewById(R.id.tvDocType)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        val tvAcceptanceDate: TextView = view.findViewById(R.id.tvAcceptanceDate)

        val btnToggleDetails: MaterialButton = view.findViewById(R.id.btnToggleDetails)
        val btnViewList: MaterialButton = view.findViewById(R.id.btnViewList)

        val detailsContainer: MaterialCardView = view.findViewById(R.id.detailsContainer)
        val tvTotalQty: TextView = view.findViewById(R.id.tvTotalQty)
        val tvDefectQty: TextView = view.findViewById(R.id.tvDefectQty)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_return, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val doc = items[position]

        // Заголовок = №ТТН + Дата ТТН
        holder.tvTitleTtn.text = "№ ${doc.invoice} от ${doc.ttnDate}"

        // Контрагент
        holder.tvCounterparty.text = doc.contractor

        // Тип документа — пока заглушка (позже добавим выбор при создании)
        holder.tvDocType.text = "Возвратная накладная"

        // Статус
        holder.tvStatus.text = when (doc.status) {
            ReturnStatus.CREATED -> "Создан"
            ReturnStatus.ACCEPTED -> "Подтверждён"
            else -> "Неизвестно"
        }

        // Дата приёмки
        holder.tvAcceptanceDate.text = "Дата приёмки: ${doc.acceptanceDate ?: "—"}"

        // Данные для деталей
        val totalQty = doc.products.sumOf { it.quantity }
        val defectQty = doc.products.sumOf { it.defect }
        holder.tvTotalQty.text = "$totalQty шт."
        holder.tvDefectQty.text = "$defectQty шт."

        // Состояние раскрытия
        val expanded = expandedIds.contains(doc.id)
        holder.detailsContainer.visibility = if (expanded) View.VISIBLE else View.GONE
        holder.btnToggleDetails.text = if (expanded) "Скрыть детали" else "Показать детали"

        // Кнопка: показать/скрыть детали
        holder.btnToggleDetails.setOnClickListener {
            val nowExpanded = expandedIds.contains(doc.id)
            if (nowExpanded) expandedIds.remove(doc.id) else expandedIds.add(doc.id)
            notifyItemChanged(holder.bindingAdapterPosition)
        }

        // Кнопка: просмотр списка (переход к позициям возврата)
        holder.btnViewList.setOnClickListener {
            onOpenReturn(doc.id)
        }
    }

    override fun getItemCount() = items.size

    fun update(newList: List<ReturnDocument>) {
        items.clear()
        items.addAll(newList)
        notifyDataSetChanged()
    }
}
