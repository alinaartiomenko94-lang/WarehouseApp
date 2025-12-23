package by.nik.warehouseapp.ui.screens

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

            val intent = Intent(this, ReturnItemsActivity::class.java).apply {
                intent.putExtra("invoice", etInvoice.text.toString().trim())
                intent.putExtra("date", etDate.text.toString().trim())
                intent.putExtra("contractor", etContractor.text.toString().trim())
            }

            startActivity(intent)
        }
    }

    private fun validateCreateReturn(): Boolean {

        val invoice = etInvoice.text?.toString()?.trim().orEmpty()
        val date = etDate.text?.toString()?.trim().orEmpty()
        val contractor = etContractor.text?.toString()?.trim().orEmpty()

        if(invoice.isEmpty()) {
            etInvoice.error = "Введите номер ТТН"
            etInvoice.requestFocus()
            return false
        }

        if(date.isEmpty()) {
            etDate.error = "Укажите дату ТТН"
            etDate.requestFocus()
            return false
        }

        if(contractor.isEmpty()) {
            etContractor.error = "Введите контрагента"
            etContractor.requestFocus()
            return false
        }

        return true

    }

}