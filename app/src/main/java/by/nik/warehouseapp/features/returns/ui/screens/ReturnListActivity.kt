package by.nik.warehouseapp.features.returns.ui.screens

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.nik.warehouseapp.R
import by.nik.warehouseapp.core.data.RepositoryProvider
import by.nik.warehouseapp.features.returns.model.ReturnDocument
import by.nik.warehouseapp.features.returns.ui.adapter.ReturnListAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText

class ReturnListActivity : AppCompatActivity() {

    private val repo = RepositoryProvider.returnRepository
    private lateinit var adapter: ReturnListAdapter

    private var allReturns: List<ReturnDocument> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_return_list)

        val recyclerView = findViewById<RecyclerView>(R.id.rcReturns)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = ReturnListAdapter(mutableListOf()) { returnId ->
            openReturn(returnId)
        }
        recyclerView.adapter = adapter

        val etSearch = findViewById<TextInputEditText>(R.id.etSearch)
        etSearch.addTextChangedListener { s ->
            applyFilter(s?.toString().orEmpty())
        }

        findViewById<FloatingActionButton>(R.id.fabAddReturn).setOnClickListener {
            startActivity(Intent(this, ReturnCreateActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        allReturns = repo.getAll()
        applyFilter(findViewById<TextInputEditText>(R.id.etSearch).text?.toString().orEmpty())
    }

    private fun applyFilter(query: String) {
        val q = query.trim().lowercase()
        val filtered = if (q.isBlank()) {
            allReturns
        } else {
            allReturns.filter { doc ->
                doc.invoice.lowercase().contains(q) ||
                        doc.contractor.lowercase().contains(q)
            }
        }
        adapter.update(filtered)
    }

    private fun openReturn(returnId: Long) {
        val intent = Intent(this, ReturnItemsActivity::class.java)
        intent.putExtra("returnId", returnId)
        startActivity(intent)
    }
}
