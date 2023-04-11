package com.kma.demo.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.cloud.storage.Bucket;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.cloud.StorageClient;
import com.kma.demo.model.Song;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.zip.GZIPOutputStream;

@Service
public class SongService {

    @Cacheable("myCache")
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

    @Cacheable("myCache")
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

    @Cacheable("myCache")
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

    @Cacheable("myCache")
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

    @Cacheable("myCache")
    public byte[] compress(String urlStr, String fileName) {
        URL url = null;
        InputStream inputStream = null;
        GZIPOutputStream gzip = null;
        ByteArrayOutputStream bos = null;

        try {
            url = new URL(urlStr);
            inputStream = url.openStream();
            byte[] bytes = inputStream.readAllBytes();
            System.out.println("Length before compress " + bytes.length);
            bos = new ByteArrayOutputStream(bytes.length);
            gzip = new GZIPOutputStream(bos);
            gzip.write(bytes);
            gzip.close();
            byte[] compressed = bos.toByteArray();
            System.out.println("Length compress " + compressed.length);
            bos.close();
            inputStream.close();
            return compressed;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(gzip != null) {
                try {
                    gzip.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public int updateCount(String id) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference docRef = db.collection("songs").document(id);
        docRef.update("count", FieldValue.increment(1));

        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();
        return document.toObject(Song.class).getCount();
    }

    @Cacheable("myCache")
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
