package by.nik.warehouseapp.features.returns.ui.screens

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.widget.addTextChangedListener
import by.nik.warehouseapp.R
import by.nik.warehouseapp.core.data.product.InMemoryProductRepository
import by.nik.warehouseapp.features.products.ui.ProductSelectActivity
import by.nik.warehouseapp.features.returns.model.ProductItem
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.textview.MaterialTextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class AddProductActivity : AppCompatActivity() {

    private var selected: ProductItem? = null
    private val SELECT_PRODUCT_REQUEST = 200

    private lateinit var cardProduct: MaterialCardView
    private lateinit var ivProduct: ImageView
    private lateinit var tvProductName: MaterialTextView
    private lateinit var tvProductMeta: MaterialTextView
    private lateinit var etCode: TextInputEditText
    private lateinit var etQty: TextInputEditText


    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)

        val tilCode = findViewById<TextInputLayout>(R.id.tilCode)
        val tilQty = findViewById<TextInputLayout>(R.id.tilQty)
        val tilDefect = findViewById<TextInputLayout>(R.id.tilDefect)

       val etDefect = findViewById<TextInputEditText>(R.id.etDefect)

        val btnAdd = findViewById<MaterialButton>(R.id.btnAdd)
        val fabScan = findViewById<ExtendedFloatingActionButton>(R.id.fabScan)

        cardProduct = findViewById(R.id.cardProduct)
        ivProduct = findViewById(R.id.ivProduct)
        tvProductName = findViewById(R.id.tvProductName)
        tvProductMeta = findViewById(R.id.tvProductMeta)

        etCode = findViewById(R.id.etCode)
        etQty = findViewById(R.id.etQty)


        val position = intent.getIntExtra("position", -1)

        fun clearErrors() {
            tilCode.error = null
            tilQty.error = null
            tilDefect.error = null
        }

        fun showProduct(p: ProductItem?) {
            selected = p
            if (p == null) {
                cardProduct.visibility = android.view.View.GONE
                return
            }
            cardProduct.visibility = android.view.View.VISIBLE
            ivProduct.setImageResource(p.imageRes)
            tvProductName.text = p.name
            tvProductMeta.text = "Код: ${p.nn} | Арт: ${p.article} | ШК: ${p.barcode}"
        }

        fun resolveProduct(query: String) {
            val q = query.trim()
            if (q.isEmpty()) {
                showProduct(null)
                return
            }

            val found = InMemoryProductRepository.findByAnyCode(q)

            when {
                found.isEmpty() -> {
                    showProduct(null)
                    Toast.makeText(this, "Товар не найден", Toast.LENGTH_SHORT).show()
                }
                found.size == 1 -> {
                    showProduct(found.first())
                    etQty.requestFocus()
                }
                else -> {
                    val intent = Intent(this, ProductSelectActivity::class.java)
                    intent.putExtra("query", q)
                    startActivityForResult(intent, SELECT_PRODUCT_REQUEST)
                }
            }
        }

        etCode.addTextChangedListener { clearErrors() }

        etCode.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                event?.keyCode == KeyEvent.KEYCODE_ENTER
            ) {
                resolveProduct(etCode.text?.toString().orEmpty())
                true
            } else false
        }

        etCode.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                resolveProduct(etCode.text?.toString().orEmpty())
                true
            } else false
        }

        fabScan.setOnClickListener {
            etCode.requestFocus()
            resolveProduct(etCode.text?.toString().orEmpty())
        }

        btnAdd.setOnClickListener {
            val code = etCode.text?.toString()?.trim().orEmpty()
            val qty = etQty.text?.toString()?.trim().orEmpty()
            val defect = etDefect.text?.toString()?.trim().orEmpty().ifEmpty { "0" }

            val result = Intent().apply {
                putExtra("code", code)
                putExtra("qty", qty)
                putExtra("defect", defect)
                putExtra("position", position)
            }
            setResult(RESULT_OK, result)
            finish()
        }

        etCode.requestFocus()
    }

    private fun showProduct(p: ProductItem?) {
        selected = p
        if (p == null) {
            cardProduct.visibility = android.view.View.GONE
            return
        }
        cardProduct.visibility = android.view.View.VISIBLE
        ivProduct.setImageResource(p.imageRes)
        tvProductName.text = p.name
        tvProductMeta.text = "Код: ${p.nn} | Арт: ${p.article} | ШК: ${p.barcode}"
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SELECT_PRODUCT_REQUEST && resultCode == RESULT_OK && data != null) {
            val id = data.getLongExtra("productId", -1L)
            if (id <= 0) return

            val p = ProductItem(
                id = id,
                nn = data.getStringExtra("nn").orEmpty(),
                name = data.getStringExtra("name").orEmpty(),
                article = data.getStringExtra("article").orEmpty(),
                barcode = data.getStringExtra("barcode").orEmpty(),
                imageRes = data.getIntExtra("imageRes", R.drawable.ic_toy_placeholder)
            )
            showProduct(p)
            etCode.setText(if (p.barcode.isNotBlank()) p.barcode else p.article)
            etQty.requestFocus()
        }
    }
}
