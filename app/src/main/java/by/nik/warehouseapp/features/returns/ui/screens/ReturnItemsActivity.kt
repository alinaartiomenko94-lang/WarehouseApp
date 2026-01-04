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
    private lateinit var tvDate: MaterialTextView
    private lateinit var tvContractor: MaterialTextView


    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_return_items)

        val returnId = intent.getLongExtra("returnId", -1L)
        doc = repo.getById(returnId) ?: run { finish(); return }

        tvInvoice = findViewById(R.id.tvInvoice)
        tvDate = findViewById(R.id.tvDate)
        tvContractor = findViewById(R.id.tvContractor)

        tvInvoice.text = doc.invoice
        tvDate.text = doc.date
        tvContractor.text = doc.contractor

        tvTotalItems = findViewById(R.id.tvTotalItems)
        tvTotalQty = findViewById(R.id.tvTotalQty)
        tvTotalDefect = findViewById(R.id.tvTotalDefect)

        val recyclerView = findViewById<RecyclerView>(R.id.rcProducts)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // ВАЖНО: адаптер должен работать с doc.products
        adapter = ReturnProductAdapter(doc.products, this)
        recyclerView.adapter = adapter

        findViewById<FloatingActionButton>(R.id.fabAddProduct).setOnClickListener {
            startActivityForResult(Intent(this, AddProductActivity::class.java), ADD_PRODUCT_REQUEST)
        }

        findViewById<MaterialButton>(R.id.btnConfirm).setOnClickListener {
            showConfirmDialog() // используй один диалог
        }

        updateSummary()
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
            }
            .setNegativeButton("Отмена", null)
            .show()
    }



    private fun updateSummary() {
        val list = doc.products
        tvTotalItems.text = "Позиций: ${list.size}"
        tvTotalQty.text = "Всего: ${list.sumOf { it.quantity }}"
        tvTotalDefect.text = "Брак: ${list.sumOf { it.defect }}"
    }



    private fun showConfirmDialog() {

        if (doc.products.isEmpty()) {
            Toast.makeText(this, "Список товаров пуст", Toast.LENGTH_SHORT).show()
            return
        }

        val totalItems = doc.products.size
        val totalQty = doc.products.sumOf {it.quantity}
        val totalDefect = doc.products.sumOf { it.defect }

        val message = """
        Накладная: ${tvInvoice.text}
        
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
        // ПОКА ЗАГЛУШКА
        Toast.makeText(
            this,
            "Возврат подтверждён (пока локально)",
            Toast.LENGTH_LONG
        ).show()

        // TODO:
        // 1. Сформировать объект возврата
        // 2. Отправить в 1С
        // 3. Или сохранить офлайн
        // 4. Закрыть экран
    }


}