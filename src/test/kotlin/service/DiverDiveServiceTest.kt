
package service

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Assertions.*
import model.Diver
import model.DiverDive
import model.LoggedDive
import persistence.JSONSerializer
import persistence.XMLSerializer
import java.io.File
import java.time.LocalDate

class DiverDiveServiceTest {

    private lateinit var diverService: DiverService
    private lateinit var loggedDiveService: LoggedDiveService
    private lateinit var diverDiveService: DiverDiveService
    private lateinit var diver: Diver
    private lateinit var dive: LoggedDive

    @BeforeEach
    fun setUp() {
        diverService = DiverService(
            JSONSerializer(File("test-relationship-divers.json"))
        )

        loggedDiveService = LoggedDiveService(
            JSONSerializer(File("test-relationship-dives.json"))
        )

        diver = Diver(
            0, "John Smith", LocalDate.of(2000, 5, 10),
            "john@example.com", "Open Water", 25
        )

        dive = LoggedDive(
            0, "Blue Hole", LocalDate.of(2026, 6, 15),
            18.5, 45, 22.0, 15, "Recreational", "Good visibility"
        )

        diverService.addDiver(diver)
        loggedDiveService.addDive(dive)

        diverDiveService = DiverDiveService(
            diverService,
            loggedDiveService,
            JSONSerializer(File("test-relationships.json"))
        )
    }

    @Nested
    inner class JSONPersistence {

        @Test
        fun `saving and loading relationships using JSON restores correct data`() {
            val file = File("test-relationships.json")

            assertTrue(diverDiveService.addDiverToDive(diver.diverId, dive.diveId))
            diverDiveService.store()

            val loadingService = DiverDiveService(
                diverService, loggedDiveService, JSONSerializer(file)
            )
            loadingService.load()

            assertEquals(1, loadingService.getRelationships().size)
            assertEquals(diver.diverId, loadingService.getRelationships()[0].diverId)
            assertEquals(dive.diveId, loadingService.getRelationships()[0].diveId)

            file.delete()
        }

        @Test
        fun `saving and loading an empty relationship collection in JSON doesn't crash app`() {
            val file = File("test-empty-relationships.json")

            val storingRelationships = DiverDiveService(
                diverService, loggedDiveService, JSONSerializer(file)
            )
            storingRelationships.store()

            val loadedRelationships = DiverDiveService(
                diverService, loggedDiveService, JSONSerializer(file)
            )
            loadedRelationships.load()

            assertEquals(0, storingRelationships.getRelationships().size)
            assertEquals(0, loadedRelationships.getRelationships().size)

            file.delete()
        }

        @Test
        fun `loading a missing JSON file throws an exception`() {
            val file = File("test-missing-relationships.json")
            file.delete()

            val loadingService = DiverDiveService(
                diverService, loggedDiveService, JSONSerializer(file)
            )

            assertThrows(Exception::class.java) {
                loadingService.load()
            }
        }

        @Test
        fun `loading invalid JSON throws an exception`() {
            val file = File("test-invalid-relationships.json")
            file.writeText("invalid json")

            val loadingService = DiverDiveService(
                diverService, loggedDiveService, JSONSerializer(file)
            )

            assertThrows(Exception::class.java) {
                loadingService.load()
            }

            file.delete()
        }
    }

    @Nested
    inner class XMLPersistence {

        @Test
        fun `saving and loading relationships using XML restores correct data`() {
            val file = File("test-relationships.xml")

            val savingService = DiverDiveService(
                diverService, loggedDiveService, XMLSerializer(file)
            )

            assertTrue(savingService.addDiverToDive(diver.diverId, dive.diveId))
            savingService.store()

            val loadingService = DiverDiveService(
                diverService, loggedDiveService, XMLSerializer(file)
            )
            loadingService.load()

            assertEquals(1, loadingService.getRelationships().size)
            assertEquals(diver.diverId, loadingService.getRelationships()[0].diverId)
            assertEquals(dive.diveId, loadingService.getRelationships()[0].diveId)

            file.delete()
        }

        @Test
        fun `saving and loading an empty relationship collection in XML doesn't crash app`() {
            val file = File("test-empty-relationships.xml")

            val storingRelationships = DiverDiveService(
                diverService, loggedDiveService, XMLSerializer(file)
            )
            storingRelationships.store()

            val loadedRelationships = DiverDiveService(
                diverService, loggedDiveService, XMLSerializer(file)
            )
            loadedRelationships.load()

            assertEquals(0, storingRelationships.getRelationships().size)
            assertEquals(0, loadedRelationships.getRelationships().size)
            assertEquals(
                storingRelationships.getRelationships().size,
                loadedRelationships.getRelationships().size
            )

            file.delete()
        }
    }

    @Nested
    inner class RelationshipValidation {

        @Test
        fun `adding a duplicate relationship returns false`() {
            assertTrue(diverDiveService.addDiverToDive(diver.diverId, dive.diveId))
            assertFalse(diverDiveService.addDiverToDive(diver.diverId, dive.diveId))
            assertEquals(1, diverDiveService.getRelationships().size)
        }

        @Test
        fun `adding a relationship with invalid IDs returns false`() {
            assertFalse(diverDiveService.addDiverToDive(999, dive.diveId))
            assertFalse(diverDiveService.addDiverToDive(diver.diverId, 999))
            assertEquals(0, diverDiveService.getRelationships().size)
        }

        @Test
        fun `loading relationships removes invalid and duplicate entries`() {
            val file = File("test-loaded-relationships.json")

            val relationships = listOf(
                DiverDive(diver.diverId, dive.diveId),
                DiverDive(diver.diverId, dive.diveId),
                DiverDive(999, 999)
            )

            JSONSerializer(file).write(relationships)

            val loadingService = DiverDiveService(
                diverService, loggedDiveService, JSONSerializer(file)
            )
            loadingService.load()

            assertEquals(1, loadingService.getRelationships().size)
            assertEquals(diver.diverId, loadingService.getRelationships()[0].diverId)
            assertEquals(dive.diveId, loadingService.getRelationships()[0].diveId)

            file.delete()
        }
    }

    @Nested
    inner class RemoveRelationships {

        @Test
        fun `removing an existing relationship returns true`() {
            diverDiveService.addDiverToDive(diver.diverId, dive.diveId)

            assertTrue(diverDiveService.removeDiverFromDive(diver.diverId, dive.diveId))
            assertEquals(0, diverDiveService.getRelationships().size)
        }

        @Test
        fun `removing a non-existent relationship returns false`() {
            assertFalse(diverDiveService.removeDiverFromDive(999, 999))
        }
    }

    @Nested
    inner class GetRelationships {

        @Test
        fun `getDiversByDive returns assigned diver`() {
            diverDiveService.addDiverToDive(diver.diverId, dive.diveId)

            val relationships = diverDiveService.getDiversByDive(dive.diveId)

            assertEquals(1, relationships.size)
            assertEquals(diver.diverId, relationships[0].diverId)
        }

        @Test
        fun `getDivesByDiver returns assigned dive`() {
            diverDiveService.addDiverToDive(diver.diverId, dive.diveId)

            val relationships = diverDiveService.getDivesByDiver(diver.diverId)

            assertEquals(1, relationships.size)
            assertEquals(dive.diveId, relationships[0].diveId)
        }
    }

    @Nested
    inner class ReplaceRelationships {

        @Test
        fun `replacing relationships updates the list correctly`() {
            diverDiveService.addDiverToDive(diver.diverId, dive.diveId)

            diverDiveService.replaceRelationships(emptyList())

            assertEquals(0, diverDiveService.getRelationships().size)
        }

        @Test
        fun `replacing relationships rejects duplicates`() {
            val relationships = listOf(
                DiverDive(diver.diverId, dive.diveId),
                DiverDive(diver.diverId, dive.diveId)
            )

            diverDiveService.replaceRelationships(relationships)

            assertEquals(1, diverDiveService.getRelationships().size)
        }
    }
}
