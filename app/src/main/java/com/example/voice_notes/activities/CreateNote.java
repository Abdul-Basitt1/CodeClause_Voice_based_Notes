package com.example.voice_notes.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.voice_notes.R;
import com.example.voice_notes.database.NotesDatabase;
import com.example.voice_notes.entities.NoteDB;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import org.w3c.dom.Node;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class CreateNote extends AppCompatActivity {

    private EditText noteTitle, noteSubtitle, noteMainDesc;
    private TextView noteDateTime;
    private String selectedNoteColor;
    private View noteSubIndication;
    private NoteDB alreadyAvailableNote;
    private AlertDialog deleteNoteDialog;
    //private ImageButton btnSpeak;
    protected static final int RESULT_SPEECH = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);

        noteTitle = findViewById(R.id.noteTitle);
        noteSubtitle = findViewById(R.id.noteSubtitle);
        noteMainDesc = findViewById(R.id.noteMainDesc);
        noteDateTime = findViewById(R.id.noteDateTime);
        noteSubIndication = findViewById(R.id.noteSubIndication);
        ImageView btnSpeak = findViewById(R.id.micButton);
        ImageView doneArrow = findViewById(R.id.doneArr);
        ImageView backArrow = findViewById(R.id.backArr);

        btnSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
                try {
                    startActivityForResult(intent, RESULT_SPEECH);
                    noteMainDesc.setText("");
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(CreateNote.this, "Speech-To-Text not Supported!", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });

        doneArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNote();
            }
        });

        noteDateTime.setText(new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault( )).format(new Date()));
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        selectedNoteColor = "#333333";

        if(getIntent().getBooleanExtra("isViewOrUpdate", false)){
            alreadyAvailableNote = (NoteDB) getIntent().getSerializableExtra("note");
            setViewOrUpdateNote();
        }

        inItColors();
        setSubIndicateColor();

    }

    private void setViewOrUpdateNote(){
        noteTitle.setText(alreadyAvailableNote.getTitle());
        noteSubtitle.setText(alreadyAvailableNote.getSubtitle());
        noteMainDesc.setText(alreadyAvailableNote.getNoteText());
        noteDateTime.setText(alreadyAvailableNote.getDateTime());
        if(alreadyAvailableNote.getImage() != null && alreadyAvailableNote.getImage().trim().isEmpty()){
            /*imageNote.setImageBitmap(BitmapFactory.decodeFile(alreadyAvailableNote.getImage()));
            imageNote.setVisibility(View.VISIBLE);
            selectedImagePath = alreadyAvailableNote.getImage();*/
        }

        if(alreadyAvailableNote.getWebLink() != null && alreadyAvailableNote.getWebLink().trim().isEmpty()){
            /*textWebURL.setImageBitmap(BitmapFactory.decodeFile(alreadyAvailableNote.getWebLink()));
            layoutWebURL.setVisibility(View.VISIBLE);*/
        }

    }

    private void saveNote() {
        if(noteTitle.getText().toString().trim().isEmpty()){
            Toast.makeText(this, "Title can't be Empty!", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (noteSubtitle.getText().toString().trim().isEmpty() && noteMainDesc.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Note can't be Empty!", Toast.LENGTH_SHORT).show();
            return;
        }
        final NoteDB noteDB = new NoteDB();
        noteDB.setTitle(noteTitle.getText().toString());
        noteDB.setSubtitle(noteSubtitle.getText().toString());
        noteDB.setNoteText(noteMainDesc.getText().toString());
        noteDB.setDateTime(noteDateTime.getText().toString());
        noteDB.setColor(selectedNoteColor);

        if(alreadyAvailableNote != null){
            noteDB.setId(alreadyAvailableNote.getId());
        }

        @SuppressLint("StaticFieldLeak")
        class SaveNoteTask extends AsyncTask<Void, Void, Void>{

            @Override
            protected Void doInBackground(Void... voids) {
                NotesDatabase.getDatabase(getApplicationContext()).noteDao().InsertNote(noteDB);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        }
        new SaveNoteTask().execute();
    }

    private void inItColors(){
        final LinearLayout layoutColorsM = findViewById(R.id.layoutColorsMain);
        final BottomSheetBehavior<LinearLayout> bottomSheetBehavior = BottomSheetBehavior.from(layoutColorsM);
        layoutColorsM.findViewById(R.id.ColorCardText).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED){
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
                else{
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });

        final ImageView ImgColor1 = findViewById(R.id.ImgColor1);
        final ImageView ImgColor2 = findViewById(R.id.ImgColor2);
        final ImageView ImgColor3 = findViewById(R.id.ImgColor3);
        final ImageView ImgColor4 = findViewById(R.id.ImgColor4);
        final ImageView ImgColor5 = findViewById(R.id.ImgColor5);

        layoutColorsM.findViewById(R.id.Color1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedNoteColor = "#333333";
                ImgColor1.setImageResource(R.drawable.done_arrow);
                ImgColor2.setImageResource(0);
                ImgColor3.setImageResource(0);
                ImgColor4.setImageResource(0);
                ImgColor5.setImageResource(0);
                setSubIndicateColor();
            }
        });

        layoutColorsM.findViewById(R.id.Color2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedNoteColor = "#FF4842";
                ImgColor1.setImageResource(0);
                ImgColor2.setImageResource(R.drawable.done_arrow);
                ImgColor3.setImageResource(0);
                ImgColor4.setImageResource(0);
                ImgColor5.setImageResource(0);
                setSubIndicateColor();
            }
        });

        layoutColorsM.findViewById(R.id.Color3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedNoteColor = "#088595";
                ImgColor1.setImageResource(0);
                ImgColor2.setImageResource(0);
                ImgColor3.setImageResource(R.drawable.done_arrow);
                ImgColor4.setImageResource(0);
                ImgColor5.setImageResource(0);
                setSubIndicateColor();
            }
        });

        layoutColorsM.findViewById(R.id.Color4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedNoteColor = "#FDBE3B";
                ImgColor1.setImageResource(0);
                ImgColor2.setImageResource(0);
                ImgColor3.setImageResource(0);
                ImgColor4.setImageResource(R.drawable.done_arrow);
                ImgColor5.setImageResource(0);
                setSubIndicateColor();
            }
        });

        layoutColorsM.findViewById(R.id.Color5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedNoteColor = "#267E29";
                ImgColor1.setImageResource(0);
                ImgColor2.setImageResource(0);
                ImgColor3.setImageResource(0);
                ImgColor4.setImageResource(0);
                ImgColor5.setImageResource(R.drawable.done_arrow);
                setSubIndicateColor();
            }
        });

        if(alreadyAvailableNote != null && alreadyAvailableNote.getColor() != null && alreadyAvailableNote.getColor().trim().isEmpty()){
            switch (alreadyAvailableNote.getColor()){
                case "#FF4842":
                    layoutColorsM.findViewById(R.id.Color2).performClick();
                    break;
                case "#088595":
                    layoutColorsM.findViewById(R.id.Color3).performClick();
                    break;
                case "#FDBE3B":
                    layoutColorsM.findViewById(R.id.Color4).performClick();
                    break;
                case "#267E29":
                    layoutColorsM.findViewById(R.id.Color5).performClick();
                    break;
            }
        }
        if(alreadyAvailableNote != null){
            layoutColorsM.findViewById(R.id.layoutDelete).setVisibility(View.VISIBLE);
            layoutColorsM.findViewById(R.id.layoutDelete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    showDeleteDialog();
                }
            });
        }
    }

    private void showDeleteDialog(){
        if(deleteNoteDialog == null){
            AlertDialog.Builder builder = new AlertDialog.Builder(CreateNote.this);
            View view = LayoutInflater.from(this).inflate(R.layout.layout_delete, (ViewGroup) findViewById(R.id.layoutDeleteNote));
            builder.setView(view);
            deleteNoteDialog = builder.create();
            if(deleteNoteDialog != null){
                deleteNoteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            view.findViewById(R.id.confirmDelete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    @SuppressLint("StaticFieldLeak")
                    class DeleteTask extends AsyncTask<Void, Void, Void>{

                        @Override
                        protected Void doInBackground(Void... voids) {
                            NotesDatabase.getDatabase(getApplicationContext()).noteDao().DeleteNote(alreadyAvailableNote);
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            super.onPostExecute(aVoid);
                            Intent intent = new Intent();
                            intent.putExtra("isNoteDeleted", true);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    }
                    new DeleteTask().execute();
                }
            });

            view.findViewById(R.id.cancelDelete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteNoteDialog.dismiss();
                }
            });
        }
        deleteNoteDialog.show();
    }

    private void setSubIndicateColor(){
        GradientDrawable gradientDrawable = (GradientDrawable) noteSubIndication.getBackground();
        gradientDrawable.setColor(Color.parseColor(selectedNoteColor));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RESULT_SPEECH:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    noteMainDesc.setText(text.get(0));
                }
                break;
        }
    }




}