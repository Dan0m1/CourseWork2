package com.example.coursework.room.repositories.sync

interface SyncRepository {
    suspend fun twoWaySync()
}