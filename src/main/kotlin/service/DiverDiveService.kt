package service

import model.DiverDive
import persistence.Serializer

class DiverDiveService(
    private val diverService: DiverService,
    private val loggedDiveService: LoggedDiveService,
    private val serializer: Serializer
) {

    private var diverDiveList = ArrayList<DiverDive>()

    fun addDiverToDive(diverId: Int, diveId: Int): Boolean {
        if (diverService.findDiverById(diverId) == null) return false
        if (loggedDiveService.findDiveById(diveId) == null) return false

        if (diverDiveList.any {
                it.diverId == diverId && it.diveId == diveId
            }) return false

        return diverDiveList.add(DiverDive(diverId, diveId))
    }

    fun removeDiverFromDive(diverId: Int, diveId: Int): Boolean {
        return diverDiveList.removeIf {
            it.diverId == diverId && it.diveId == diveId
        }
    }

    fun getDivesByDiver(diverId: Int): List<DiverDive> {
        return diverDiveList.filter { it.diverId == diverId }
    }

    fun getDiversByDive(diveId: Int): List<DiverDive> {
        return diverDiveList.filter { it.diveId == diveId }
    }

    fun store() {
        serializer.write(diverDiveList)
    }

    fun load() {
        val array = serializer.read(Array<DiverDive>::class.java)

        val validRelationships = array.filter {
            diverService.findDiverById(it.diverId) != null &&
                    loggedDiveService.findDiveById(it.diveId) != null
        }.distinctBy { it.diverId to it.diveId }

        diverDiveList = ArrayList(validRelationships)
    }

    fun replaceRelationships(relationships: List<DiverDive>) {
        diverDiveList = ArrayList()

        for ((diverId, diveId) in relationships) {
            addDiverToDive(diverId, diveId)
        }
    }

    fun getRelationships(): List<DiverDive> {
        return diverDiveList
    }

}