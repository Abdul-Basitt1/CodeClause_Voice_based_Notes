package com.example.voice_notes.listeners;

import com.example.voice_notes.entities.NoteDB;

public interface NotesListener {
    void onNoteClicked(NoteDB noteDB, int position);


}
