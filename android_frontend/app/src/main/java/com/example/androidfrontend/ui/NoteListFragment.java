package com.example.androidfrontend.ui;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.TextView;

import com.example.androidfrontend.R;
import com.example.androidfrontend.model.Note;
import com.example.androidfrontend.supabase.SupabaseClient;

import java.util.ArrayList;
import java.util.List;

/**
 * PUBLIC_INTERFACE
 * Fragment displaying the list of notes.
 */
public class NoteListFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private NoteAdapter noteAdapter;
    private TextView emptyView;

    public interface NoteSelectedListener {
        void onNoteSelected(Note note);
        void onCreateNote();
    }

    private NoteSelectedListener listener;

    public NoteListFragment() {}

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof NoteSelectedListener) {
            listener = (NoteSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement NoteSelectedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note_list, container, false);
        recyclerView = view.findViewById(R.id.notes_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        progressBar = view.findViewById(R.id.progress_bar);
        emptyView = view.findViewById(R.id.empty_view);

        view.findViewById(R.id.fab_add_note).setOnClickListener(v ->
                listener.onCreateNote()
        );

        noteAdapter = new NoteAdapter(new ArrayList<>(), note -> listener.onNoteSelected(note));
        recyclerView.setAdapter(noteAdapter);

        loadNotes();

        return view;
    }

    // PUBLIC_INTERFACE
    public void refreshNotes() {
        loadNotes();
    }

    private void loadNotes() {
        progressBar.setVisibility(View.VISIBLE);
        SupabaseClient.getInstance(getContext()).getAllNotes(new SupabaseClient.NotesCallback() {
            @Override
            public void onSuccess(final List<Note> notes) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    progressBar.setVisibility(View.GONE);
                    noteAdapter.setNotes(notes);
                    emptyView.setVisibility(notes.isEmpty() ? View.VISIBLE : View.GONE);
                });
            }

            @Override
            public void onError(final String error) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    progressBar.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                    Toast.makeText(getContext(), "Error loading notes: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}
