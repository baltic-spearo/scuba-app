import java.time.LocalDate
import java.time.format.DateTimeFormatter

import service.DiverService
import model.Diver
val diverService = DiverService()


fun main(){
    runMenu()
}

fun mainMenu(): Int {
    print("""
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
        ➤ """.trimIndent())
    return readlnOrNull()?.toIntOrNull() ?: -1
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
            0 -> println("Exiting App")
            else -> println("Invalid Option")
        }
    } while (input != 0)
}

fun addDiver(){
    print("Name: ")
    val fullName = readlnOrNull() ?: ""

    print("Date Of Birth(DD/MM/YYYY): ")
    val dob = LocalDate.parse(readln() ,DateTimeFormatter.ofPattern("dd/MM/yyyy"))

    print("Email: ")
    val email = readlnOrNull()?: ""

    print("Certification: ")
    val certification = readlnOrNull() ?: ""

    print("Previous Dives: ")
    val previousDives = readlnOrNull()?.toIntOrNull() ?: 0

    diverService.addDiver(
        Diver(0, fullName , dob, email, certification, previousDives )
    )

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
    print("Enter Diver ID: ")
    val id = readlnOrNull()?.toIntOrNull() ?: -1
    val diver = diverService.findDiverById(id)

    if (diver != null) {
        printDiver(diver)
    } else {
        println("Diver not found.")
    }
    println()
}

fun deleteDiver() {
    print("Enter ID to delete: ")
    val id = readlnOrNull()?.toIntOrNull() ?: -1

    if (diverService.deleteDiver(id)) {
        println("Deleted")
    } else {
        println(" ID Not found")
    }
}

fun updateDiver() {
    print("Enter ID to update: ")
    val id = readlnOrNull()?.toIntOrNull() ?: -1

    print("Name: ")
    val fullName = readlnOrNull() ?: ""

    print("Date Of Birth (DD/MM/YYYY): ")
    val dob = LocalDate.parse(readln(), DateTimeFormatter.ofPattern("dd/MM/yyyy"))

    print("Email: ")
    val email = readlnOrNull() ?: ""

    print("Certification: ")
    val certification = readlnOrNull() ?: ""

    print("Previous Dives: ")
    val previousDives = readlnOrNull()?.toIntOrNull() ?: 0

    val updated = Diver(id, fullName, dob, email, certification, previousDives)

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
