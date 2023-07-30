package com.example.voice_notes.adapters;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.voice_notes.R;
import com.example.voice_notes.entities.NoteDB;
import com.example.voice_notes.listeners.NotesListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder>{

    private List<NoteDB> notes;
    private NotesListener notesListener;
    private Timer timer;
    private List<NoteDB> notesSource;

    public NoteAdapter(List<NoteDB> notes, NotesListener notesListener) {
        this.notes = notes;
        this.notesListener = notesListener;
        notesSource = notes;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NoteViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_card, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, final int position) {
        holder.setNote(notes.get(position));
        holder.layoutNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notesListener.onNoteClicked(notes.get(position), position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder{

        TextView NoteCardTitle, NoteCardSubtitle, NoteCardDateTime;
        LinearLayout layoutNote;

        NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            NoteCardTitle = itemView.findViewById(R.id.NoteCardTitle);
            NoteCardSubtitle = itemView.findViewById(R.id.NoteCardSubtitle);
            NoteCardDateTime = itemView.findViewById(R.id.NoteCardDateTime);
            layoutNote = itemView.findViewById(R.id.CardLayout);
        }

        void setNote(NoteDB noteDB){
            NoteCardTitle.setText(noteDB.getTitle());
            if(noteDB.getSubtitle().trim().isEmpty()){
                NoteCardSubtitle.setVisibility(View.GONE);
            }
            else{
                NoteCardSubtitle.setText(noteDB.getSubtitle());
            }
            NoteCardDateTime.setText(noteDB.getDateTime());
            GradientDrawable gradientDrawable = (GradientDrawable) layoutNote.getBackground();
            if(noteDB.getColor() != null){
                gradientDrawable.setColor(Color.parseColor(noteDB.getColor()));
            }
            else{
                gradientDrawable.setColor(Color.parseColor("#333333"));
            }
        }
    }

    public void searchNotes(final String searchKeyword){
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(searchKeyword.trim().isEmpty()){
                    notes = notesSource;
                }
                else{
                    ArrayList<NoteDB> temp = new ArrayList<>();
                    for(NoteDB noteDB : notesSource){
                        if(noteDB.getTitle().toLowerCase().contains(searchKeyword.toLowerCase())
                                || noteDB.getSubtitle().toLowerCase().contains(searchKeyword.toLowerCase())
                                || noteDB.getNoteText().toLowerCase().contains(searchKeyword.toLowerCase())){
                            temp.add(noteDB);
                        }
                    }
                    notes = temp;
                }
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });
            }
        }, 500);
    }

    public void cancelTimer(){
        if(timer != null){
            timer.cancel();
        }
    }
}
