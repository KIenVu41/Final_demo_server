package com.kma.demo.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.cloud.storage.Bucket;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.cloud.StorageClient;
import com.kma.demo.model.Song;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

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
            song.setDocId(document.getId());
            songs.add(song);
        }
        return songs;
    }

    public List<Song> pagination(int page) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        List<QueryDocumentSnapshot> documents = null;
        CollectionReference collectionRef = db.collection("songs");
        Query query = collectionRef.orderBy("id").startAfter((page - 1)  * 20).limit(20);

        documents = query.get().get().getDocuments();
        List<Song> songs = new ArrayList<>();
        for (QueryDocumentSnapshot document : documents) {
            Song song = new Song();
            song = document.toObject(Song.class);
            song.setDocId(document.getId());
            songs.add(song);
        }
        return songs;
    }

    public List<Song> featuredPagination(int page) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        List<QueryDocumentSnapshot> documents = null;
        CollectionReference collectionRef = db.collection("songs");
        Query query = collectionRef.whereEqualTo("featured", true).orderBy("id").startAfter((page - 1)  * 20).limit(20);

        documents = query.get().get().getDocuments();
        List<Song> songs = new ArrayList<>();
        for (QueryDocumentSnapshot document : documents) {
            Song song = new Song();
            song = document.toObject(Song.class);
            song.setDocId(document.getId());
            songs.add(song);
        }
        return songs;
    }

    public List<Song> popularPagination(int page) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        List<QueryDocumentSnapshot> documents = null;
        CollectionReference collectionRef = db.collection("songs");
        Query query = collectionRef.whereGreaterThanOrEqualTo("count", 50).orderBy("count").startAfter((page - 1)  * 20).limit(20);

        documents = query.get().get().getDocuments();
        List<Song> songs = new ArrayList<>();
        for (QueryDocumentSnapshot document : documents) {
            Song song = new Song();
            song = document.toObject(Song.class);
            song.setDocId(document.getId());
            songs.add(song);
        }
        return songs;
    }

    public List<Song> latestPagination(int page) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        List<QueryDocumentSnapshot> documents = null;
        CollectionReference collectionRef = db.collection("songs");
        Query query = collectionRef.whereEqualTo("latest", true).orderBy("id").startAfter((page - 1)  * 20).limit(20);

        documents = query.get().get().getDocuments();
        List<Song> songs = new ArrayList<>();
        for (QueryDocumentSnapshot document : documents) {
            Song song = new Song();
            song = document.toObject(Song.class);
            song.setDocId(document.getId());
            songs.add(song);
        }
        return songs;
    }

    @Cacheable(value="fileCache", key="#urlStr")
    public byte[] compress(String urlStr, String fileName) throws IOException {
        GZIPOutputStream gzip = null;
        ByteArrayOutputStream bos = null;
        try (BufferedInputStream in = new BufferedInputStream(new URL(urlStr).openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(fileName)) {
            byte dataBuffer[] = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }

            bos = new ByteArrayOutputStream(dataBuffer.length);
            gzip = new GZIPOutputStream(bos);
            gzip.write(dataBuffer);
            gzip.close();
            byte[] compressed = bos.toByteArray();
            bos.close();
            return compressed;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (gzip != null) {
                gzip.close();
            }
            if (bos != null) {
                bos.close();
            }
        }
        return null;
    }

    public byte[] compressFile(String filePath) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(filePath);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);

        byte[] buffer = new byte[1024];
        int len;
        while ((len = fileInputStream.read(buffer)) != -1) {
            gzipOutputStream.write(buffer, 0, len);
        }

        gzipOutputStream.close();
        byteArrayOutputStream.close();
        fileInputStream.close();

        return byteArrayOutputStream.toByteArray();
    }


    public int updateCount(String id) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference docRef = db.collection("songs").document(id);
        docRef.update("count", FieldValue.increment(1));

        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();
        return document.toObject(Song.class).getCount();
    }

    public List<Song> findHomeData() throws ExecutionException, InterruptedException {
        List<Song> songs = new ArrayList<>();
        songs.addAll(findBannerSong());
        songs.addAll(findLatestSong());
        songs.addAll(findPopularSong());

        return songs;
    }

    public List<Song> findBannerSong() throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<QuerySnapshot> future = db.collection("songs").whereEqualTo("featured", true).limit(5).get();

        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        List<Song> songs = new ArrayList<>();
        for (QueryDocumentSnapshot document : documents) {
            Song song = new Song();
            song = document.toObject(Song.class);
            song.setDocId(document.getId());
            songs.add(song);
        }
        return songs;
    }

    public List<Song> findLatestSong() throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<QuerySnapshot> future = db.collection("songs").whereEqualTo("latest", true).limit(4).get();

        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        List<Song> songs = new ArrayList<>();
        for (QueryDocumentSnapshot document : documents) {
            Song song = new Song();
            song = document.toObject(Song.class);
            song.setDocId(document.getId());
            songs.add(song);
        }
        return songs;
    }

    public List<Song> findPopularSong() throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<QuerySnapshot> future = db.collection("songs").whereGreaterThanOrEqualTo("count", 50).limit(4).get();

        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        List<Song> songs = new ArrayList<>();
        for (QueryDocumentSnapshot document : documents) {
            Song song = new Song();
            song = document.toObject(Song.class);
            song.setDocId(document.getId());
            songs.add(song);
        }
        return songs;
    }
}
