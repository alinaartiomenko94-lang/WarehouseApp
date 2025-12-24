package by.nik.warehouseapp.ui.screens

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.nik.warehouseapp.R
import by.nik.warehouseapp.data.RepositoryProvider
import by.nik.warehouseapp.model.ReturnDocument
import by.nik.warehouseapp.model.ReturnStatus
import by.nik.warehouseapp.ui.adapter.ReturnListAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ReturnListActivity : AppCompatActivity() {

    private val CREATE_RETURN_REQUEST = 101
    private val returns = mutableListOf<ReturnDocument>()
    private val repo = RepositoryProvider.returnRepository
    private lateinit var adapter: ReturnListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_return_list)

        val recyclerView = findViewById<RecyclerView>(R.id.rcReturns)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = ReturnListAdapter(repo.getAll()) { returnDoc ->
            val intent = Intent(this, ReturnItemsActivity::class.java).apply {
                putExtra("returnId", returnDoc.id)
            }
            startActivity(intent)
        }

        recyclerView.adapter = adapter

        findViewById<FloatingActionButton>(R.id.fabAddReturn).setOnClickListener {
            val intent = Intent(this, ReturnCreateActivity::class.java)
            startActivityForResult(intent, CREATE_RETURN_REQUEST)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == CREATE_RETURN_REQUEST && resultCode == Activity.RESULT_OK) {
            val invoice = data?.getStringExtra("invoice") ?: return
            val date = data.getStringExtra("date") ?: return
            val contractor = data.getStringExtra("contractor") ?: return

            val newReturn = ReturnDocument(
                id = System.currentTimeMillis(),
                invoice = invoice,
                date = date,
                contractor = contractor,
                status = ReturnStatus.CREATED,
                products = mutableListOf()
            )

            returns.add(0, newReturn)
            adapter.notifyItemInserted(0)
        }
    }

    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()
    }
}