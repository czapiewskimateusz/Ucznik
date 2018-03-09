package com.ucznik.model.dao

import android.arch.persistence.room.*
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import com.ucznik.model.entities.Topic
import io.reactivex.Flowable

/**
 * Created by Mateusz on 24.02.2018.
 */
@Dao interface TopicDAO {

    @Query("select * from topic")
    fun getAllTopics(): List<Topic>

    @Query("select * from topic where topicId = :arg0")
    fun findTopicById(id: Long): Topic

    @Insert(onConflict = REPLACE)
    fun insertTopic(topic: Topic)

    @Update(onConflict = REPLACE)
    fun updateTopic(topic: Topic)

    @Delete
    fun deleteTopic(topic: Topic)
}