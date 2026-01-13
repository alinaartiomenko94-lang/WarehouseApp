package by.nik.warehouseapp.features.returns.ui.screens

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import by.nik.warehouseapp.R
import by.nik.warehouseapp.core.data.RepositoryProvider
import by.nik.warehouseapp.features.products.ui.ProductSelectActivity
import by.nik.warehouseapp.features.returns.model.ReturnProduct
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

class AddProductActivity : AppCompatActivity() {

    private lateinit var codeInput: TextInputEditText
    private lateinit var qtyInput: TextInputEditText
    private lateinit var defectInput: TextInputEditText

    private lateinit var btnAdd: MaterialButton
    private lateinit var btnScan: MaterialButton
    private lateinit var btnFind: MaterialButton

    private lateinit var cardProduct: MaterialCardView
    private lateinit var tvProductName: MaterialTextView
    private lateinit var tvProductMeta: MaterialTextView
    private lateinit var ivProduct: ImageView

    private val productRepository by lazy { RepositoryProvider.productRepository }
    private val returnRepository by lazy { RepositoryProvider.returnRepository }

    private var selectedProductId: Long? = null
    private var returnId: Long = -1L

    // Камера-сканер
    private val scanLauncher = registerForActivityResult(ScanContract()) { result ->
        val contents = result.contents ?: return@registerForActivityResult
        val barcode = contents.trim()
        codeInput.setText(barcode)
        resolveProduct(barcode)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)

        // ⬅️ ОБЯЗАТЕЛЬНО: returnId должен прийти из ReturnItemsActivity
        returnId = intent.getLongExtra(EXTRA_RETURN_ID, -1L)
        if (returnId <= 0) {
            Toast.makeText(this, "Ошибка: не передан returnId", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        bindViews()
        setupActions()
        resetForNextScan()
    }

    private fun bindViews() {
        codeInput = findViewById(R.id.etCode)
        qtyInput = findViewById(R.id.etQty)
        defectInput = findViewById(R.id.etDefect)

        btnAdd = findViewById(R.id.btnAdd)
        btnScan = findViewById(R.id.btnScan)
        btnFind = findViewById(R.id.btnFind)

        cardProduct = findViewById(R.id.cardProduct)
        tvProductName = findViewById(R.id.tvProductName)
        tvProductMeta = findViewById(R.id.tvProductMeta)
        ivProduct = findViewById(R.id.ivProduct)
    }

    private fun setupActions() {

        btnScan.setOnClickListener { startCameraScan() }

        btnFind.setOnClickListener {
            openFindProductScreen(codeInput.text?.toString().orEmpty())
        }

        // ENTER от аппаратного сканера
        codeInput.setOnKeyListener { _, keyCode, event ->
            val isEnter =
                keyCode == KeyEvent.KEYCODE_ENTER ||
                        keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER

            if (isEnter && event.action == KeyEvent.ACTION_DOWN) {
                resolveProduct(codeInput.text?.toString().orEmpty())
                true
            } else false
        }

        // Экранная клавиатура
        codeInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                actionId == EditorInfo.IME_ACTION_SEARCH
            ) {
                resolveProduct(codeInput.text?.toString().orEmpty())
                true
            } else false
        }

        btnAdd.setOnClickListener { onAddClicked() }
    }

    private fun startCameraScan() {
        val options = ScanOptions().apply {
            setPrompt("Наведите камеру на штрихкод")
            setBeepEnabled(true)
            setOrientationLocked(true)
        }
        scanLauncher.launch(options)
    }

    private fun openFindProductScreen(query: String) {
        val intent = android.content.Intent(this, ProductSelectActivity::class.java)
        intent.putExtra(ProductSelectActivity.EXTRA_QUERY, query)
        startActivityForResult(intent, REQUEST_SELECT_PRODUCT)
    }

    private fun resolveProduct(query: String) {
        val q = query.trim()
        if (q.isBlank()) return

        val exact = productRepository.findByBarcode(q)
        if (exact != null) {
            showProduct(exact.id)
            return
        }

        val list = productRepository.search(q)

        when {
            list.isEmpty() -> {
                clearSelectedProduct(true)
                showNotFoundDialog(q)
            }
            list.size == 1 -> showProduct(list.first().id)
            else -> openFindProductScreen(q)
        }
    }

    private fun showNotFoundDialog(query: String) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Товар не найден")
            .setMessage("Штрихкод: $query\n\nНайти вручную?")
            .setPositiveButton("Найти") { _, _ ->
                openFindProductScreen(query)
            }
            .setNegativeButton("Повторить скан") { _, _ ->
                startCameraScan()
            }
            .setNeutralButton("Отмена", null)
            .show()
    }

    private fun showProduct(productId: Long) {
        val p = productRepository.getById(productId) ?: return

        selectedProductId = p.id

        cardProduct.visibility = View.VISIBLE
        tvProductName.text = p.name
        tvProductMeta.text =
            "Код: ${p.nomenclatureCode} | Арт: ${p.article} | ШК: ${p.barcode ?: "—"}"

        btnAdd.isEnabled = true
        qtyInput.requestFocus()
    }

    private fun clearSelectedProduct(keepCode: Boolean) {
        selectedProductId = null
        cardProduct.visibility = View.GONE
        btnAdd.isEnabled = false
        if (!keepCode) codeInput.setText("")
    }

    private fun onAddClicked() {
        val productId = selectedProductId ?: return

        val qty = qtyInput.text?.toString()?.toIntOrNull() ?: 0
        val defect = defectInput.text?.toString()?.toIntOrNull() ?: 0

        if (qty <= 0 || defect < 0 || defect > qty) {
            Toast.makeText(this, "Некорректные данные", Toast.LENGTH_SHORT).show()
            return
        }

        val product = productRepository.getById(productId) ?: return

        // ✅ ВАЖНО: используем ТВОЙ репозиторий
        returnRepository.addProduct(
            returnId,
            ReturnProduct(
                code = product.nomenclatureCode,
                quantity = qty,
                defect = defect
            )
        )

        Toast.makeText(this, "Добавлено", Toast.LENGTH_SHORT).show()
        resetForNextScan()
    }

    private fun resetForNextScan() {
        clearSelectedProduct(false)
        qtyInput.setText("")
        defectInput.setText("")
        codeInput.requestFocus()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: android.content.Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_SELECT_PRODUCT && resultCode == RESULT_OK) {
            val id = data?.getLongExtra(ProductSelectActivity.EXTRA_PRODUCT_ID, -1) ?: -1
            if (id > 0) showProduct(id)
        }
    }

    companion object {
        private const val REQUEST_SELECT_PRODUCT = 1001
        const val EXTRA_RETURN_ID = "returnId"
    }
}
