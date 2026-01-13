package by.nik.warehouseapp.features.returns.ui.screens

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.widget.addTextChangedListener
import by.nik.warehouseapp.R
import by.nik.warehouseapp.core.data.RepositoryProvider
import by.nik.warehouseapp.features.products.ui.ProductSelectActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.textview.MaterialTextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.card.MaterialCardView

class AddProductActivity : AppCompatActivity() {

    private val SELECT_PRODUCT_REQUEST = 200

    // выбранный товар (пока не используем в ReturnProduct, но держим для будущего)
    private var selectedProductId: Long? = null
    private var selectedBarcode: String? = null
    private var selectedArticle: String? = null
    private var selectedName: String? = null
    private var selectedNomCode: String? = null

    private val productRepo by lazy { RepositoryProvider.productRepository }

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)

        val tilCode = findViewById<TextInputLayout>(R.id.tilCode)
        val tilQty = findViewById<TextInputLayout>(R.id.tilQty)
        val tilDefect = findViewById<TextInputLayout>(R.id.tilDefect)

        val etCode = findViewById<TextInputEditText>(R.id.etCode)
        val etQty = findViewById<TextInputEditText>(R.id.etQty)
        val etDefect = findViewById<TextInputEditText>(R.id.etDefect)

        val btnAdd = findViewById<MaterialButton>(R.id.btnAdd)
        val fabScan = findViewById<ExtendedFloatingActionButton>(R.id.fabScan)

        // Блок карточки товара (добавь в XML как я написала)
        val cardInfo = findViewById<MaterialCardView>(R.id.cardProductInfo)
        val tvName = findViewById<MaterialTextView>(R.id.tvProductName)
        val tvMeta = findViewById<MaterialTextView>(R.id.tvProductMeta)

        val position = intent.getIntExtra("position", -1)

        // --- режим редактирования ---
        if (position >= 0) {
            // старое поведение оставляем: код/кол-во/брак заполняем как раньше
            etCode.setText(intent.getStringExtra("code").orEmpty())
            etQty.setText(intent.getStringExtra("qty").orEmpty())
            etDefect.setText(intent.getStringExtra("defect").orEmpty())
            btnAdd.text = "Сохранить"
        } else {
            btnAdd.text = "Добавить товар"
        }

        fun clearErrors() {
            tilCode.error = null
            tilQty.error = null
            tilDefect.error = null
        }

        fun clearSelectedProductUi() {
            selectedProductId = null
            selectedBarcode = null
            selectedArticle = null
            selectedName = null
            selectedNomCode = null
            cardInfo.visibility = android.view.View.GONE
        }

        fun renderSelectedProduct() {
            if (selectedProductId == null) {
                cardInfo.visibility = android.view.View.GONE
                return
            }
            tvName.text = selectedName ?: "—"
            val bc = selectedBarcode ?: "—"
            val art = selectedArticle ?: "—"
            val nom = selectedNomCode ?: "—"
            tvMeta.text = "Код: $nom | Арт: $art | ШК: $bc"
            cardInfo.visibility = android.view.View.VISIBLE
        }

        fun validate(showErrors: Boolean): Boolean {
            if (showErrors) clearErrors()

            val codeText = etCode.text?.toString()?.trim().orEmpty()
            val qtyText = etQty.text?.toString()?.trim().orEmpty()
            val defectText = etDefect.text?.toString()?.trim().orEmpty()

            val qty = qtyText.toIntOrNull()
            val defect = if (defectText.isEmpty()) 0 else defectText.toIntOrNull()

            var ok = true

            // Код/поиск: разрешаем либо введённый текст, либо выбранный товар
            if (codeText.isEmpty() && selectedProductId == null) {
                ok = false
                if (showErrors) tilCode.error = "Сканируйте или найдите товар"
            }

            if (qty == null) {
                ok = false
                if (showErrors) tilQty.error = "Введите количество"
            } else if (qty <= 0) {
                ok = false
                if (showErrors) tilQty.error = "Количество должно быть больше 0"
            }

            if (defect == null) {
                ok = false
                if (showErrors) tilDefect.error = "Введите число (или оставьте пустым)"
            } else {
                if (defect < 0) {
                    ok = false
                    if (showErrors) tilDefect.error = "Брак не может быть меньше 0"
                }
                if (qty != null && defect > qty) {
                    ok = false
                    if (showErrors) tilDefect.error = "Брак не может быть больше количества"
                }
            }

            btnAdd.isEnabled = ok
            return ok
        }

        fun applySelectedFromSearch(
            productId: Long,
            nomCode: String,
            name: String,
            article: String,
            barcode: String?
        ) {
            selectedProductId = productId
            selectedNomCode = nomCode
            selectedName = name
            selectedArticle = article
            selectedBarcode = barcode

            // Поле "код" заполняем тем, что реально сканируют чаще всего:
            // если есть штрихкод — ставим его, иначе артикул
            etCode.setText((barcode?.takeIf { it.isNotBlank() } ?: article).trim())
            etCode.setSelection(etCode.text?.length ?: 0)

            renderSelectedProduct()
            validate(showErrors = false)

            // после выбора товара — фокус на количество
            etQty.requestFocus()
        }

        fun resolveProduct(queryRaw: String) {
            val query = queryRaw.trim()
            if (query.isEmpty()) {
                clearSelectedProductUi()
                validate(showErrors = false)
                return
            }

            // 1) пробуем как штрихкод (точное совпадение)
            val byBarcode = productRepo.findByBarcode(query)
            if (byBarcode != null) {
                applySelectedFromSearch(
                    productId = byBarcode.id,
                    nomCode = byBarcode.nomenclatureCode,
                    name = byBarcode.name,
                    article = byBarcode.article,
                    barcode = byBarcode.barcode
                )
                return
            }

            // 2) общий поиск (артикул/название)
            val list = productRepo.search(query)
            when {
                list.isEmpty() -> {
                    clearSelectedProductUi()
                    if (query.length >= 3) {
                        Toast.makeText(this, "Товар не найден", Toast.LENGTH_SHORT).show()
                    }
                    validate(showErrors = false)
                }
                list.size == 1 -> {
                    val p = list.first()
                    applySelectedFromSearch(
                        productId = p.id,
                        nomCode = p.nomenclatureCode,
                        name = p.name,
                        article = p.article,
                        barcode = p.barcode
                    )
                }
                else -> {
                    // несколько результатов — открываем выбор
                    val intent = Intent(this, ProductSelectActivity::class.java).apply {
                        putExtra("query", query)
                    }
                    startActivityForResult(intent, SELECT_PRODUCT_REQUEST)
                }
            }
        }

        // Валидация “на лету”
        etQty.addTextChangedListener { validate(showErrors = false) }
        etDefect.addTextChangedListener { validate(showErrors = false) }

        // Если пользователь руками меняет поле кода — сбрасываем выбранный товар,
        // чтобы не было ситуации "карточка от одного, код от другого".
        etCode.addTextChangedListener {
            tilCode.error = null
            clearSelectedProductUi()
            validate(showErrors = false)
        }

        fun isEnter(event: KeyEvent?) =
            event?.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN

        // Enter/Next/Done логика + поиск
        etCode.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_NEXT || isEnter(event) || actionId == EditorInfo.IME_ACTION_DONE) {
                resolveProduct(etCode.text?.toString().orEmpty())
                true
            } else false
        }

        etQty.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_NEXT || isEnter(event)) {
                etDefect.requestFocus()
                true
            } else false
        }

        etDefect.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || isEnter(event)) {
                btnAdd.performClick()
                true
            } else false
        }

        // Кнопка "Скан" — фокус в поле + очистка ошибки
        fabScan.setOnClickListener {
            tilCode.error = null
            etCode.requestFocus()
            etCode.setSelection(etCode.text?.length ?: 0)
        }

        btnAdd.setOnClickListener {
            if (!validate(showErrors = true)) return@setOnClickListener

            // что отдаём назад в ReturnItems:
            // пока система ждёт "code" строкой, отдаём:
            // - если выбран товар: barcode (если есть) иначе article
            // - иначе то, что ввели вручную
            val codeOut = if (selectedProductId != null) {
                (selectedBarcode?.takeIf { it.isNotBlank() } ?: selectedArticle).orEmpty()
            } else {
                etCode.text?.toString()?.trim().orEmpty()
            }

            val qty = etQty.text?.toString()?.trim().orEmpty()
            val defect = etDefect.text?.toString()?.trim().orEmpty().ifEmpty { "0" }

            val result = Intent().apply {
                putExtra("code", codeOut)
                putExtra("qty", qty)
                putExtra("defect", defect)
                putExtra("position", position)

                // extras на будущее (когда перейдём на productId в ReturnProduct)
                selectedProductId?.let { putExtra("productId", it) }
                putExtra("name", selectedName ?: "")
                putExtra("article", selectedArticle ?: "")
                putExtra("barcode", selectedBarcode ?: "")
                putExtra("nomenclatureCode", selectedNomCode ?: "")
            }
            setResult(RESULT_OK, result)
            finish()
        }

        // Первичная валидация и фокус
        validate(showErrors = false)
        etCode.requestFocus()
        etCode.setSelection(etCode.text?.length ?: 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SELECT_PRODUCT_REQUEST && resultCode == RESULT_OK && data != null) {
            val productId = data.getLongExtra("productId", -1L)
            val name = data.getStringExtra("name").orEmpty()
            val article = data.getStringExtra("article").orEmpty()
            val barcode = data.getStringExtra("barcode")?.takeIf { it.isNotBlank() }
            val nomCode = data.getStringExtra("nomenclatureCode").orEmpty()

            if (productId > 0) {
                // применяем выбор
                selectedProductId = productId
                selectedName = name
                selectedArticle = article
                selectedBarcode = barcode
                selectedNomCode = nomCode

                // заполняем etCode и двигаемся дальше
                val codeText = (barcode ?: article).trim()
                val etCode = findViewById<TextInputEditText>(R.id.etCode)
                etCode.setText(codeText)
                etCode.setSelection(etCode.text?.length ?: 0)

                // показать карточку
                findViewById<MaterialCardView>(R.id.cardProductInfo).visibility = android.view.View.VISIBLE
                findViewById<MaterialTextView>(R.id.tvProductName).text = name
                findViewById<MaterialTextView>(R.id.tvProductMeta).text =
                    "Код: $nomCode | Арт: $article | ШК: ${barcode ?: "—"}"

                findViewById<TextInputEditText>(R.id.etQty).requestFocus()
                findViewById<MaterialButton>(R.id.btnAdd).isEnabled = true
            }
        }
    }
}
