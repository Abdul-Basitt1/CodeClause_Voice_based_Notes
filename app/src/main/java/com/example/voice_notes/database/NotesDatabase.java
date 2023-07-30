package com.example.voice_notes.database;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.voice_notes.dao.NoteDao;
import com.example.voice_notes.entities.NoteDB;

@Database(entities = NoteDB.class, version = 1, exportSchema = false)
public abstract class NotesDatabase extends RoomDatabase {

    private static NotesDatabase notesDB;

    public static synchronized NotesDatabase getDatabase(Context context){
        if(notesDB == null){
            notesDB = Room.databaseBuilder(context, NotesDatabase.class, "notes_db").build();
        }
        return notesDB;
    }

    public abstract NoteDao noteDao();
}
