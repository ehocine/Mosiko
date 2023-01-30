package com.hocel.mosiko.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
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
abstract class MosikoDatabase: RoomDatabase() {

    abstract fun musicDAO(): MusicDAO

    abstract fun playlistDAO(): PlaylistDAO

    companion object {
        private var INSTANCE: MosikoDatabase? = null

        fun getInstance(context: Context): MosikoDatabase {
            if (INSTANCE == null) {
                synchronized(MosikoDatabase::class) {
                    INSTANCE = Room.databaseBuilder(context, MosikoDatabase::class.java, "music.db")
                        .fallbackToDestructiveMigration()
                        .build()
                }
            }

            return INSTANCE!!
        }
    }
}
