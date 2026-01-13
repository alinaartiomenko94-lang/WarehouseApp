package by.nik.warehouseapp.features.products.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import by.nik.warehouseapp.R
import by.nik.warehouseapp.core.data.product.InMemoryProductRepository
import by.nik.warehouseapp.features.products.ui.adapter.ProductSelectAdapter
import com.google.android.material.textfield.TextInputEditText

class ProductSelectActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_select)

        val etQuery = findViewById<TextInputEditText>(R.id.etQuery)
        val rv = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rvProducts)
        rv.layoutManager = LinearLayoutManager(this)

        val adapter = ProductSelectAdapter(mutableListOf()) { p ->
            val data = Intent().apply {
                putExtra("productId", p.id)
                putExtra("nn", p.nn)
                putExtra("name", p.name)
                putExtra("article", p.article)
                putExtra("barcode", p.barcode)
                putExtra("imageRes", p.imageRes)
            }
            setResult(RESULT_OK, data)
            finish()
        }
        rv.adapter = adapter

        val initial = intent.getStringExtra("query").orEmpty()
        etQuery.setText(initial)

        fun refresh(q: String) {
            adapter.update(InMemoryProductRepository.findByAnyCode(q))
        }

        refresh(initial)

        etQuery.setOnEditorActionListener { v, _, _ ->
            refresh(v.text?.toString().orEmpty())
            true
        }
    }
}
