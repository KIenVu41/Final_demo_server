package com.kma.demo.api;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.kma.demo.model.Song;
import com.kma.demo.service.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/v1")
public class SongController {

    @Autowired
    private SongService songService;

    @GetMapping("/songs")
    public ResponseEntity<List<Song>> getAllSong(@RequestParam String name) {
        try {
            List<Song> songs = songService.fetchAllData(name);
            if (songs == null) {
                return ResponseEntity.notFound().build();
            } else {
                return ResponseEntity.ok(songs);
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(500).build();
    }

    @GetMapping("/home")
    public ResponseEntity<List<Song>> getHomeSong() {
        try {
            List<Song> songs = songService.findHomeData();
            if (songs == null) {
                return ResponseEntity.notFound().build();
            } else {
                CacheControl cacheControl = CacheControl.maxAge(60, TimeUnit.SECONDS);

                return ResponseEntity.ok()
                        .cacheControl(cacheControl)
                        .body(songs);
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(500).build();
    }

    @GetMapping("/pagination/songs")
    public ResponseEntity<List<Song>> pagination(@RequestParam int page) {
        try {
            List<Song> songs = songService.pagination(page);
            if (songs == null) {
                return ResponseEntity.notFound().build();
            } else {
                return ResponseEntity.ok(songs);
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(500).build();
    }

    @GetMapping("/pagination/featured")
    public ResponseEntity<List<Song>> featuredPagination(@RequestParam int page) {
        try {
            List<Song> songs = songService.featuredPagination(page);
            if (songs == null) {
                return ResponseEntity.notFound().build();
            } else {
                return ResponseEntity.ok(songs);
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(500).build();
    }

    @GetMapping("/pagination/popular")
    public ResponseEntity<List<Song>> popularPagination(@RequestParam int page) {
        try {
            List<Song> songs = songService.popularPagination(page);
            if (songs == null) {
                return ResponseEntity.notFound().build();
            } else {
                return ResponseEntity.ok(songs);
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(500).build();
    }

    @GetMapping("/pagination/latest")
    public ResponseEntity<List<Song>> latestPagination(@RequestParam int page) {
        try {
            List<Song> songs = songService.latestPagination(page);
            if (songs == null) {
                return ResponseEntity.notFound().build();
            } else {
                return ResponseEntity.ok(songs);
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(500).build();
    }

    @GetMapping("/update")
    public ResponseEntity<Integer> updateCount(@RequestParam String id) {
        try {
            int count = songService.updateCount(id);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(500).build();
    }

    @RequestMapping(path = "/download", method = RequestMethod.GET)
    public ResponseEntity<byte[]> download(@RequestParam String url, @RequestParam String name) throws IOException {
        try {
            byte[] bytedata = songService.compress(url, name);
            if (bytedata == null) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(bytedata);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(500).build();
    }

    @GetMapping("/download/gzip")
    public ResponseEntity<byte[]> downloadFile(@RequestParam String file) throws IOException {
        String url = "https://firebasestorage.googleapis.com/v0/b/finaldemo-385a1.appspot.com/o/music%2FBeertalks%20(Acoustic%20Live).mp3?alt=media&token=42e904e6-7b22-47dd-86a4-d3d7c7d22e4a";
        byte[] bytedata = songService.compressFile(url);
        if (bytedata == null) {
            return ResponseEntity.notFound().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "file.zip");
        headers.setContentLength(bytedata.length);
        return new ResponseEntity<byte[]>(bytedata, headers, HttpStatus.OK);
    }
}
