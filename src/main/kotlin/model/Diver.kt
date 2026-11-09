package model
import java.time.LocalDate

data class Diver(
    var diverId: Int,
    var fullName: String,
    var dob: LocalDate,
    var email: String,
    var certification: String,
    var previousDives: Int
)
