package com.kma.demo.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import com.kma.demo.model.Song;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
public class SongService {
    public Song fetchData() throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();

        DocumentReference documentReference = db.collection("songs").document("FRJdzjfYVmCctwQdG85G");

        ApiFuture<DocumentSnapshot> future = documentReference.get();

        DocumentSnapshot document = future.get();

        Song song = null;
        if(document.exists()) {
            song = document.toObject(Song.class);
            song.setId(document.getId());
            return song;
        }
        return null;
    }
}
