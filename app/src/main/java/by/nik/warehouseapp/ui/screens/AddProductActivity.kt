package by.nik.warehouseapp.ui.screens

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.widget.addTextChangedListener
import by.nik.warehouseapp.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton



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

        val position = intent.getIntExtra("position", -1)

        // режим редактирования
        if (position >= 0) {
            etCode.setText(intent.getStringExtra("code"))
            etQty.setText(intent.getStringExtra("qty"))
            etDefect.setText(intent.getStringExtra("defect"))
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
            clearErrors()

            val code = etCode.text?.toString()?.trim().orEmpty()
            val qtyText = etQty.text?.toString()?.trim().orEmpty()
            val defectText = etDefect.text?.toString()?.trim().orEmpty()

            val qty = qtyText.toIntOrNull()
            val defect = defectText.toIntOrNull() ?: 0

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

            if (defect < 0) {
                ok = false
                if (showErrors) tilDefect.error = "Брак не может быть меньше 0"
            }

            if (qty != null && defect > qty) {
                ok = false
                if (showErrors) tilDefect.error = "Брак не может быть больше количества"
            }

            btnAdd.isEnabled = ok
            return ok
        }

        // Первичная проверка (например, в режиме редактирования)
        validate(showErrors = false)

        // Фокус в код сразу (удобно для ТСД/сканера)
        etCode.requestFocus()
        etCode.post {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(etCode, InputMethodManager.SHOW_IMPLICIT)
        }

// Enter/Next логика для склада
        etCode.setOnEditorActionListener { _, actionId, event ->
            val isEnter = event?.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN
            if (actionId == EditorInfo.IME_ACTION_NEXT || isEnter) {
                etQty.requestFocus()
                true
            } else false
        }

        etQty.setOnEditorActionListener { _, actionId, event ->
            val isEnter = event?.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN
            if (actionId == EditorInfo.IME_ACTION_NEXT || isEnter) {
                etDefect.requestFocus()
                true
            } else false
        }

        etDefect.setOnEditorActionListener { _, actionId, event ->
            val isEnter = event?.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN
            if (actionId == EditorInfo.IME_ACTION_DONE || isEnter) {
                // Нажимаем кнопку, если всё валидно
                btnAdd.performClick()
                true
            } else false
        }


        // Проверка “на лету”
        etCode.addTextChangedListener { validate(showErrors = false) }
        etQty.addTextChangedListener { validate(showErrors = false) }
        etDefect.addTextChangedListener { validate(showErrors = false) }

        btnAdd.setOnClickListener {
            if (!validate(showErrors = true)) return@setOnClickListener

            val code = etCode.text?.toString()?.trim().orEmpty()
            val qty = etQty.text?.toString()?.trim().orEmpty()          // уже валидно
            val defect = etDefect.text?.toString()?.trim().orEmpty()    // может быть пусто

            val result = Intent().apply {
                putExtra("code", code)
                putExtra("qty", qty)
                putExtra("defect", defect.ifEmpty { "0" })
                putExtra("position", position)
            }
            setResult(Activity.RESULT_OK, result)
            finish()
        }

        val fabScan = findViewById<ExtendedFloatingActionButton>(R.id.fabScan)

        fabScan.setOnClickListener {
            // Пока "сканер" = быстро подготовить поле для ввода от ТСД/сканера
            tilCode.error = null
            etCode.requestFocus()
            etCode.setSelection(etCode.text?.length ?: 0)
        }

    }
}
