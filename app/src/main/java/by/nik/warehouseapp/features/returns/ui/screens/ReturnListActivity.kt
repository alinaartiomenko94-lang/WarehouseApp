package by.nik.warehouseapp.ui.screens

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.nik.warehouseapp.R
import by.nik.warehouseapp.core.data.RepositoryProvider
import by.nik.warehouseapp.features.returns.ui.adapter.ReturnListAdapter
import by.nik.warehouseapp.features.returns.ui.screens.ReturnCreateActivity
import by.nik.warehouseapp.features.returns.ui.screens.ReturnItemsActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ReturnListActivity : AppCompatActivity() {

    private val repo = RepositoryProvider.returnRepository
    private lateinit var adapter: ReturnListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_return_list)

        val recyclerView = findViewById<RecyclerView>(R.id.rcReturns)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = ReturnListAdapter(repo.getAll()) { returnDoc ->
            startActivity(Intent(this, ReturnItemsActivity::class.java).apply {
                putExtra("returnId", returnDoc.id)
            })
        }
        recyclerView.adapter = adapter

        findViewById<FloatingActionButton>(R.id.fabAddReturn).setOnClickListener {
            startActivity(Intent(this, ReturnCreateActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()
    }
}
