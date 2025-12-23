package by.nik.warehouseapp.ui.screens

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.nik.warehouseapp.R
import by.nik.warehouseapp.model.ReturnProduct
import by.nik.warehouseapp.ui.adapter.ReturnProductAdapter
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textview.MaterialTextView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ReturnItemsActivity : AppCompatActivity(), ReturnProductAdapter.OnProductClickListener {

    private val ADD_PRODUCT_REQUEST = 100
    private lateinit var adapter: ReturnProductAdapter
    private val products = mutableListOf<ReturnProduct>()
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

        tvInvoice = findViewById(R.id.tvInvoice)
        tvDate = findViewById(R.id.tvDate)
        tvContractor = findViewById(R.id.tvContractor)

        tvInvoice.text = intent.getStringExtra("invoice")
        tvDate.text = intent.getStringExtra("date")
        tvContractor.text = intent.getStringExtra("contractor")


        val recyclerView = findViewById<RecyclerView>(R.id.rcProducts)

        adapter = ReturnProductAdapter(products,this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        val fabAddProduct = findViewById<FloatingActionButton>(R.id.fabAddProduct)
        fabAddProduct.setOnClickListener {
            val intent = Intent(this, AddProductActivity::class.java)
            startActivityForResult(intent, ADD_PRODUCT_REQUEST)
        }

        tvTotalItems = findViewById(R.id.tvTotalItems)
        tvTotalQty = findViewById(R.id.tvTotalQty)
        tvTotalDefect = findViewById(R.id.tvTotalDefect)

        val btnConfirm = findViewById<MaterialButton>(R.id.btnConfirm)

        btnConfirm.setOnClickListener {
            if (products.isEmpty()) {
                Toast.makeText(
                    this,
                    "Список товаров пуст",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            AlertDialog.Builder(this)
                .setTitle("Подтверждение возврата")
                .setMessage("Подтвердить возврат на ${products.size} позиций?")
                .setPositiveButton("Подтвердить") { _, _ ->
                    confirmReturn()
                }
                .setNegativeButton("Отмена", null)
                .show()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if(requestCode == ADD_PRODUCT_REQUEST && resultCode == Activity.RESULT_OK) {
            val code = data?.getStringExtra("code") ?: return
            val qty = data.getStringExtra("qty")?.toIntOrNull() ?: return
            val defect = data.getStringExtra("defect")?.toIntOrNull() ?: 0
            val position = data.getIntExtra("position", -1)

            val product = ReturnProduct(
                code = code,
                quantity = qty,
                defect = defect
            )

            if(position >= 0) {
                adapter.updateItem(position, product)
                updateSummary()
            } else {
                adapter.addItem(product)
                updateSummary()
            }


            // ПОКА просто логика-заглушка
            // позже добавим в RecyclerView
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
        AlertDialog.Builder(this)
            .setTitle("Удалить товар")
            .setMessage("Удалить ${product.code}?")
            .setPositiveButton("Удалить") { _, _ ->
                adapter.removeItem(position)
                updateSummary()
            }
            .setNegativeButton("Отмена", null)
            .show()
    }
    private fun updateSummary() {
        val totalItems = products.size
        val totalQty = products.sumOf { it.quantity }
        val totalDefect = products.sumOf { it.defect }

        tvTotalItems.text = "Позиций: $totalItems"
        tvTotalQty.text = "Всего: $totalQty"
        tvTotalDefect.text = "Брак: $totalDefect"
    }

    private fun showConfirmDialog() {

        if (products.isEmpty()) {
            Toast.makeText(this, "Список товаров пуст", Toast.LENGTH_SHORT).show()
            return
        }

        val totalItems = products.size
        val totalQty = products.sumOf {it.quantity}
        val totalDefect = products.sumOf { it.defect }

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