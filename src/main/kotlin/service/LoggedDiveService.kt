
package service

import model.LoggedDive

class LoggedDiveService {

    private var diveList = ArrayList<LoggedDive>()
    private var lastId = 0

    private fun getId(): Int {
        return ++lastId
    }

    fun addDive(dive: LoggedDive): Boolean {
        dive.diveId = getId()
        dive.isArchived = false
        return diveList.add(dive)
    }

    fun getDives(): List<LoggedDive> {
        return diveList
    }

    fun findDiveById(id: Int): LoggedDive? {
        return diveList.find { it.diveId == id }
    }

    fun deleteDive(id: Int): Boolean {
        return diveList.removeIf { it.diveId == id }
    }

    fun updateDive(id: Int, updatedDive: LoggedDive): Boolean {
        val foundDive = findDiveById(id)

        if (foundDive != null) {
            foundDive.location = updatedDive.location
            foundDive.diveDate = updatedDive.diveDate
            foundDive.maxDepth = updatedDive.maxDepth
            foundDive.duration = updatedDive.duration
            foundDive.waterTemperature = updatedDive.waterTemperature
            foundDive.visibility = updatedDive.visibility
            foundDive.diveType = updatedDive.diveType
            foundDive.notes = updatedDive.notes
            return true
        }

        return false
    }
}
