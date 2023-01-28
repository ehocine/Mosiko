package com.hocel.mosiko.utils

import android.content.Context
import android.widget.Toast

object AppUtils {

    object PreferencesKey {
        const val SORT_MUSIC_OPTION = "com.hocel.mosiko:prefKey:sort_music_option"
        const val LAST_MUSIC_PLAYED = "com.hocel.mosiko:prefKey:last_music_played"
    }

    object PreferencesValue {
        const val SORT_MUSIC_BY_NAME = "com.hocel.mosiko:prefVal:sort_music_by_name"
        const val SORT_MUSIC_BY_ARTIST_NAME = "com.hocel.mosiko:prefVal:sort_music_by_artist_name"
        const val SORT_MUSIC_BY_DATE_ADDED = "com.hocel.mosiko:prefVal:sort_music_by_date_added"
    }

    /**
     * Convert element.
     * @author kafri8889
     */
    fun <T, R> Collection<T>.convert(to: (T) -> R): List<R> {
        val result = ArrayList<R>()
        this.forEach { result.add(to(it)) }
        return result
    }

    /**
     * Move element.
     * @author kafri8889
     */
    fun <T> Collection<T>.move(fromIndex: Int, toIndex: Int): List<T> {
        if (fromIndex == toIndex) return this.toList()
        return ArrayList(this).apply {
            val temp = get(fromIndex)
            removeAt(fromIndex)
            add(toIndex, temp)
        }
    }

    /**
     * Get index element of given predicate.
     * @author kafri8889
     */
    fun <T> Collection<T>.indexOf(predicate: (T) -> Boolean): Int {
        this.forEachIndexed { i, t ->
            if (predicate(t)) return i
        }

        return -1
    }

    /**
     * Remove element by given [predicate].
     * @author kafri8889
     */
    fun <T> Collection<T>.removeBy(predicate: (T) -> Boolean): List<T> {
        val result = ArrayList(this)

        this.forEachIndexed { i, t ->
            if (predicate(t)) result.removeAt(i)
        }

        return result
    }

    /**
     * Checks if the specified element is contained in this collection by given [predicate].
     * @author kafri8889
     */
    fun <T> Collection<T>.containBy(predicate: (T) -> Boolean): Boolean {
        this.forEach {
            if (predicate(it)) return true
        }

        return false
    }

    /**
     * Return a item containing only elements matching the given [predicate].
     * @author kafri8889
     */
    fun <T> Collection<T>.get(predicate: (T) -> Boolean): T? {
        this.forEach {
            if (predicate(it)) return it
        }

        return null
    }

    fun Any.toast(context: Context, length: Int = Toast.LENGTH_SHORT) =
        Toast.makeText(context, this.toString(), length).show()
}