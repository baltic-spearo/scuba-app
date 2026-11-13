package service
import model.Diver
import persistence.Serializer

class DiverService(serializerType: Serializer) {

    private var serializer: Serializer = serializerType
    private var diverList = ArrayList<Diver>()
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

    private fun updateLastId() {
        lastId = (diverList.maxOfOrNull { it.diverId } ?: -1) + 1
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

    fun load() {
        val array = serializer.read(Array<Diver>::class.java)
        diverList = array.toCollection(ArrayList())
        updateLastId()
    }

    fun store() {
        serializer.write(diverList)
    }

    fun replaceDivers(divers: List<Diver>) {
        diverList = ArrayList(divers)
        updateLastId()
    }

    fun searchDiversByName(name: String): List<Diver> {
        return diverList.filter {
            it.fullName.contains(name, ignoreCase = true)
        }
    }

    fun filterDiversByCertification(certification: String): List<Diver> {
        return diverList.filter {
            it.certification.equals(certification, ignoreCase = true)
        }
    }

    fun filterDiversByPreviousDives(minDives: Int): List<Diver> {
        return diverList.filter {
            it.previousDives >= minDives
        }
    }

    fun numberOfDivers(): Int {
        return diverList.size
    }

    fun numberOfDiversByCertification(certification: String): Int {
        return diverList.count {
            it.certification.equals(certification, ignoreCase = true)
        }
    }


    fun numberOfExperiencedDivers(): Int {
        return diverList.count {
            it.previousDives >= 100
        }
    }


}