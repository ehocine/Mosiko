package com.hocel.mosiko.database

import androidx.room.*
import com.hocel.mosiko.model.Music

@Dao
interface MusicDAO {

    @Query("SELECT * FROM music_table")
    suspend fun getAllMusic(): List<Music>

    @Query("SELECT * FROM music_table WHERE audioID LIKE :mAudioID")
    suspend fun getMusic(mAudioID: Long): Music

    @Query("DELETE FROM music_table")
    suspend fun deleteAllMusic()

    @Update
    suspend fun update(music: Music)

    @Delete
    suspend fun deleteMusic(music: Music)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMusic(musicList: List<Music>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMusic(music: Music)

}