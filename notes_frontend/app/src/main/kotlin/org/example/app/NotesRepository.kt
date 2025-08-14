package org.example.app

import android.content.Context
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/**
 * PUBLIC_INTERFACE
 * NotesRepository
 *
 * Simple local persistence using SharedPreferences storing notes as a JSON array.
 * No external dependencies; uses org.json. Suitable for demo/minimal apps.
 */
class NotesRepository(private val context: Context) {

    companion object {
        private const val PREFS_NAME = "notes_prefs"
        private const val KEY_NOTES_JSON = "notes_json"
    }

    // PUBLIC_INTERFACE
    /**
     * Load all notes sorted by updatedAt descending.
     * @return list of notes
     */
    fun getNotes(): List<Note> {
        val arr = loadJsonArray()
        val list = mutableListOf<Note>()
        for (i in 0 until arr.length()) {
            val obj = arr.optJSONObject(i) ?: continue
            fromJson(obj)?.let { list.add(it) }
        }
        return list.sortedByDescending { it.updatedAt }
    }

    // PUBLIC_INTERFACE
    /**
     * Get a single note by id.
     */
    fun getNoteById(id: Long): Note? {
        val arr = loadJsonArray()
        for (i in 0 until arr.length()) {
            val obj = arr.optJSONObject(i) ?: continue
            val n = fromJson(obj)
            if (n?.id == id) return n
        }
        return null
    }

    // PUBLIC_INTERFACE
    /**
     * Add a new note.
     * @return the created note
     */
    fun addNote(title: String, content: String): Note {
        val now = System.currentTimeMillis()
        val note = Note(
            id = now,
            title = title,
            content = content,
            createdAt = now,
            updatedAt = now
        )
        val arr = loadJsonArray()
        arr.put(toJson(note))
        saveJsonArray(arr)
        return note
    }

    // PUBLIC_INTERFACE
    /**
     * Update an existing note by id. If not found, no-op.
     */
    fun updateNote(note: Note) {
        val arr = loadJsonArray()
        val updated = JSONArray()
        for (i in 0 until arr.length()) {
            val obj = arr.optJSONObject(i) ?: continue
            val n = fromJson(obj)
            if (n?.id == note.id) {
                updated.put(toJson(note))
            } else {
                updated.put(obj)
            }
        }
        saveJsonArray(updated)
    }

    // PUBLIC_INTERFACE
    /**
     * Delete a note by id.
     */
    fun deleteNote(id: Long) {
        val arr = loadJsonArray()
        val updated = JSONArray()
        for (i in 0 until arr.length()) {
            val obj = arr.optJSONObject(i) ?: continue
            val n = fromJson(obj)
            if (n?.id != id) {
                updated.put(obj)
            }
        }
        saveJsonArray(updated)
    }

    private fun loadJsonArray(): JSONArray {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_NOTES_JSON, "[]") ?: "[]"
        return try {
            JSONArray(json)
        } catch (_: JSONException) {
            JSONArray()
        }
    }

    private fun saveJsonArray(arr: JSONArray) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_NOTES_JSON, arr.toString()).apply()
    }

    private fun toJson(note: Note): JSONObject {
        val obj = JSONObject()
        obj.put("id", note.id)
        obj.put("title", note.title)
        obj.put("content", note.content)
        obj.put("createdAt", note.createdAt)
        obj.put("updatedAt", note.updatedAt)
        return obj
    }

    private fun fromJson(obj: JSONObject): Note? {
        return try {
            Note(
                id = obj.getLong("id"),
                title = obj.getString("title"),
                content = obj.getString("content"),
                createdAt = obj.getLong("createdAt"),
                updatedAt = obj.getLong("updatedAt")
            )
        } catch (_: JSONException) {
            null
        }
    }
}
