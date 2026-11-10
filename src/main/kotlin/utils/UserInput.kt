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

fun readNextPositiveInt(prompt: String = ""): Int {
    while (true) {
        val value = readNextInt(prompt)

        if (value >= 0) {
            return value
        }

        println("Please enter a number of 0 or greater.")
    }
}

fun readNextDate(prompt: String = ""): LocalDate {
    val formatter = DateTimeFormatter.ofPattern("dd/MM/uuuu")
        .withResolverStyle(ResolverStyle.STRICT)

    while (true) {
        print(prompt)

        val dob = try {
            LocalDate.parse(readln(), formatter)
        } catch (e: Exception) {
            null
        }

        if (dob != null && !dob.isAfter(LocalDate.now())) {
            return dob
        }

        println("Please enter a valid date of birth.")
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