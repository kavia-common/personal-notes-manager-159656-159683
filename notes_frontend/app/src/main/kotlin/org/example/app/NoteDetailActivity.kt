package org.example.app

import android.app.Activity
import android.os.Bundle
import android.widget.TextView

/**
 * PUBLIC_INTERFACE
 * NoteDetailActivity
 *
 * Displays the full details of a note selected from the list.
 * Expects an Intent extra "note_id" (Long).
 */
class NoteDetailActivity : Activity() {

    companion object {
        const val EXTRA_NOTE_ID = "note_id"
    }

    private lateinit var notesRepository: NotesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_detail)

        notesRepository = NotesRepository(applicationContext)

        val backButton: TextView = findViewById(R.id.backButton)
        val headerTitle: TextView = findViewById(R.id.headerTitleDetail)
        val titleView: TextView = findViewById(R.id.detailTitle)
        val contentView: TextView = findViewById(R.id.detailContent)

        backButton.setOnClickListener { finish() }

        val noteId = intent.getLongExtra(EXTRA_NOTE_ID, -1L)
        val note = notesRepository.getNoteById(noteId)
        if (note != null) {
            headerTitle.text = getString(R.string.note_detail_header)
            titleView.text = note.title
            contentView.text = note.content
        } else {
            headerTitle.text = getString(R.string.note_not_found)
            titleView.text = ""
            contentView.text = ""
        }
    }
}
