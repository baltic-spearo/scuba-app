package model
import java.time.LocalDate

data class LoggedDive(
    var diveId: Int = 0,
    var location: String = "",
    var diveDate: LocalDate = LocalDate.now(),
    var maxDepth: Double = 0.0,
    var duration: Int = 0,
    var waterTemperature: Double = 0.0,
    var visibility: Int = 0,
    var diveType: String = "",
    var notes: String = "",
    var isArchived: Boolean = false
)
