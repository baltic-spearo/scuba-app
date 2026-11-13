package model
import java.time.LocalDate

data class LoggedDive(
    var diveId: Int = 0,
    var location: String,
    var diveDate: LocalDate,
    var maxDepth: Double,
    var duration: Int,
    var waterTemperature: Double,
    var visibility: Int,
    var diveType: String,
    var notes: String,
    var isArchived: Boolean = false
)
