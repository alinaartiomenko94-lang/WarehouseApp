package by.nik.warehouseapp.core.data.legacy

import by.nik.warehouseapp.core.data.legacy.ReturnItem
import java.time.LocalDate

class ReturnDraft (
    val invoiceNumber: String,
    val invoiceDate: LocalDate,
    val contractorText: String,
    val items: MutableList<ReturnItem>
)