package by.nik.warehouseapp.ui.screens

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import by.nik.warehouseapp.R
import by.nik.warehouseapp.model.ReturnDocument
import by.nik.warehouseapp.model.ReturnStatus
import by.nik.warehouseapp.ui.adapter.ReturnListAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ReturnListActivity : AppCompatActivity() {

    private val returns = mutableListOf<ReturnDocument>()

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_return_list)

        // заглушка данных
        returns.add(
            ReturnDocument(
                id = 1,
                invoice = "12345",
                date = "12.09.2025",
                contractor = "ООО Ромашка",
                status = ReturnStatus.CREATED,
                products = mutableListOf()
            )
        )

        val adapter = ReturnListAdapter(returns) { returnDoc ->
            val intent = Intent(this, ReturnItemsActivity::class.java).apply {
                putExtra("invoice", returnDoc.invoice)
                putExtra("date", returnDoc.date)
                putExtra("contractor", returnDoc.contractor)
            }
            startActivity(intent)
        }

        val rc = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rcReturns)
        rc.layoutManager = LinearLayoutManager(this)
        rc.adapter = adapter

        findViewById<FloatingActionButton>(R.id.fabAddReturn).setOnClickListener {
            startActivity(Intent(this, ReturnCreateActivity::class.java))
        }
    }
}