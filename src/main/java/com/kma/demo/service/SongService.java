package com.kma.demo.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.cloud.storage.Storage;
import com.google.firebase.cloud.FirestoreClient;
import com.kma.demo.model.Song;
import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class SongService {

    public List<Song> fetchAllData(String name) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        List<QueryDocumentSnapshot> documents = null;
        ApiFuture<QuerySnapshot> future = null;
        if(name.isEmpty()) {
            // asynchronously retrieve all documents
            future = db.collection("songs").get();
            // future.get() blocks on response
            documents = future.get().getDocuments();
        } else {
            future = db.collection("songs").whereGreaterThanOrEqualTo("title", name).whereLessThanOrEqualTo("title", name + "\\uf7ff").get();
            documents = future.get().getDocuments();
        }

        List<Song> songs = new ArrayList<>();
        for (QueryDocumentSnapshot document : documents) {
            Song song = new Song();
            song = document.toObject(Song.class);
            songs.add(song);
        }
        return songs;
    }

    public Song fetchData() throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();

        DocumentReference documentReference = db.collection("songs").document("FRJdzjfYVmCctwQdG85G1");

        ApiFuture<DocumentSnapshot> future = documentReference.get();

        DocumentSnapshot document = future.get();

        Song song = null;


        return null;
    }

    public byte[] download(String urlStr) throws IOException {
        try (BufferedInputStream in = new BufferedInputStream(new URL(urlStr).openStream());
             FileOutputStream fileOutputStream = new FileOutputStream("beertalk")) {
            byte dataBuffer[] = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
            return dataBuffer;
        } catch (IOException e) {
            // handle exception
        }
        return null;
    }

    public void update(String id) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference docRef = db.collection("songs").document("FRJdzjfYVmCctwQdG85G");

        ApiFuture<WriteResult> future = docRef.update("count", 21);

        WriteResult result = future.get();
        System.out.println("Write result: " + result);
    }
}
