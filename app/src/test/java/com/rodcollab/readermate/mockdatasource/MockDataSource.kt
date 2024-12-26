package com.rodcollab.readermate.mockdatasource

import java.util.UUID

/**
 * This class is useful to mock any type of datasource
 */
class MockDataSource<T> {

    private var source = mutableMapOf<String, T>()

    fun createEntity(entity: T) {
        source[getId(entity)] = entity
    }

    fun getAll(): List<T> = source.map { it.value }

    fun readyById(id: String): T? = source[id]

    fun update(entity: T) {
        source[getId(entity)] = entity
    }

    fun delete(id: String) { source.remove(id) }

    private fun getId(entity: T): String {
        val uuidField = entity!!::class.java.getDeclaredField(UUID_FIELD)
        uuidField.isAccessible = true

        val actualField = uuidField.get(entity) as String
        return if(actualField == "") {
            // TODO("the id should be a Long in the future, to avoid UUID ids")
            val newUUID = UUID.randomUUID().toString()
            uuidField.set(entity, newUUID)
            newUUID
        } else {
            actualField
        }
    }

    fun removeAll() {
        source.clear()
    }

    companion object {
        const val UUID_FIELD = "uuid"
    }
}