package service

import model.DiverDive

class DiverDiveService(
    private val diverService: DiverService,
    private val loggedDiveService: LoggedDiveService
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
}