package com.example.alex.alexcasanovauas.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.alex.alexcasanovauas.NoteActivity;
import com.example.alex.alexcasanovauas.R;
import com.example.alex.alexcasanovauas.model.Note;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

/**
 * Created by Alex on 7/9/2018.
 */

public class NoteRecyclerViewAdapter extends RecyclerView.Adapter<NoteRecyclerViewAdapter.ViewHolder> {

    private List<Note> notesList;
    private Context context;
    private FirebaseFirestore firestoreDB;
    private FirebaseStorage mStorageImage;
    private ViewHolder mHolder;

    public NoteRecyclerViewAdapter(List<Note> notesList, Context context, FirebaseFirestore firestoreDB) {
        this.notesList = notesList;
        this.context = context;
        this.firestoreDB = firestoreDB;
    }

    @Override
    public NoteRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.items_note, parent, false);

        return new NoteRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NoteRecyclerViewAdapter.ViewHolder holder, int position) {
        final int itemPosition = position;
        final Note note = notesList.get(itemPosition);
        mHolder = holder;


        mStorageImage = FirebaseStorage.getInstance();
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageRef = firebaseStorage.getReference();
        StorageReference mountainImagesRef = storageRef.child("images/"+note.getId());
        mountainImagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context)
                        .load(uri)
                        .into(mHolder.mSportsImage);
            }
        });

        holder.title.setText(note.getTitle());
        holder.content.setText(note.getContent());

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateNote(note);
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteNote(note.getId(), itemPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, content;
        Button edit;
        Button delete;
        ImageView mSportsImage;

        ViewHolder(View view) {
            super(view);
            mSportsImage =  view.findViewById(R.id.sportsImage);
            title = view.findViewById(R.id.tvTitle);
            content = view.findViewById(R.id.tvContent);

            edit = view.findViewById(R.id.ivEdit);
            delete = view.findViewById(R.id.ivDelete);
        }
    }

    private void updateNote(Note note) {
        Intent intent = new Intent(context, NoteActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("UpdateNoteId", note.getId());
        intent.putExtra("UpdateNoteTitle", note.getTitle());
        intent.putExtra("UpdateNoteContent", note.getContent());
        context.startActivity(intent);
    }

    private void deleteNote(String id, final int position) {
        firestoreDB.collection("notes")
                .document(id)
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        notesList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, notesList.size());
                        Toast.makeText(context, "Note has been deleted!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}