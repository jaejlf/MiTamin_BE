package great.job.mytamin.domain.alarm.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class FcmService {

    private FirebaseMessaging instance;

    public void sendTargetMessage(String targetToken, String title, String body) throws FirebaseMessagingException {
        Notification notification = Notification.builder().setTitle(title).setBody(body).build();
        Message message = Message.builder().setToken(targetToken).setNotification(notification).build();
        sendMessage(message);
    }

    public void sendTopicMessage(String topic, String title, String body) throws FirebaseMessagingException {
        Notification notification = Notification.builder().setTitle(title).setBody(body).build();
        Message message = Message.builder().setTopic(topic).setNotification(notification).build();
        sendMessage(message);
    }

    public void sendMessage(Message message) throws FirebaseMessagingException {
        this.instance.send(message);
    }

    @PostConstruct
    public void firebaseSetting() throws IOException {
        String firebaseConfigPath = "firebase/firebase-key.json";
        GoogleCredentials googleCredentials = GoogleCredentials.fromStream(new ClassPathResource(firebaseConfigPath).getInputStream())
                .createScoped(("https://www.googleapis.com/auth/firebase.messaging"));

        FirebaseOptions secondaryAppConfig = FirebaseOptions.builder()
                .setCredentials(googleCredentials)
                .build();
        FirebaseApp app = FirebaseApp.initializeApp(secondaryAppConfig);
        this.instance = FirebaseMessaging.getInstance(app);
    }

}