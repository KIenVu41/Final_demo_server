package com.kma.demo.firebase;

import com.google.cloud.firestore.DocumentChange;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class FirestoreEventListener {

    private final Firestore firestore;

    public FirestoreEventListener(Firestore firestore) {
        this.firestore = firestore;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void registerListener() throws Exception {
        firestore.collection("songs")
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        System.err.println("Listen failed: " + e);
                        return;
                    }

                    for (DocumentChange dc : Objects.requireNonNull(snapshots).getDocumentChanges()) {
                        switch (dc.getType()) {
                            case ADDED:
                                System.out.println("New user: " + dc.getDocument().getData());
                                break;
                            case MODIFIED:
                                System.out.println("Modified user: " + dc.getDocument().getData());
                                try {
                                    Message message = Message.builder()
                                            .setNotification(new Notification("Title", "Body"))
                                            .setToken("device_token")
                                            .build();
                                    FirebaseMessaging.getInstance().send(message);

                                }catch (Exception err) {
                                    err.printStackTrace();
                                }
                                break;
                            case REMOVED:
                                System.out.println("Removed user: " + dc.getDocument().getData());
                                break;
                            default:
                                break;
                        }
                    }
                });
    }
}
