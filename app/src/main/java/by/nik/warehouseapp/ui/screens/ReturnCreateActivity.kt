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

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_return_create)

        val etInvoice = findViewById<TextInputEditText>(R.id.etInvoice)
        val etDate = findViewById<TextInputEditText>(R.id.etDate)
        val etContracrot = findViewById<TextInputEditText>(R.id.etContractor)
        val btnNext = findViewById<MaterialButton>(R.id.btnNext)

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
            val intent = Intent(this, ReturnItemsActivity::class.java)
            intent.putExtra("invoice", etInvoice.text.toString())
            intent.putExtra("date", etDate.text.toString())
            intent.putExtra("contractor", etContracrot.text.toString())
            startActivity(intent)
        }
    }

}