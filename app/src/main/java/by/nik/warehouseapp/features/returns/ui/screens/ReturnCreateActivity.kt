package by.nik.warehouseapp.features.returns.ui.screens

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import by.nik.warehouseapp.R
import by.nik.warehouseapp.core.data.RepositoryProvider
import by.nik.warehouseapp.features.returns.model.ReturnDocument
import by.nik.warehouseapp.features.returns.model.ReturnStatus
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import java.util.Calendar

class ReturnCreateActivity : AppCompatActivity() {

    private val repo = RepositoryProvider.returnRepository
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

            val newDoc = ReturnDocument(
                id = System.currentTimeMillis(),
                invoice = etInvoice.text.toString().trim(),
                date = etDate.text.toString().trim(),
                contractor = etContractor.text.toString().trim(),
                status = ReturnStatus.CREATED,
                products = mutableListOf()
            )

            repo.create(newDoc)

            startActivity(Intent(this, ReturnItemsActivity::class.java).apply {
                putExtra("returnId", newDoc.id)
            })
            finish()

            if (!validateCreateReturn()) return@setOnClickListener

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