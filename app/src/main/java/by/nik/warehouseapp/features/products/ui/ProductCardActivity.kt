package by.nik.warehouseapp.features.products.ui

import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import by.nik.warehouseapp.R
import by.nik.warehouseapp.core.data.RepositoryProvider
import com.google.android.material.textview.MaterialTextView

class ProductCardActivity : AppCompatActivity() {

    private val repo = RepositoryProvider.productRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_card)

        val productId = intent.getLongExtra("productId", -1L)
        if (productId <= 0) {
            Toast.makeText(this, "Товар не найден", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val product = repo.getById(productId)
        if (product == null) {
            Toast.makeText(this, "Товар не найден", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val iv = findViewById<ImageView>(R.id.ivProduct)
        val tvName = findViewById<MaterialTextView>(R.id.tvName)
        val tvNom = findViewById<MaterialTextView>(R.id.tvNomCode)
        val tvArticle = findViewById<MaterialTextView>(R.id.tvArticle)
        val tvBarcode = findViewById<MaterialTextView>(R.id.tvBarcode)

        tvName.text = product.name
        tvNom.text = product.nomenclatureCode
        tvArticle.text = product.article
        tvBarcode.text = product.barcode ?: "Нет штрихкода"

        // Картинка: пока простейший вариант через Uri (галерея/файл/потом сеть)
        val uriStr = product.imageUri
        if (!uriStr.isNullOrBlank()) {
            runCatching { iv.setImageURI(Uri.parse(uriStr)) }
        } else {
            iv.setImageResource(android.R.drawable.ic_menu_report_image)
        }
    }
}
