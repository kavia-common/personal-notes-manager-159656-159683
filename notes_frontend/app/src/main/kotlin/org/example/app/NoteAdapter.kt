package org.example.app

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * PUBLIC_INTERFACE
 * NoteAdapter
 *
 * A minimalistic adapter to show Note items in a ListView.
 */
class NoteAdapter(
    private val context: Context,
    private val items: MutableList<Note>
) : BaseAdapter() {

    override fun getCount(): Int = items.size

    override fun getItem(position: Int): Any = items[position]

    override fun getItemId(position: Int): Long = items[position].id

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val holder: ViewHolder
        val view: View

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_note, parent, false)
            holder = ViewHolder(
                title = view.findViewById(R.id.noteTitle),
                content = view.findViewById(R.id.noteContentPreview),
                date = view.findViewById(R.id.noteDate)
            )
            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ViewHolder
        }

        val note = items[position]
        holder.title.text = note.title
        holder.content.text = preview(note.content)
        holder.date.text = formatDate(note.updatedAt)

        return view
    }

    private fun preview(text: String, max: Int = 100): String {
        val t = text.trim()
        return if (t.length <= max) t else t.substring(0, max) + "…"
    }

    private fun formatDate(time: Long): String {
        val sdf = SimpleDateFormat("MMM d, yyyy • HH:mm", Locale.getDefault())
        return sdf.format(Date(time))
    }

    private data class ViewHolder(
        val title: TextView,
        val content: TextView,
        val date: TextView
    )
}
