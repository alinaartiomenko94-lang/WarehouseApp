package by.nik.warehouseapp.features.returns.ui.screens

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.widget.addTextChangedListener
import by.nik.warehouseapp.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class AddProductActivity : AppCompatActivity() {

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

        val position = intent.getIntExtra("position", -1)

        // Режим редактирования
        if (position >= 0) {
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

        fun validate(showErrors: Boolean): Boolean {
            if (showErrors) clearErrors()

            val code = etCode.text?.toString()?.trim().orEmpty()
            val qtyText = etQty.text?.toString()?.trim().orEmpty()
            val defectText = etDefect.text?.toString()?.trim().orEmpty()

            val qty = qtyText.toIntOrNull()
            val defect = if (defectText.isEmpty()) 0 else defectText.toIntOrNull()

            var ok = true

            if (code.isEmpty()) {
                ok = false
                if (showErrors) tilCode.error = "Введите код товара"
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

        // Валидация “на лету”
        etCode.addTextChangedListener { validate(showErrors = false) }
        etQty.addTextChangedListener { validate(showErrors = false) }
        etDefect.addTextChangedListener { validate(showErrors = false) }

        // Enter/Next/Done логика для ТСД
        fun isEnter(event: KeyEvent?) =
            event?.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN

        etCode.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_NEXT || isEnter(event)) {
                etQty.requestFocus()
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

        // Кнопка "Скан" — пока заглушка: просто фокус в поле кода
        fabScan.setOnClickListener {
            tilCode.error = null
            etCode.requestFocus()
            etCode.setSelection(etCode.text?.length ?: 0)
        }

        btnAdd.setOnClickListener {
            if (!validate(showErrors = true)) return@setOnClickListener

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

        // Первичная валидация и фокус (удобно для ТСД)
        validate(showErrors = false)
        etCode.requestFocus()
    }
}
