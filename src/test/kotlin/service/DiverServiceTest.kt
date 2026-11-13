
package service

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import model.Diver
import persistence.XMLSerializer
import java.io.File
import java.time.LocalDate

class DiverServiceTest {

    private lateinit var diver1: Diver
    private lateinit var diver2: Diver
    private lateinit var diver3: Diver
    private lateinit var diver4: Diver
    private lateinit var diver5: Diver
    private lateinit var emptyDiverService: DiverService
    private lateinit var populatedDiverService: DiverService

    @BeforeEach
    fun setUp() {
        diver1 = Diver(0, "John Smith", LocalDate.of(2000, 5, 10),
            "john@example.com", "Open Water", 25)

        diver2 = Diver(0, "Sarah Jones", LocalDate.of(1998, 8, 15),
            "sarah@example.com", "Advanced Open Water", 100)

        diver3 = Diver(0, "Mark Brown", LocalDate.of(1995, 3, 20),
            "mark@example.com", "Open Water", 99)

        diver4 = Diver(0, "Emma Wilson", LocalDate.of(2001, 7, 12),
            "emma@example.com", "Rescue Diver", 101)

        // For use in tests - not added to populated list in setUp
        diver5 = Diver(0, "David Green", LocalDate.of(1999, 11, 5),
            "david@example.com", "Open Water", 50)

        emptyDiverService = DiverService(
            XMLSerializer(File("test-empty-divers.xml"))
        )

        populatedDiverService = DiverService(
            XMLSerializer(File("test-populated-divers.xml"))
        )

        populatedDiverService.addDiver(diver1)
        populatedDiverService.addDiver(diver2)
        populatedDiverService.addDiver(diver3)
        populatedDiverService.addDiver(diver4)
    }

    @AfterEach
    fun tearDown() {
        println("Test completed")
    }

    @Nested
    inner class AddDivers {

        @Test
        fun `add diver to an empty service increases size correctly`() {
            assertEquals(0, emptyDiverService.getDivers().size)
            emptyDiverService.addDiver(diver1)
            assertEquals(1, emptyDiverService.getDivers().size)
        }

        @Test
        fun `add diver to a populated service increases size correctly`() {
            assertEquals(4, populatedDiverService.getDivers().size)
            populatedDiverService.addDiver(diver5)
            assertEquals(5, populatedDiverService.getDivers().size)
        }

        @Test
        fun `add diver stores correct values`() {
            emptyDiverService.addDiver(diver1)

            val retrievedDiver = emptyDiverService.getDivers().first()

            assertEquals(diver1.fullName, retrievedDiver.fullName)
            assertEquals(diver1.dob, retrievedDiver.dob)
            assertEquals(diver1.email, retrievedDiver.email)
            assertEquals(diver1.certification, retrievedDiver.certification)
            assertEquals(diver1.previousDives, retrievedDiver.previousDives)
        }

        @Test
        fun `ensure diver ids remain unique after multiple inserts`() {
            assertEquals(0, populatedDiverService.getDivers()[0].diverId)
            assertEquals(1, populatedDiverService.getDivers()[1].diverId)
            assertEquals(2, populatedDiverService.getDivers()[2].diverId)
            assertEquals(3, populatedDiverService.getDivers()[3].diverId)

            populatedDiverService.addDiver(diver5)
            assertEquals(4, populatedDiverService.getDivers()[4].diverId)
        }
    }

    @Nested
    inner class DeleteDivers {

        @Test
        fun `deleting a diver from a populated service decreases size correctly`() {
            assertEquals(4, populatedDiverService.getDivers().size)
            populatedDiverService.deleteDiver(1)
            assertEquals(3, populatedDiverService.getDivers().size)
        }
    }
}
