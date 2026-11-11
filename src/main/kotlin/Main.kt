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

        if (option in 0..5 || option in 20..23) {
            return option
        }

        println("Invalid option. Choose 0-5, 20 or 21.")
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
