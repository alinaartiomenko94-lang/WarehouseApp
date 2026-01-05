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
import by.nik.warehouseapp.features.returns.ui.adapter.ReturnProductAdapter
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textview.MaterialTextView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ReturnItemsActivity : AppCompatActivity(), ReturnProductAdapter.OnProductClickListener {

    private val ADD_PRODUCT_REQUEST = 100
    private lateinit var adapter: ReturnProductAdapter
    private val repo = RepositoryProvider.returnRepository
    private lateinit var doc: ReturnDocument

    private lateinit var tvTotalItems: MaterialTextView
    private lateinit var tvTotalQty: MaterialTextView
    private lateinit var tvTotalDefect: MaterialTextView

    private lateinit var tvInvoice: MaterialTextView
    private lateinit var tvDate: MaterialTextView           // Дата ТТН
    private lateinit var tvContractor: MaterialTextView
    private lateinit var tvAcceptanceDate: MaterialTextView // Дата приёмки (НОВОЕ)

    private lateinit var btnConfirm: MaterialButton
    private lateinit var fabAddProduct: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_return_items)

        val returnId = intent.getLongExtra("returnId", -1L)
        doc = repo.getById(returnId) ?: run { finish(); return }

        tvInvoice = findViewById(R.id.tvInvoice)
        tvDate = findViewById(R.id.tvDate)
        tvContractor = findViewById(R.id.tvContractor)
        fabAddProduct = findViewById(R.id.fabAddProduct)

        // НОВОЕ: TextView для даты приёмки
        // ⚠️ В layout должен появиться MaterialTextView с id tvAcceptanceDate
        tvAcceptanceDate = findViewById(R.id.tvAcceptanceDate)

        tvTotalItems = findViewById(R.id.tvTotalItems)
        tvTotalQty = findViewById(R.id.tvTotalQty)
        tvTotalDefect = findViewById(R.id.tvTotalDefect)

        val recyclerView = findViewById<RecyclerView>(R.id.rcProducts)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = ReturnProductAdapter(doc.products, this)
        recyclerView.adapter = adapter

        findViewById<FloatingActionButton>(R.id.fabAddProduct).setOnClickListener {
            startActivityForResult(Intent(this, AddProductActivity::class.java), ADD_PRODUCT_REQUEST)
        }

        btnConfirm = findViewById(R.id.btnConfirm)
        btnConfirm.setOnClickListener {
            showConfirmDialog()
        }

        renderHeader()
        updateSummary()
        updateConfirmButtonState()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == ADD_PRODUCT_REQUEST && resultCode == RESULT_OK) {
            val d = data ?: return

            val code = d.getStringExtra("code") ?: return
            val qty = d.getStringExtra("qty")?.toIntOrNull() ?: return
            val defect = d.getStringExtra("defect")?.toIntOrNull() ?: 0
            val position = d.getIntExtra("position", -1)

            val product = ReturnProduct(code = code, quantity = qty, defect = defect)

            if (position >= 0) {
                repo.updateProduct(doc.id, position, product)
                adapter.notifyItemChanged(position)
            } else {
                repo.addProduct(doc.id, product)
                adapter.notifyItemInserted(doc.products.size - 1)
            }

            updateSummary()
            updateConfirmButtonState()
        }
    }

    override fun onProductClick(product: ReturnProduct, position: Int) {
        val intent = Intent(this, AddProductActivity::class.java).apply {
            putExtra("code", product.code)
            putExtra("qty", product.quantity.toString())
            putExtra("defect", product.defect.toString())
            putExtra("position", position)
        }
        startActivityForResult(intent, ADD_PRODUCT_REQUEST)
    }

    override fun onProductDelete(product: ReturnProduct, position: Int) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Удалить товар")
            .setMessage("Удалить ${product.code}?")
            .setPositiveButton("Удалить") { _, _ ->
                repo.deleteProduct(doc.id, position)
                adapter.notifyItemRemoved(position)
                updateSummary()
                updateConfirmButtonState()
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun renderHeader() {
        // Обновляем doc из репозитория на всякий случай (после confirmReturn)
        doc = repo.getById(doc.id) ?: return

        tvInvoice.text = doc.invoice
        tvDate.text = doc.ttnDate
        tvContractor.text = doc.contractor

        // Дата приёмки появляется только после подтверждения
        tvAcceptanceDate.text = doc.acceptanceDate ?: "—"
    }

    private fun updateSummary() {
        val list = doc.products
        tvTotalItems.text = list.size.toString()
        tvTotalQty.text = "${list.sumOf { it.quantity }} шт."
        tvTotalDefect.text = "${list.sumOf { it.defect }} шт."
    }

    private fun updateConfirmButtonState() {
        // Кнопка активна только если есть товары
        btnConfirm.isEnabled = doc.products.isNotEmpty()
    }

    private fun showConfirmDialog() {
        if (doc.products.isEmpty()) {
            Toast.makeText(this, "Список товаров пуст", Toast.LENGTH_SHORT).show()
            return
        }

        val totalItems = doc.products.size
        val totalQty = doc.products.sumOf { it.quantity }
        val totalDefect = doc.products.sumOf { it.defect }

        val message = """
            ТТН: ${doc.invoice}
            Дата ТТН: ${doc.ttnDate}
            
            Позиций: $totalItems
            Всего: $totalQty
            Брак: $totalDefect
            
            Подтвердить возврат?
        """.trimIndent()

        MaterialAlertDialogBuilder(this)
            .setTitle("Подтверждение возврата")
            .setMessage(message)
            .setNegativeButton("Отмена", null)
            .setPositiveButton("Подтвердить") { _, _ ->
                confirmReturn()
            }
            .show()
    }

    private fun confirmReturn() {
        repo.confirmReturn(doc.id)          // тут ставится acceptanceDate и статус
        doc = repo.getById(doc.id) ?: doc   // перечитать

        // обновить UI
        tvInvoice.text = doc.invoice
        tvDate.text = doc.ttnDate
        tvContractor.text = doc.contractor
        tvAcceptanceDate.text = doc.acceptanceDate ?: "Ожидает подтверждения"

        updateSummary()

        Toast.makeText(this, "Возврат подтверждён (локально)", Toast.LENGTH_LONG).show()
    }

}
