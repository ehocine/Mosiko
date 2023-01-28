package com.hocel.mosiko.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.hocel.mosiko.database.PlaylistDAO
import com.hocel.mosiko.model.Music
import com.hocel.mosiko.model.Playlist

@Database(
    entities = [
        Music::class,
        Playlist::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(DatabaseTypeConverter::class)
abstract class MusyDatabase: RoomDatabase() {

    abstract fun musicDAO(): MusicDAO

    abstract fun playlistDAO(): PlaylistDAO

    companion object {
        private var INSTANCE: MusyDatabase? = null

        fun getInstance(context: Context): MusyDatabase {
            if (INSTANCE == null) {
                synchronized(MusyDatabase::class) {
                    INSTANCE = Room.databaseBuilder(context, MusyDatabase::class.java, "music.db")
                        .fallbackToDestructiveMigration()
                        .build()
                }
            }

            return INSTANCE!!
        }
    }
}
