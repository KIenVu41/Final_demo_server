package com.kma.demo.api;

import com.kma.demo.model.Song;
import com.kma.demo.service.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

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

    @GetMapping("/songs/latest")
    public ResponseEntity<Song> getSong() {
        try {
            Song song = songService.fetchData();
            if (song == null) {
                return ResponseEntity.notFound().build();
            } else {
                return ResponseEntity.ok(song);
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(500).build();
    }

    @GetMapping("/update")
    public ResponseEntity<Integer> update() {
        try {
            songService.update("");
            return ResponseEntity.ok(1);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(500).build();
    }

    @RequestMapping(path = "/download", method = RequestMethod.GET)
    public ResponseEntity<byte[]> download(String param) throws IOException {
        try {
            byte[] bytedata = songService.download("https://firebasestorage.googleapis.com/v0/b/finaldemo-385a1.appspot.com/o/music%2FTh%E1%BA%BF%20Th%C3%B4i%20-%20H%E1%BA%A3i%20S%C3%A2m.mp3?alt=media&token=3f804457-73da-4402-a9fb-14317bebddd2");
            if (bytedata == null) {
                return ResponseEntity.notFound().build();
            } else {
                return ResponseEntity.ok(bytedata);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(500).build();
    }
}
