package com.example.voice_notes.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;


import com.example.voice_notes.entities.NoteDB;
import java.util.List;

@Dao
public interface NoteDao  {

    @Query("SELECT * FROM notes ORDER BY id DESC")
    List<NoteDB> getAllNotes();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void InsertNote(NoteDB notedb);

    @Delete
    void DeleteNote(NoteDB notedb);

}
