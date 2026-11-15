package utils
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.ResolverStyle

fun readNextInt(prompt: String = ""): Int {
    while (true) {
        print(prompt)

        val value = readln().trim().toIntOrNull()
        if (value != null) {
            return value
        }

        println("Please enter a valid integer.")
    }
}

fun readNextPositiveInt(prompt: String = "", minimum:Int = 0): Int {
    while (true) {
        val value = readNextInt(prompt)

        if (value >= minimum) {
            return value
        }

        println("Please enter a number of $minimum or greater.")
    }
}

fun readNextDate(prompt: String = ""): LocalDate {
    val formatter = DateTimeFormatter.ofPattern("dd/MM/uuuu")
        .withResolverStyle(ResolverStyle.STRICT)

    while (true) {
        print(prompt)

        val dob = try {
            LocalDate.parse(readln(), formatter)
        } catch (_: Exception) {
            null
        }

        if (dob != null && !dob.isAfter(LocalDate.now())) {
            return dob
        }

        println("Please enter a valid date.")
    }
}

fun readNextName(prompt: String = ""): String {
    while (true) {
        print(prompt)
        val name = readln().trim()

        if (name.isNotEmpty() && name.all { it.isLetter() || it.isWhitespace() }) {
            return name
        }

        println("Please enter a valid name.")
    }
}

fun readNextEmail(prompt: String = ""): String {
    while (true) {
        print(prompt)
        val email = readln().trim()

        if (email.matches(Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"))) {
            return email
        }

        println("Please enter a valid email address.")
    }
}

fun readNextCertification(prompt: String = ""): String {
    while (true) {
        print(prompt)
        val certification = readln().trim()

        if (certification.isNotEmpty() &&
            certification.all { it.isLetter() || it.isWhitespace() }) {
            return certification
        }

        println("Please enter a valid certification.")
    }
}

fun readNextRequiredText(prompt: String = ""): String {
    while (true) {
        print(prompt)
        val value = readln().trim()

        if (value.isNotEmpty()) {
            return value
        }

        println("This field cannot be empty.")
    }
}

fun readNextPositiveDouble(prompt: String = ""): Double {
    while (true) {
        print(prompt)
        val value = readln().trim().toDoubleOrNull()

        if (value != null && value.isFinite() && value > 0) {
            return value
        }

        println("Please enter a valid number greater than 0.")
    }
}

fun readNextWaterTemperature(prompt: String = ""): Double {
    while (true) {
        print(prompt)
        val value = readln().trim().toDoubleOrNull()

        if (value != null && value.isFinite() && value in -2.0..50.0) {
            return value
        }

        println("Please enter a temperature between -2 and 50°C.")
    }
}
