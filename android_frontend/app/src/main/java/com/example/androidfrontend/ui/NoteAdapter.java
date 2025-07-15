package com.example.androidfrontend.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.androidfrontend.R;
import com.example.androidfrontend.model.Note;

import java.util.List;

/**
 * PUBLIC_INTERFACE
 * Adapter for the note RecyclerView.
 */
public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {
    public interface NoteClickListener {
        void onNoteClick(Note note);
    }

    private List<Note> notes;
    private final NoteClickListener listener;

    public NoteAdapter(List<Note> notes, NoteClickListener listener) {
        this.notes = notes;
        this.listener = listener;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
        notifyDataSetChanged();
    }

    @Override
    public NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NoteViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(NoteViewHolder holder, int position) {
        final Note note = notes.get(position);
        holder.title.setText(note.getTitle());
        String snippet = note.getContent().length() > 40 ? 
            note.getContent().substring(0, 40) + "..." 
            : note.getContent();
        holder.content.setText(snippet);
        holder.itemView.setOnClickListener(v -> listener.onNoteClick(note));
    }

    @Override
    public int getItemCount() {
        return notes == null ? 0 : notes.size();
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView title, content;
        NoteViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.note_title);
            content = itemView.findViewById(R.id.note_content);
        }
    }
}
