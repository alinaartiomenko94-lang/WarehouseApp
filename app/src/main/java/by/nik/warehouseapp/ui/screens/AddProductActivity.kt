package by.nik.warehouseapp.ui.screens

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import by.nik.warehouseapp.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class AddProductActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)

        val etCode = findViewById<TextInputEditText>(R.id.etCode)
        val etQty = findViewById<TextInputEditText>(R.id.etQty)
        val etDefect = findViewById<TextInputEditText>(R.id.etDefect)
        val btnAdd = findViewById<MaterialButton>(R.id.btnAdd)

        //позиция = признак редактирования
        val position = intent.getIntExtra("position", -1)

        //режим редактирования
        if (position >= 0) {
            etCode.setText(intent.getStringExtra("code"))
            etQty.setText(intent.getStringExtra("qty"))
            etDefect.setText(intent.getStringExtra("defect"))
            btnAdd.text="Сохранить"
        }

        btnAdd.setOnClickListener {

            val code = etCode.text?.toString()?.trim().orEmpty()
            val qty = etQty.text?.toString()?.trim().orEmpty()
            val defect = etDefect.text?.toString()?.ifEmpty { "0" }

            if(code.isEmpty() || qty.isEmpty()) {
                etCode.error = "Введите код товара"
                etQty.error = "Ввведите количество"
                return@setOnClickListener
            }


            val result = Intent().apply {
                putExtra("code", etCode.text.toString())
                putExtra("qty", etQty.text.toString())
                putExtra("defect", etDefect.text.toString())
                putExtra("position", position)
            }
            setResult(Activity.RESULT_OK, result)
            finish()
        }
    }

}