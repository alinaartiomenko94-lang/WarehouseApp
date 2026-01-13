package by.nik.warehouseapp.features.products.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.nik.warehouseapp.R
import by.nik.warehouseapp.core.data.RepositoryProvider
import by.nik.warehouseapp.features.products.ui.adapter.ProductSelectAdapter

class ProductSelectActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProductSelectAdapter

    private val productRepository by lazy {
        RepositoryProvider.productRepository
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_select)

        recyclerView = findViewById(R.id.rvProducts)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val query = intent.getStringExtra(EXTRA_QUERY).orEmpty()
        val products = productRepository.search(query)

        adapter = ProductSelectAdapter(products) { product ->
            val result = Intent().apply {
                putExtra(EXTRA_PRODUCT_ID, product.id)
            }
            setResult(Activity.RESULT_OK, result)
            finish()
        }

        recyclerView.adapter = adapter
    }

    companion object {
        const val EXTRA_QUERY = "extra_query"
        const val EXTRA_PRODUCT_ID = "extra_product_id"
    }
}
