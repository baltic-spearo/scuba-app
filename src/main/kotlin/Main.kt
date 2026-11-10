import java.time.format.DateTimeFormatter
import io.github.oshai.kotlinlogging.KotlinLogging
import service.DiverService
import model.Diver
import utils.readNextInt
import utils.readNextPositiveInt
import utils.readNextDate
import utils.readNextName
import utils.readNextEmail
import utils.readNextCertification

private val logger = KotlinLogging.logger {}
val diverService = DiverService()


fun main(){
    logger.info { "Scuba Dive Log App Started" }
    runMenu()
}

fun mainMenu(): Int {
    println("""
        ┌──────────────────────────────────┐
        │         SCUBA DIVE LOG           │
        ├──────────────────────────────────┤
        │           DIVER MENU             │
        ├──────────────────────────────────┤
        │  1) Add Diver                    │
        │  2) Display All Divers           │
        │  3) Update Diver                 │
        │  4) Delete Diver                 │
        │  5) Find Diver by ID             │
        ├──────────────────────────────────┤
        │  0) Exit                         │
        └──────────────────────────────────┘
        """.trimIndent())
    while (true) {
        val option = readNextInt("Enter menu choice (0-5): ")

        if (option in 0..5) {
            return option
        }

        println("Please enter a number between 0 and 5.")
    }
}

fun runMenu (){
    var input : Int
    do {
        input = mainMenu()
        when(input) {
            1 -> addDiver()
            2 -> displayDiver()
            3 -> updateDiver()
            4 -> deleteDiver()
            5 -> findDiverById()
            0 -> logger.info { "Scuba Dive Log App Exiting" }
        }
    } while (input != 0)
}

fun addDiver(){
    val diver = readDiverDetails(0)

    diverService.addDiver(diver)

    println("Diver added")
    println()
}

fun displayDiver(){
    val divers = diverService.getDivers()
    if (divers.isEmpty()){
        println("No Divers found.")
    }else{
        println("\n============ ALL DIVERS ============")
        for (diver in divers){
            printDiver(diver)
        }
        println("────────────────────────────────\n")
    }
}

fun findDiverById(){
    val id = readNextInt("Enter Diver ID: ")
    val diver = diverService.findDiverById(id)

    if (diver != null) {
        printDiver(diver)
    } else {
        println("Diver not found.")
    }
    println()
}

fun deleteDiver() {
    val id = readNextInt("Enter Diver ID to Delete: ")

    if (diverService.deleteDiver(id)) {
        println("Deleted")
    } else {
        println(" ID Not found")
    }
}

fun updateDiver() {
    val id = readNextInt("Enter Diver ID to Update: ")

    if (diverService.findDiverById(id) == null) {
        println("Diver not found.")
        return
    }

    val updated = readDiverDetails(id)

    if (diverService.updateDiver(id, updated)) {
        println("Diver updated")
    } else {
        println("Diver not found")
    }
    println()
}

fun printDiver(diver: Diver) {
    println("──────────────────────────────────")
    println("ID: ${diver.diverId}")
    println("Name: ${diver.fullName}")
    println("Date of Birth: ${diver.dob.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}")
    println("Email: ${diver.email}")
    println("Certification: ${diver.certification}")
    println("Previous Dives: ${diver.previousDives}")
}

fun readDiverDetails(id: Int): Diver {
    val fullName = readNextName("Name: ")
    val dob = readNextDate("Date Of Birth (DD/MM/YYYY): ")
    val email = readNextEmail("Email: ")
    val certification = readNextCertification("Certification: ")
    val previousDives = readNextPositiveInt("Previous Dives: ")

    return Diver(id, fullName, dob, email, certification, previousDives)
}
