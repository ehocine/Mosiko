package com.hocel.mosiko.database

import androidx.room.TypeConverter
import com.hocel.mosiko.model.Music
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

object DatabaseTypeConverter {

    @TypeConverter
    fun musicListToJSON(musicList: List<Music>) = Gson().toJson(musicList)!!

    @TypeConverter
    fun musicListFromJSON(musicListJSON: String): List<Music> {
        val listType: Type = object : TypeToken<ArrayList<Music>>() {}.type

        return Gson().fromJson(
            musicListJSON,
            listType
        ) ?: emptyList()
    }
}