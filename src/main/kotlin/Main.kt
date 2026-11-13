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
import persistence.JSONSerializer
import persistence.XMLSerializer
import java.io.File

private val logger = KotlinLogging.logger {}
val diverService = DiverService(JSONSerializer(File("divers.json")))
val xmlDiverService = DiverService(XMLSerializer(File("divers.xml")))


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
        │  6) Search Divers by Name        │
        │  7) Filter by Certification      │
        │  8) Filter by Previous Dives     │
        │  9) Display Diver Statistics     │
        ├──────────────────────────────────┤
        │  20) Save Divers                 │
        │  21) Load Divers                 │
        │  22) Export Divers to XML        │
        │  23) Import Divers from XML      │    
        ├──────────────────────────────────┤
        │  0) Exit                         │
        └──────────────────────────────────┘
        """.trimIndent())
    while (true) {
        val option = readNextInt("Enter menu choice: ")

        if (option in 0..9 || option in 20..23) {
            return option
        }

        println("Invalid option. Choose 0-9 or 20-23.")
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
            6 -> searchDiversByName()
            7 -> filterDiversByCertification()
            8 -> filterDiversByPreviousDives()
            9 -> displayDiverStatistics()
            20 -> save()
            21 -> load()
            22 -> saveXML()
            23 -> loadXML()
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
    val diver = diverService.findDiverById(id)

    if (diver == null) {
        println("Diver not found.")
        return
    }
    val updated = diver.copy()

    println("""
        What would you like to update?
        1) Full Name
        2) Date of Birth
        3) Email
        4) Certification
        5) Previous Dives
        0) Cancel
    """.trimIndent())

    val choice = readNextInt("Enter choice: ")
    when (choice) {
        1 -> updated.fullName = readNextName("New Name: ")
        2 -> updated.dob = readNextDate("New Date of Birth (DD/MM/YYYY): ")
        3 -> updated.email = readNextEmail("New Email: ")
        4 -> updated.certification = readNextCertification("New Certification: ")
        5 -> updated.previousDives = readNextPositiveInt("New Previous Dives: ")
        0 -> println("Update cancelled.")
        else -> println("Invalid option.")
    }
    if (choice in 1..5) {
        if (diverService.updateDiver(id, updated)) {
            println("Diver updated successfully!")
        }
    }
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

fun displayResults(divers: List<Diver>) {
    if (divers.isEmpty()) {
        println("No matching divers found.")
    } else {
        println("\n============ RESULTS ============")
        for (diver in divers) {
            printDiver(diver)
        }
        println("────────────────────────────────\n")
    }
}

fun readDiverDetails(id: Int): Diver {
    val fullName = readNextName("Name: ")
    val dob = readNextDate("Date Of Birth (DD/MM/YYYY): ")
    val email = readNextEmail("Email: ")
    val certification = readNextCertification("Certification: ")
    val previousDives = readNextPositiveInt("Previous Dives: ")

    return Diver(id, fullName, dob, email, certification, previousDives)
}

fun save() {
    try {
        diverService.store()
        println("Saved Divers")
    } catch (e: Exception) {
        System.err.println("Error writing to file: $e")
    }
}

fun load() {
    try {
        diverService.load()
        println("Loaded Divers")
    } catch (e: Exception) {
        System.err.println("Error reading from file: $e")
    }
}

fun saveXML() {
    try {
        xmlDiverService.replaceDivers(diverService.getDivers())
        xmlDiverService.store()
        println("Divers exported to XML.")
    } catch (e: Exception) {
        System.err.println("Error writing XML file: $e")
    }
}

fun loadXML() {
    try {
        xmlDiverService.load()
        diverService.replaceDivers(xmlDiverService.getDivers())
        println("Divers imported from XML.")
    } catch (e: Exception) {
        System.err.println("Error reading XML file: $e")
    }
}

fun searchDiversByName() {
    print("Enter Diver Name to Search: ")
    val name = readln().trim()

    val divers = diverService.searchDiversByName(name)

    displayResults(divers)
}

fun filterDiversByCertification() {
    print("Enter Certification to Filter: ")
    val certification = readln().trim()

    val divers = diverService.filterDiversByCertification(certification)

    displayResults(divers)
}

fun filterDiversByPreviousDives() {
    val minDives = readNextPositiveInt("Enter Minimum Previous Dives: ")

    val divers = diverService.filterDiversByPreviousDives(minDives)

    displayResults(divers)
}

fun displayDiverStatistics() {
    println("\n============ DIVER STATISTICS ============")

    println("Total Divers: ${diverService.numberOfDivers()}")

    val certification = readNextCertification("Enter Certification: ")
    println("Divers with $certification: ${diverService.numberOfDiversByCertification(certification)}")

    println("Experienced Divers (100+ dives): ${diverService.numberOfExperiencedDivers()}")

    println("==========================================\n")
}

