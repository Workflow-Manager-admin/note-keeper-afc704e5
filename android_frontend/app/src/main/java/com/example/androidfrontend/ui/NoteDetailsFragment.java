package com.example.androidfrontend.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.androidfrontend.R;
import com.example.androidfrontend.model.Note;
import com.example.androidfrontend.supabase.SupabaseClient;

/**
 * PUBLIC_INTERFACE
 * Fragment for viewing/editing a single note.
 */
public class NoteDetailsFragment extends Fragment {

    public interface NoteDetailsListener {
        void onBackToList();
        void onNoteChanged();
    }
    private static final String ARG_NOTE = "note";
    private Note note;
    private NoteDetailsListener listener;
    private EditText titleEdit, contentEdit;
    private ProgressBar progressBar;
    private Button saveBtn, deleteBtn;

    public NoteDetailsFragment() {}

    public static NoteDetailsFragment newInstance(@Nullable Note note) {
        NoteDetailsFragment frag = new NoteDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_NOTE, note);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof NoteDetailsListener) {
            listener = (NoteDetailsListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement NoteDetailsListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note_details, container, false);
        titleEdit = view.findViewById(R.id.title_edit);
        contentEdit = view.findViewById(R.id.content_edit);
        progressBar = view.findViewById(R.id.progress_bar);
        saveBtn = view.findViewById(R.id.save_btn);
        deleteBtn = view.findViewById(R.id.delete_btn);

        if (getArguments() != null) {
            note = (Note) getArguments().getSerializable(ARG_NOTE);
        }

        if (note != null) {
            titleEdit.setText(note.getTitle());
            contentEdit.setText(note.getContent());
            deleteBtn.setVisibility(View.VISIBLE);
        } else {
            deleteBtn.setVisibility(View.GONE);
        }

        saveBtn.setOnClickListener(v -> onSaveClicked());
        deleteBtn.setOnClickListener(v -> onDeleteClicked());

        return view;
    }

    private void onSaveClicked() {
        String title = titleEdit.getText().toString().trim();
        String content = contentEdit.getText().toString().trim();
        if (TextUtils.isEmpty(title)) {
            titleEdit.setError("Title required");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        if (note == null) {
            // Create new
            SupabaseClient.getInstance(getContext()).createNote(title, content, new SupabaseClient.NoteCallback() {
                @Override
                public void onSuccess(Note note) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Note created!", Toast.LENGTH_SHORT).show();
                        listener.onNoteChanged();
                    });
                }
                @Override
                public void onError(String error) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
                    });
                }
            });
        } else {
            // Update
            SupabaseClient.getInstance(getContext()).updateNote(note.getId(), title, content, new SupabaseClient.VoidCallback() {
                @Override
                public void onSuccess() {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Note updated.", Toast.LENGTH_SHORT).show();
                        listener.onNoteChanged();
                    });
                }
                @Override
                public void onError(String error) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
                    });
                }
            });
        }
    }

    private void onDeleteClicked() {
        if (note == null) return;
        new AlertDialog.Builder(getContext())
            .setTitle("Delete Note")
            .setMessage("Delete this note?")
            .setPositiveButton("Delete", (dialog, which) -> {
                progressBar.setVisibility(View.VISIBLE);
                SupabaseClient.getInstance(getContext()).deleteNote(note.getId(), new SupabaseClient.VoidCallback() {
                    @Override
                    public void onSuccess() {
                        new Handler(Looper.getMainLooper()).post(() -> {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "Note deleted.", Toast.LENGTH_SHORT).show();
                            listener.onNoteChanged();
                        });
                    }
                    @Override
                    public void onError(String error) {
                        new Handler(Looper.getMainLooper()).post(() -> {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
                        });
                    }
                });
            })
            .setNegativeButton("Cancel", null)
            .show();
    }
}
