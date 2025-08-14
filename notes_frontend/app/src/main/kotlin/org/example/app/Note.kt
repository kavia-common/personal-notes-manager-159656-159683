package org.example.app

/**
 * PUBLIC_INTERFACE
 * Data class representing a note entity.
 *
 * @property id unique identifier for the note (timestamp-based)
 * @property title note title
 * @property content note body content
 * @property createdAt creation timestamp (ms)
 * @property updatedAt last updated timestamp (ms)
 */
data class Note(
    val id: Long,
    val title: String,
    val content: String,
    val createdAt: Long,
    val updatedAt: Long
)
