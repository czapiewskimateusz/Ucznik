package com.ucznik.model.entities

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey


/**
 * Created by Mateusz on 22.02.2018.
 */
@Entity()
data class Topic(var name: String, var done: Int) {
    @PrimaryKey(autoGenerate = true) var topicId: Long = 0

//    companion object {
//        fun getSampleData(): ArrayList<Topic> {
//            val topics = ArrayList<Topic>()
//            topics.add(Topic(1, "Fizyka", 0))
//            topics.add(Topic(2, "Matematyka", 0))
//            topics.add(Topic(3, "Biologia", 1))
//            topics.add(Topic(4, "Chemia", 0))
//            topics.add(Topic(5, "Język Polski", 1))
//            topics.add(Topic(6, "Programowanie w języku JAVA", 0))
//            topics.add(Topic(7, "Technologie Obiektowe", 1))
//            topics.add(Topic(8, "Sieci Internetowe", 0))
//            return topics
//        }
//    }

}