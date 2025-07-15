package com.example.androidfrontend;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.fragment.app.Fragment;

import com.example.androidfrontend.model.Note;
import com.example.androidfrontend.ui.NoteDetailsFragment;
import com.example.androidfrontend.ui.NoteListFragment;

public class MainActivity extends AppCompatActivity
    implements NoteListFragment.NoteSelectedListener, NoteDetailsFragment.NoteDetailsListener {

    private NoteListFragment noteListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        noteListFragment = new NoteListFragment();
        if (savedInstanceState == null) {
            showFragment(noteListFragment, false);
        }

        BottomNavigationView nav = findViewById(R.id.bottom_navigation);
        nav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_notes) {
                showFragment(noteListFragment, false);
                return true;
            } else if (item.getItemId() == R.id.nav_create) {
                showFragment(NoteDetailsFragment.newInstance(null), true);
                return true;
            }
            return false;
        });
    }

    private void showFragment(Fragment frag, boolean addToBackStack) {
        if (addToBackStack) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, frag)
                    .addToBackStack(null)
                    .commit();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, frag)
                    .commit();
        }
    }

    // NoteListFragment.NoteSelectedListener
    @Override
    public void onNoteSelected(Note note) {
        showFragment(NoteDetailsFragment.newInstance(note), true);
    }

    @Override
    public void onCreateNote() {
        showFragment(NoteDetailsFragment.newInstance(null), true);
    }

    // NoteDetailsFragment.NoteDetailsListener
    @Override
    public void onBackToList() {
        getSupportFragmentManager().popBackStack();
    }

    @Override
    public void onNoteChanged() {
        getSupportFragmentManager().popBackStack();
        noteListFragment.refreshNotes();
    }
}
