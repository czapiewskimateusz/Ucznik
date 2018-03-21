package com.ucznik.model.entities

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey


/**
* Created by Mateusz on 22.02.2018.
*/
@Entity()
data class Topic(var name: String, var done: Int) {
    @PrimaryKey(autoGenerate = true) var topicId: Long = 0
}