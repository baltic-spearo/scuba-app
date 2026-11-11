package model
import java.time.LocalDate

data class Diver(
    var diverId: Int = 0,
    var fullName: String = "",
    var dob: LocalDate = LocalDate.now(),
    var email: String = "",
    var certification: String = "",
    var previousDives: Int = 0
)
