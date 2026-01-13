package by.nik.warehouseapp.features.products.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import by.nik.warehouseapp.R
import by.nik.warehouseapp.core.data.RepositoryProvider
import by.nik.warehouseapp.features.products.ui.adapter.ProductSelectAdapter
import com.google.android.material.textfield.TextInputEditText

class ProductSelectActivity : AppCompatActivity() {

    private val repo = RepositoryProvider.productRepository
    private lateinit var adapter: ProductSelectAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_select)

        val etQuery = findViewById<TextInputEditText>(R.id.etQuery)
        val rv = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rvProducts)
        rv.layoutManager = LinearLayoutManager(this)

        adapter = ProductSelectAdapter(mutableListOf()) { product ->
            val data = Intent().apply {
                putExtra("productId", product.id)
                putExtra("barcode", product.barcode ?: "")
                putExtra("article", product.article)
                putExtra("name", product.name)
                putExtra("nomenclatureCode", product.nomenclatureCode)
            }
            setResult(RESULT_OK, data)
            finish()
        }
        rv.adapter = adapter

        val initialQuery = intent.getStringExtra("query") ?: ""
        etQuery.setText(initialQuery)
        refresh(initialQuery)

        etQuery.setOnEditorActionListener { v, _, _ ->
            refresh(v.text?.toString().orEmpty())
            true
        }
    }

    private fun refresh(q: String) {
        val query = q.trim()
        if (query.isEmpty()) {
            adapter.update(emptyList())
            return
        }
        // поиск по артикулу/названию
        val list = repo.search(query)
        adapter.update(list)
    }
}
