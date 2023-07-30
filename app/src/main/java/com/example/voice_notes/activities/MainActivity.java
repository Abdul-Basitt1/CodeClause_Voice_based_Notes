package com.example.voice_notes.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.voice_notes.R;
import com.example.voice_notes.activities.CreateNote;
import com.example.voice_notes.adapters.NoteAdapter;
import com.example.voice_notes.database.NotesDatabase;
import com.example.voice_notes.entities.NoteDB;
import com.example.voice_notes.listeners.NotesListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NotesListener {

    public static final int REQUEST_CODE_ADD_NOTE = 1;
    public static final int REQUEST_CODE_UPDATE_NOTE = 2;
    public static final int REQUEST_CODE_SHOW_NOTE = 3;
    private RecyclerView notesRecyclerView;
    private List<NoteDB> noteDBList;
    private NoteAdapter noteAdapter;
    private int noteClickedPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        notesRecyclerView = findViewById(R.id.NoteDisplay);
        ImageView AddNote = findViewById(R.id.AddNoteMain);
        AddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(
                        new Intent(getApplicationContext(), CreateNote.class),REQUEST_CODE_ADD_NOTE
                );

            }
        });

        notesRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        noteDBList = new ArrayList<>();
        noteAdapter = new NoteAdapter(noteDBList, this);
        notesRecyclerView.setAdapter(noteAdapter);
        getNotes(REQUEST_CODE_SHOW_NOTE, false);

        EditText search = findViewById(R.id.search);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                noteAdapter.cancelTimer();

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(noteDBList.size() != 0){
                    noteAdapter.searchNotes(s.toString());
                }

            }
        });
    }

    @Override
    public void onNoteClicked(NoteDB noteDB, int position) {
        noteClickedPosition = position;
        Intent intent = new Intent(getApplicationContext(), CreateNote.class);
        intent.putExtra("isViewOrUpdate", true);
        intent.putExtra("note", noteDB);
        startActivityForResult(intent, REQUEST_CODE_UPDATE_NOTE);

    }

    private void getNotes(final int requestCode, final boolean isNoteDeleted){

        @SuppressLint("StaticFieldLeak")
        class getNotesTask extends AsyncTask<Void,Void, List<NoteDB>>{
            @Override
            protected List<NoteDB> doInBackground(Void... voids) {
                return NotesDatabase.getDatabase(getApplicationContext()).noteDao().getAllNotes();
            }

            @Override
            protected void onPostExecute(List<NoteDB> notes) {
                super.onPostExecute(notes);
                if(requestCode == REQUEST_CODE_SHOW_NOTE){
                    noteDBList.addAll(notes);
                    noteAdapter.notifyDataSetChanged();
                }
                else if (requestCode == REQUEST_CODE_ADD_NOTE) {
                    noteDBList.add(0, notes.get(0));
                    noteAdapter.notifyItemInserted(0);
                    notesRecyclerView.smoothScrollToPosition(0);
                }
                else if (requestCode == REQUEST_CODE_UPDATE_NOTE) {
                    noteDBList.remove(noteClickedPosition);
                    if(isNoteDeleted){
                        noteAdapter.notifyItemRemoved(noteClickedPosition);
                    }
                    else{
                        noteDBList.add(noteClickedPosition, notes.get(noteClickedPosition));
                        noteAdapter.notifyItemChanged(noteClickedPosition);
                    }

                }
            }
        }
        new getNotesTask().execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_ADD_NOTE && resultCode == RESULT_OK){
            getNotes(REQUEST_CODE_SHOW_NOTE, false);
        }
        else if(requestCode == REQUEST_CODE_UPDATE_NOTE && resultCode == RESULT_OK){
            if(data != null){
                getNotes(REQUEST_CODE_UPDATE_NOTE, data.getBooleanExtra("isNoteDeleted", false));
            }

        }
    }
}