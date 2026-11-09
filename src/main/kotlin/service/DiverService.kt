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
}