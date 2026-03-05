package server.engine

object RoomManager {
    private val activeRooms = mutableMapOf<String, Room>()

    fun createRoom(): Room {
        val newId = (1000..9999).random().toString()

        if (activeRooms.containsKey(newId)) return createRoom()

        val newRoom = Room(newId)
        activeRooms[newId] = newRoom
        return newRoom
    }

    fun getRoom(id : String) : Room? {
        return activeRooms[id]
    }

    fun closeRoom(id : String) {
        activeRooms.remove(id)
    }
}