package com.kma.demo.firebase;

import com.google.cloud.firestore.DocumentChange;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.kma.demo.model.Song;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class FirestoreEventListener {

    private final Firestore firestore;

    public FirestoreEventListener(Firestore firestore) {
        this.firestore = firestore;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void registerListener() throws Exception {
        FirebaseMessaging messaging = FirebaseMessaging.getInstance();

        firestore.collection("songs")
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        System.err.println("Listen failed: " + e);
                        return;
                    }
                    Message message;
                    for (DocumentChange dc : Objects.requireNonNull(snapshots).getDocumentChanges()) {
                        switch (dc.getType()) {
                            case ADDED:
                                System.out.println("New user: " + dc.getDocument().getData());
                                break;
                            case MODIFIED:
                                System.out.println("Modified user: " + dc.getDocument().getData());
                                break;
                            case REMOVED:
                                System.out.println("Removed user: " + dc.getDocument().getData());
                                break;
                            default:
                                break;
                        }
                    }

                    message = Message.builder()
                            .setNotification(new Notification("My Spotify", "Check for latest songs"))
                            .setToken("c29adK9TS1qHNN5jIKn2Ee:APA91bH-XZP7AVg4khgU4jfUitcM4tsJI5gwNqpQIWEE0o37abEIAiu3CX9BUKpfTWpSET6OKWGK7OZWaQ07YdP0owKPN9QJDrzqMnK_2-vcgR3Vhq8lApkZPqJcBcaL4c7ZB60rLTd7")
                            .build();

                    try {
                        String response = messaging.send(message);
                    } catch (FirebaseMessagingException ex) {
                        ex.printStackTrace();
                    }
                });
    }
}
