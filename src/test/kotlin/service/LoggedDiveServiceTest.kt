
package service

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Assertions.*
import model.LoggedDive
import persistence.JSONSerializer
import persistence.XMLSerializer
import java.io.File
import java.time.LocalDate

class LoggedDiveServiceTest {

    private lateinit var dive1: LoggedDive
    private lateinit var emptyDiveService: LoggedDiveService
    private lateinit var populatedDiveService: LoggedDiveService

    @BeforeEach
    fun setUp() {
        dive1 = LoggedDive(
            0, "Blue Hole", LocalDate.of(2026, 6, 15),
            18.5, 45, 22.0, 15, "Recreational", "Good visibility"
        )

        emptyDiveService = LoggedDiveService(
            XMLSerializer(File("test-empty-dives.xml"))
        )

        populatedDiveService = LoggedDiveService(
            XMLSerializer(File("test-dives.xml"))
        )

        populatedDiveService.addDive(dive1)
    }

    @Nested
    inner class XMLPersistence {

        @Test
        fun `saving and loading an empty collection in XML doesn't crash app`() {
            val file = File("test-empty-dives.xml")

            emptyDiveService.store()

            val loadedDives = LoggedDiveService(XMLSerializer(file))
            loadedDives.load()

            assertEquals(0, emptyDiveService.getDives().size)
            assertEquals(0, loadedDives.getDives().size)

            file.delete()
        }

        @Test
        fun `saving and loading dives using XML restores correct data`() {
            val file = File("test-dives.xml")

            populatedDiveService.store()

            val loadedDives = LoggedDiveService(XMLSerializer(file))
            loadedDives.load()

            assertEquals(1, loadedDives.getDives().size)
            assertEquals(dive1.location, loadedDives.getDives()[0].location)
            assertEquals(dive1.maxDepth, loadedDives.getDives()[0].maxDepth)

            file.delete()
        }

        @Test
        fun `adding a dive after loading XML assigns a unique ID`() {
            val file = File("test-dives-ids.xml")

            val savingService = LoggedDiveService(XMLSerializer(file))
            savingService.addDive(dive1.copy())
            savingService.store()

            val loadingService = LoggedDiveService(XMLSerializer(file))
            loadingService.load()

            val newDive = dive1.copy()
            loadingService.addDive(newDive)

            assertEquals(2, loadingService.getDives().size)
            assertEquals(1, loadingService.getDives()[0].diveId)
            assertEquals(2, newDive.diveId)

            file.delete()
        }

    }

    @Nested
    inner class JSONPersistence {

        @Test
        fun `saving and loading dives using JSON restores correct data`() {
            val file = File("test-dives.json")

            val savingService = LoggedDiveService(JSONSerializer(file))
            savingService.addDive(dive1.copy())
            savingService.store()

            val loadingService = LoggedDiveService(JSONSerializer(file))
            loadingService.load()

            assertEquals(1, loadingService.getDives().size)
            assertEquals(dive1.location, loadingService.getDives()[0].location)
            assertEquals(dive1.maxDepth, loadingService.getDives()[0].maxDepth)

            file.delete()
        }

        @Test
        fun `adding a dive after loading JSON assigns a unique ID`() {
            val file = File("test-dives-ids.json")

            val savingService = LoggedDiveService(JSONSerializer(file))
            savingService.addDive(dive1.copy())
            savingService.store()

            val loadingService = LoggedDiveService(JSONSerializer(file))
            loadingService.load()

            val newDive = dive1.copy()
            loadingService.addDive(newDive)

            assertEquals(2, loadingService.getDives().size)
            assertEquals(1, loadingService.getDives()[0].diveId)
            assertEquals(2, newDive.diveId)

            file.delete()
        }

        @Test
        fun `saving and loading an empty collection in JSON doesn't crash app`() {
            val file = File("test-empty-dives.json")

            val savingService = LoggedDiveService(JSONSerializer(file))
            savingService.store()

            val loadingService = LoggedDiveService(JSONSerializer(file))
            loadingService.load()

            assertEquals(0, savingService.getDives().size)
            assertEquals(0, loadingService.getDives().size)

            file.delete()
        }
    }

    @Nested
    inner class AddDives {

        @Test
        fun `add dive to an empty service increases size correctly`() {
            assertEquals(0, emptyDiveService.getDives().size)
            emptyDiveService.addDive(dive1.copy())
            assertEquals(1, emptyDiveService.getDives().size)
        }

        @Test
        fun `add dive to a populated service increases size correctly`() {
            assertEquals(1, populatedDiveService.getDives().size)
            populatedDiveService.addDive(dive1.copy())
            assertEquals(2, populatedDiveService.getDives().size)
        }

        @Test
        fun `new dive is not archived`() {
            val newDive = dive1.copy(isArchived = true)
            emptyDiveService.addDive(newDive)

            assertFalse(newDive.isArchived)
        }
    }

    @Nested
    inner class GetDives {

        @Test
        fun `getDives returns an empty list when service is empty`() {
            assertTrue(emptyDiveService.getDives().isEmpty())
        }

        @Test
        fun `getDives returns all dives when service is populated`() {
            assertEquals(1, populatedDiveService.getDives().size)
        }
    }

    @Nested
    inner class DeleteDives {

        @Test
        fun `deleting an existing dive returns true`() {
            assertTrue(populatedDiveService.deleteDive(dive1.diveId))
        }

        @Test
        fun `deleting a non-existent dive returns false`() {
            assertFalse(populatedDiveService.deleteDive(999))
        }

        @Test
        fun `deleting a dive reduces the number of dives`() {
            populatedDiveService.deleteDive(dive1.diveId)
            assertEquals(0, populatedDiveService.getDives().size)
        }

        @Test
        fun `deleting from an empty service returns false`() {
            assertFalse(emptyDiveService.deleteDive(1))
        }
    }

    @Nested
    inner class FindDives {

        @Test
        fun `finding an existing dive returns the correct dive`() {
            val dive = populatedDiveService.findDiveById(dive1.diveId)
            assertEquals(dive1.location, dive?.location)
        }

        @Test
        fun `finding a non-existent dive returns null`() {
            assertNull(populatedDiveService.findDiveById(999))
        }

        @Test
        fun `finding a dive in an empty service returns null`() {
            assertNull(emptyDiveService.findDiveById(1))
        }
    }

    @Nested
    inner class UpdateDives {

        @Test
        fun `updating an existing dive returns true`() {
            val updatedDive = dive1.copy(location = "New Location")
            assertTrue(populatedDiveService.updateDive(dive1.diveId, updatedDive))
        }

        @Test
        fun `updating a non-existent dive returns false`() {
            val updatedDive = dive1.copy(location = "New Location")
            assertFalse(populatedDiveService.updateDive(999, updatedDive))
        }

        @Test
        fun `updating a dive changes its details`() {
            val updatedDive = dive1.copy(location = "New Location")
            populatedDiveService.updateDive(dive1.diveId, updatedDive)

            val retrievedDive = populatedDiveService.findDiveById(dive1.diveId)
            assertEquals("New Location", retrievedDive?.location)
        }

        @Test
        fun `updating a dive preserves its original ID`() {
            val updatedDive = dive1.copy(diveId = 999, location = "New Location")
            populatedDiveService.updateDive(dive1.diveId, updatedDive)

            assertEquals(dive1.diveId, populatedDiveService.getDives()[0].diveId)
        }
    }

}
