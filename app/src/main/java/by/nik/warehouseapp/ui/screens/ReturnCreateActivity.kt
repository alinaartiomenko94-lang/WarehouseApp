package by.nik.warehouseapp.ui.screens

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import by.nik.warehouseapp.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import java.util.Calendar

class ReturnCreateActivity : AppCompatActivity() {

    private lateinit var etInvoice: TextInputEditText
    private lateinit var  etDate: TextInputEditText
    private lateinit var  etContractor: TextInputEditText
    private lateinit var  btnNext: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_return_create)

        etInvoice = findViewById(R.id.etInvoice)
        etDate = findViewById(R.id.etDate)
        etContractor = findViewById(R.id.etContractor)
        btnNext = findViewById(R.id.btnNext)

        //Выбор даты
        etDate.setOnClickListener {
            val c = Calendar.getInstance()
            DatePickerDialog(
                this,
                {_, y, m, d ->
                    etDate.setText("%02d.%02d.%d".format(d, m + 1, y))
                },
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        //Переход на экран возврата
        btnNext.setOnClickListener {

            if(!validateCreateReturn()) return@setOnClickListener

            val result = Intent().apply {
                putExtra("invoice", etInvoice.text.toString().trim())
                putExtra("date", etDate.text.toString().trim())
                putExtra("contractor", etContractor.text.toString().trim())
            }

            setResult(Activity.RESULT_OK, result)
            finish()

        }
    }

    private fun validateCreateReturn(): Boolean {

        if(etInvoice.text.isNullOrBlank()) {
            etInvoice.error = "Введите номер ТТН"
            return false
        }

        if(etDate.text.isNullOrBlank()) {
            etDate.error="Выберите дату ТТН"
            return false
        }

        if(etContractor.text.isNullOrBlank()) {
            etContractor.error="Введите контрагента"
            return false
        }

        return true

    }

}