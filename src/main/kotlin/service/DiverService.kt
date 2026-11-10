package service
import model.Diver

class DiverService {
    private val diverList = ArrayList<Diver>()
    private var lastId = 0
    private fun getId(): Int = lastId++

    fun addDiver(diver: Diver) {
        diver.diverId = getId()
        diverList.add(diver)
    }

    fun getDivers(): List<Diver> {
        return diverList
    }

    fun findDiverById(id: Int): Diver? {
        return diverList.find {it.diverId == id}
    }

    fun deleteDiver(id: Int): Boolean {
        return diverList.removeIf { it.diverId == id }
    }

    fun updateDiver(id: Int, updated: Diver): Boolean {
        val index = diverList.indexOfFirst { it.diverId == id }

        if (index != -1) {
            diverList[index] = updated.copy(diverId = id)
            return true
        }
        return false
    }
}