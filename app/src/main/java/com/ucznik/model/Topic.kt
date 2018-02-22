package com.ucznik.model

/**
 * Created by Mateusz on 22.02.2018.
 */
data class Topic(var id: Int, var name: String, var done: Boolean) {

    companion object {
        fun getSampleData(): ArrayList<Topic> {
            val topics = ArrayList<Topic>()
            topics.add(Topic(1,"Fizyka",false))
            topics.add(Topic(2,"Matematyka",false))
            topics.add(Topic(3,"Biologia",true))
            topics.add(Topic(4,"Chemia",false))
            topics.add(Topic(5,"Język Polski",true))
            topics.add(Topic(6,"Programowanie w języku JAVA",false))
            topics.add(Topic(7,"Technologie Obiektowe",true))
            topics.add(Topic(8,"Sieci Internetowe",false))
            return topics
        }
    }
}