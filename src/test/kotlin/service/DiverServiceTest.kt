
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
import persistence.JSONSerializer

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
    inner class GetDivers {

        @Test
        fun `getDivers returns an empty list when service is empty`() {
            assertTrue(emptyDiverService.getDivers().isEmpty())
        }

        @Test
        fun `getDivers returns all divers when service is populated`() {
            val divers = populatedDiverService.getDivers()
            assertEquals(4, divers.size)
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

        @Test
        fun `deleting an existing diver returns true`() {
            assertTrue(populatedDiverService.deleteDiver(1))
        }

        @Test
        fun `deleting a non-existent diver returns false`() {
            assertFalse(populatedDiverService.deleteDiver(-1))
        }

        @Test
        fun `deleting an existing diver reduces the number of divers by one`() {
            assertTrue(populatedDiverService.deleteDiver(2))
            assertEquals(3, populatedDiverService.getDivers().size)
        }

        @Test
        fun `deleting a diver removes the correct diver`() {
            populatedDiverService.deleteDiver(1)
            assertFalse(populatedDiverService.getDivers().any { it.diverId == 1 })
        }

        @Test
        fun `deleting one diver does not remove other divers`() {
            populatedDiverService.deleteDiver(3)

            assertTrue(populatedDiverService.getDivers().any { it.diverId == 0 })
            assertTrue(populatedDiverService.getDivers().any { it.diverId == 1 })
            assertTrue(populatedDiverService.getDivers().any { it.diverId == 2 })
        }

        @Test
        fun `deleting from an empty service returns false`() {
            assertFalse(emptyDiverService.deleteDiver(1))
        }
    }
    @Nested
    inner class UpdateDivers {
        @Test
        fun `updating an existing diver returns true`() {
            val updatedDiver = diver1.copy(fullName = "Updated Name")
            assertTrue(populatedDiverService.updateDiver(0, updatedDiver))
        }

        @Test
        fun `updating a non-existent diver returns false`() {
            val updatedDiver = diver1.copy(fullName = "Updated Name")
            assertFalse(populatedDiverService.updateDiver(99, updatedDiver))
        }

        @Test
        fun `updating a diver in an empty service returns false`() {
            val updatedDiver = diver1.copy(fullName = "Updated Name")
            assertFalse(emptyDiverService.updateDiver(0, updatedDiver))
        }

        @Test
        fun `updating a diver changes its details`() {
            val updatedDiver = diver1.copy(fullName = "Updated Name")
            populatedDiverService.updateDiver(0, updatedDiver)
            val retrievedDiver = populatedDiverService.findDiverById(0)
            assertEquals("Updated Name", retrievedDiver?.fullName)
        }

        @Test
        fun `updating a diver does not change the number of divers`() {
            val updatedDiver = diver1.copy(fullName = "Updated Name")
            val sizeBefore = populatedDiverService.getDivers().size
            populatedDiverService.updateDiver(0, updatedDiver)
            assertEquals(sizeBefore, populatedDiverService.getDivers().size)
        }

        @Test
        fun `updating a diver preserves the original ID`() {
            val updatedDiver = diver1.copy(diverId = 99, fullName = "Updated Name")
            populatedDiverService.updateDiver(0, updatedDiver)
            val retrievedDiver = populatedDiverService.findDiverById(0)
            assertEquals(0, retrievedDiver?.diverId)
        }
    }

    @Nested
    inner class FindDivers {
        @Test
        fun `finding an existing diver returns the correct diver`() {
            val diver = populatedDiverService.findDiverById(1)
            assertEquals("Sarah Jones", diver?.fullName)
        }

        @Test
        fun `finding a non-existent diver returns null`() {
            val diver = populatedDiverService.findDiverById(99)
            assertNull(diver)
        }

        @Test
        fun `finding a diver in an empty service returns null`() {
            val diver = emptyDiverService.findDiverById(0)
            assertNull(diver)
        }
    }
    @Nested
    inner class SearchDivers {

        @Test
        fun `searching by name returns matching divers`() {
            val divers = populatedDiverService.searchDiversByName("John")
            assertEquals(1, divers.size)
        }
        @Test
        fun `searching by name is case insensitive`() {
            val divers = populatedDiverService.searchDiversByName("john")
            assertEquals(1, divers.size)
        }

        @Test
        fun `searching by name returns an empty list when no divers match`() {
            val divers = populatedDiverService.searchDiversByName("Michael")
            assertTrue(divers.isEmpty())
        }

        @Test
        fun `searching by name returns an empty list when service is empty`() {
            val divers = emptyDiverService.searchDiversByName("John")
            assertTrue(divers.isEmpty())
        }

        @Test
        fun `searching by partial name returns matching divers`() {
            val divers = populatedDiverService.searchDiversByName("jo")
            assertEquals(2, divers.size)
        }
    }
    @Nested
    inner class FilterDiversByCertification {

        @Test
        fun `filtering by certification returns matching divers`() {
            val divers = populatedDiverService.filterDiversByCertification("Open Water")
            assertEquals(2, divers.size)
        }

        @Test
        fun `filtering by certification is case insensitive`() {
            val divers = populatedDiverService.filterDiversByCertification("open water")
            assertEquals(2, divers.size)
        }

        @Test
        fun `filtering by certification returns an empty list when no divers match`() {
            val divers = populatedDiverService.filterDiversByCertification("Night Diver")
            assertTrue(divers.isEmpty())
        }

        @Test
        fun `filtering by certification returns an empty list when service is empty`() {
            val divers = emptyDiverService.filterDiversByCertification("Open Water")
            assertTrue(divers.isEmpty())
        }
    }

    @Nested
    inner class FilterDiversByPreviousDives {
        @Test
        fun `filtering by previous dives returns matching divers`() {
            val divers = populatedDiverService.filterDiversByPreviousDives(50)
            assertEquals(3, divers.size)
        }

        @Test
        fun `filtering by previous dives includes divers with exactly the minimum`() {
            val divers = populatedDiverService.filterDiversByPreviousDives(100)
            assertTrue(divers.any { it.previousDives == 100 })
        }

        @Test
        fun `filtering by previous dives excludes divers below the minimum`() {
            val divers = populatedDiverService.filterDiversByPreviousDives(100)
            assertFalse(divers.any { it.previousDives == 99 })
        }

        @Test
        fun `filtering by previous dives returns an empty list when no divers match`() {
            val divers = populatedDiverService.filterDiversByPreviousDives(200)
            assertTrue(divers.isEmpty())
        }

        @Test
        fun `filtering by previous dives returns an empty list when service is empty`() {
            val divers = emptyDiverService.filterDiversByPreviousDives(100)
            assertTrue(divers.isEmpty())
        }
    }

    @Nested
    inner class DiverStatistics {

        @Test
        fun `numberOfDivers returns zero when service is empty`() {
            assertEquals(0, emptyDiverService.numberOfDivers())
        }

        @Test
        fun `numberOfDivers returns correct count when populated`() {
            assertEquals(4, populatedDiverService.numberOfDivers())
        }

        @Test
        fun `numberOfDiversByCertification returns correct count`() {
            assertEquals(2, populatedDiverService.numberOfDiversByCertification("Open Water"))
        }

        @Test
        fun `numberOfDiversByCertification is case insensitive`() {
            assertEquals(2, populatedDiverService.numberOfDiversByCertification("open water"))
        }

        @Test
        fun `numberOfDiversByCertification returns zero when no divers match`() {
            assertEquals(0, populatedDiverService.numberOfDiversByCertification("Divemaster"))
        }

        @Test
        fun `numberOfDiversByCertification returns zero when service is empty`() {
            assertEquals(0, emptyDiverService.numberOfDiversByCertification("Open Water"))
        }


        @Test
        fun `numberOfExperiencedDivers returns correct count`() {
            assertEquals(2, populatedDiverService.numberOfExperiencedDivers())
        }

        @Test
        fun `numberOfExperiencedDivers returns zero when service is empty`() {
            assertEquals(0, emptyDiverService.numberOfExperiencedDivers())
        }

        @Test
        fun `numberOfExperiencedDivers updates count after deleting a diver`() {
            populatedDiverService.deleteDiver(1)
            assertEquals(1, populatedDiverService.numberOfExperiencedDivers())
        }
    }
    @Nested
    inner class JSONPersistence {

        @Test
        fun `saving and loading divers using JSON restores correct data`() {
            val file = File("test-divers.json")

            val savingService = DiverService(JSONSerializer(file))
            savingService.addDiver(diver1)
            savingService.store()

            val loadingService = DiverService(JSONSerializer(file))
            loadingService.load()

            assertEquals(1, loadingService.getDivers().size)
            assertEquals("John Smith", loadingService.getDivers()[0].fullName)
            assertEquals(25, loadingService.getDivers()[0].previousDives)

            file.delete()
        }
    }

    @Nested
    inner class XMLPersistence {

        @Test
        fun `saving and loading divers using XML restores correct data`() {
            val file = File("test-divers.xml")

            val savingService = DiverService(XMLSerializer(file))
            savingService.addDiver(diver1)
            savingService.store()

            val loadingService = DiverService(XMLSerializer(file))
            loadingService.load()

            assertEquals(1, loadingService.getDivers().size)
            assertEquals("John Smith", loadingService.getDivers()[0].fullName)
            assertEquals(25, loadingService.getDivers()[0].previousDives)
            file.delete()
        }
    }
    @Nested
    inner class ReplaceDivers {

        @Test
        fun `replacing divers updates the list correctly`() {
            val newDivers = listOf(diver5)
            populatedDiverService.replaceDivers(newDivers)
            assertEquals(1, populatedDiverService.getDivers().size)
            assertEquals("David Green", populatedDiverService.getDivers()[0].fullName)
        }

        @Test
        fun `adding a diver after replacement assigns the next available ID`() {
            val newDivers = listOf(diver1, diver2)
            populatedDiverService.replaceDivers(newDivers)
            populatedDiverService.addDiver(diver5)
            assertEquals(2, diver5.diverId)
        }
    }
}
