package by.nik.warehouseapp.data

import java.time.LocalDate

class ReturnDraft (
    val invoiceNumber: String,
    val invoiceDate: LocalDate,
    val contractorText: String,
    val items: MutableList<ReturnItem>
)