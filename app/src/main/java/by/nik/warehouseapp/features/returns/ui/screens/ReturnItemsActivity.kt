package by.nik.warehouseapp.features.returns.ui.screens

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.nik.warehouseapp.R
import by.nik.warehouseapp.core.data.RepositoryProvider
import by.nik.warehouseapp.features.returns.model.ReturnDocument
import by.nik.warehouseapp.features.returns.model.ReturnProduct
import by.nik.warehouseapp.features.returns.model.ReturnStatus
import by.nik.warehouseapp.features.returns.ui.adapter.ReturnProductAdapter
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView

class ReturnItemsActivity : AppCompatActivity(), ReturnProductAdapter.OnProductClickListener {

    private val repo by lazy { RepositoryProvider.returnRepository }

    private var returnId: Long = -1L
    private lateinit var doc: ReturnDocument

    private lateinit var tvInvoice: MaterialTextView
    private lateinit var tvDate: MaterialTextView
    private lateinit var tvContractor: MaterialTextView
    private lateinit var tvAcceptanceDate: MaterialTextView

    private lateinit var tvTotalItems: MaterialTextView
    private lateinit var tvTotalQty: MaterialTextView
    private lateinit var tvTotalDefect: MaterialTextView
    private lateinit var tvStatus: MaterialTextView

    private lateinit var btnAddProduct: MaterialButton
    private lateinit var btnConfirm: MaterialButton

    private lateinit var adapter: ReturnProductAdapter

    private lateinit var panelDefect: android.widget.LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_return_items)

        returnId = intent.getLongExtra("returnId", -1L)
        if (returnId <= 0) {
            Toast.makeText(this, "Ошибка: не передан returnId", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // Загружаем документ
        doc = repo.getById(returnId) ?: run { finish(); return }

        bindViews()
        setupRecycler()
        setupActions()

        renderDoc()
    }

    override fun onResume() {
        super.onResume()
        // ✅ КЛЮЧЕВО: после AddProductActivity (который не закрывается “по результату”)
        // мы обновляем документ и список тут
        refreshDoc()
    }

    private fun bindViews() {
        tvInvoice = findViewById(R.id.tvInvoice)
        tvDate = findViewById(R.id.tvDate)
        tvContractor = findViewById(R.id.tvContractor)
        tvAcceptanceDate = findViewById(R.id.tvAcceptanceDate)

        tvTotalItems = findViewById(R.id.tvTotalItems)
        tvTotalQty = findViewById(R.id.tvTotalQty)
        tvTotalDefect = findViewById(R.id.tvTotalDefect)
        tvStatus = findViewById(R.id.tvStatus)

        btnAddProduct = findViewById(R.id.btnAddProduct)
        btnConfirm = findViewById(R.id.btnConfirm)

        panelDefect = findViewById(R.id.panelDefect)
    }

    private fun setupRecycler() {
        val recyclerView = findViewById<RecyclerView>(R.id.rcProducts)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = ReturnProductAdapter(mutableListOf(), this)
        recyclerView.adapter = adapter
    }

    private fun setupActions() {
        btnAddProduct.setOnClickListener {
            if (isAccepted()) {
                Toast.makeText(this, "Возврат уже подтверждён", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // ✅ Запускаем AddProductActivity БЕЗ startActivityForResult
            // и ОБЯЗАТЕЛЬНО передаём returnId
            val intent = Intent(this, AddProductActivity::class.java).apply {
                putExtra(AddProductActivity.EXTRA_RETURN_ID, doc.id)
            }
            startActivity(intent)
        }

        btnConfirm.setOnClickListener {
            if (isAccepted()) return@setOnClickListener

            if (doc.products.isEmpty()) {
                Toast.makeText(this, "Нельзя подтвердить: нет товаров", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            repo.confirmReturn(doc.id)
            refreshDoc()
        }
    }

    private fun refreshDoc() {
        doc = repo.getById(returnId) ?: return
        renderDoc()
    }

    private fun renderDoc() {
        tvInvoice.text = doc.invoice
        tvDate.text = doc.ttnDate
        tvContractor.text = doc.contractor

        tvAcceptanceDate.text = doc.acceptanceDate ?: "—"

        tvStatus.text = doc.status.title

        adapter.setItems(doc.products)

        updateSummary()
        updateActionState()
    }

    private fun updateSummary() {
        val list = doc.products
        tvTotalItems.text = list.size.toString()
        tvTotalQty.text = list.sumOf { it.quantity }.toString()
        tvTotalDefect.text = list.sumOf { it.defect }.toString()

        // визуальная подсказка по браку (как у тебя было)
        val defectSum = list.sumOf { it.defect }
        panelDefect.setBackgroundColor(
            if (defectSum > 0) android.graphics.Color.parseColor("#C62828")
            else android.graphics.Color.parseColor("#2E7D32")
        )
    }

    private fun isAccepted(): Boolean = doc.status == ReturnStatus.ACCEPTED

    private fun updateActionState() {
        btnConfirm.isEnabled = doc.products.isNotEmpty() && !isAccepted()

        btnAddProduct.isEnabled = !isAccepted()
        btnAddProduct.alpha = if (btnAddProduct.isEnabled) 1f else 0.35f
    }

    // ============================================================
    // Adapter callbacks
    // ============================================================

    override fun onProductClick(product: ReturnProduct, position: Int) {
        if (isAccepted()) {
            Toast.makeText(this, "Возврат уже подтверждён", Toast.LENGTH_SHORT).show()
            return
        }

        // Пока оставим так: редактирование можно сделать следующим шагом.
        // Сейчас главный поток — сканирование.
        Toast.makeText(this, "Редактирование добавим следующим шагом", Toast.LENGTH_SHORT).show()
    }

    override fun onProductDelete(product: ReturnProduct, position: Int) {
        if (isAccepted()) {
            Toast.makeText(this, "Возврат уже подтверждён", Toast.LENGTH_SHORT).show()
            return
        }

        repo.deleteProduct(doc.id, position)
        refreshDoc()
    }
}
