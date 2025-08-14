package org.example.app

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView

/**
 * PUBLIC_INTERFACE
 * MainActivity
 *
 * This is the entry point of the Notes application. It shows a header bar, a search field,
 * a scrollable list of notes, and a floating action button to create new notes.
 * It manages create, edit, delete actions via simple modal dialogs.
 */
class MainActivity : Activity() {

    private lateinit var notesRepository: NotesRepository
    private lateinit var listView: ListView
    private lateinit var searchField: EditText
    private lateinit var addFab: Button
    private lateinit var headerTitle: TextView

    private lateinit var adapter: NoteAdapter
    private val allNotes: MutableList<Note> = mutableListOf()
    private val visibleNotes: MutableList<Note> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        notesRepository = NotesRepository(applicationContext)

        headerTitle = findViewById(R.id.headerTitle)
        searchField = findViewById(R.id.searchField)
        listView = findViewById(R.id.notesList)
        addFab = findViewById(R.id.fabAdd)

        headerTitle.text = getString(R.string.app_name)

        // Load notes and set up adapter
        refreshNotes(fromStorage = true)

        adapter = NoteAdapter(this, visibleNotes)
        listView.adapter = adapter

        // Item click opens detail view
        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val note = visibleNotes[position]
            openNoteDetail(note)
        }

        // Long click shows edit/delete options
        listView.onItemLongClickListener = AdapterView.OnItemLongClickListener { _, _, position, _ ->
            val note = visibleNotes[position]
            showItemOptions(note)
            true
        }

        // FAB -> Create new note
        addFab.setOnClickListener {
            showAddEditDialog(existingNote = null)
        }

        // Search filter
        searchField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                applyFilter(s?.toString().orEmpty())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    // PUBLIC_INTERFACE
    /**
     * Refresh notes in memory and on screen.
     * @param fromStorage If true, reloads notes from persistent storage.
     */
    fun refreshNotes(fromStorage: Boolean) {
        if (fromStorage) {
            allNotes.clear()
            allNotes.addAll(notesRepository.getNotes())
        }
        applyFilter(searchField.text?.toString().orEmpty())
    }

    // PUBLIC_INTERFACE
    /**
     * Apply a case-insensitive filter to the notes list by title/content.
     * @param query search string
     */
    fun applyFilter(query: String) {
        visibleNotes.clear()
        if (query.isBlank()) {
            visibleNotes.addAll(allNotes)
        } else {
            val q = query.trim().lowercase()
            visibleNotes.addAll(allNotes.filter {
                it.title.lowercase().contains(q) || it.content.lowercase().contains(q)
            })
        }
        adapter.notifyDataSetChanged()
    }

    private fun openNoteDetail(note: Note) {
        val intent = Intent(this, NoteDetailActivity::class.java)
        intent.putExtra(NoteDetailActivity.EXTRA_NOTE_ID, note.id)
        startActivity(intent)
    }

    private fun showItemOptions(note: Note) {
        val options = arrayOf(
            getString(R.string.action_edit),
            getString(R.string.action_delete)
        )
        AlertDialog.Builder(this)
            .setTitle(note.title)
            .setItems(options) { dialog, which ->
                when (which) {
                    0 -> showAddEditDialog(existingNote = note)
                    1 -> confirmDelete(note)
                }
                dialog.dismiss()
            }
            .show()
    }

    private fun confirmDelete(note: Note) {
        AlertDialog.Builder(this)
            .setTitle(R.string.delete_note_title)
            .setMessage(R.string.delete_note_message)
            .setPositiveButton(R.string.delete) { dialog, _ ->
                notesRepository.deleteNote(note.id)
                refreshNotes(fromStorage = true)
                dialog.dismiss()
            }
            .setNegativeButton(R.string.cancel) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun showAddEditDialog(existingNote: Note?) {
        val inflater = LayoutInflater.from(this)
        val view: View = inflater.inflate(R.layout.dialog_add_edit_note, null)
        val titleField: EditText = view.findViewById(R.id.inputTitle)
        val contentField: EditText = view.findViewById(R.id.inputContent)

        if (existingNote != null) {
            titleField.setText(existingNote.title)
            contentField.setText(existingNote.content)
        }

        val dialogTitleRes = if (existingNote == null) R.string.add_note_title else R.string.edit_note_title

        val dialog = AlertDialog.Builder(this)
            .setTitle(dialogTitleRes)
            .setView(view)
            .setPositiveButton(R.string.save, null)
            .setNegativeButton(R.string.cancel) { d, _ -> d.dismiss() }
            .create()

        dialog.setOnShowListener {
            val saveBtn = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            saveBtn.setOnClickListener {
                val title = titleField.text?.toString()?.trim().orEmpty()
                val content = contentField.text?.toString()?.trim().orEmpty()

                if (title.isBlank()) {
                    titleField.error = getString(R.string.validation_title_required)
                    return@setOnClickListener
                }

                if (existingNote == null) {
                    notesRepository.addNote(title, content)
                } else {
                    notesRepository.updateNote(
                        existingNote.copy(
                            title = title,
                            content = content,
                            updatedAt = System.currentTimeMillis()
                        )
                    )
                }
                refreshNotes(fromStorage = true)
                dialog.dismiss()
            }
        }

        dialog.show()
    }
}
