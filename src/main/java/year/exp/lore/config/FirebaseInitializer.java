package year.exp.lore.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;

@Configuration
public class FirebaseInitializer {

    private final static String CONFIG_PATH = "src/main/resources/firebase-service-credentials.json";

    @Bean
    public FirebaseApp initialize() {
        try {
            FileInputStream serviceAccount = new FileInputStream(CONFIG_PATH);

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            return FirebaseApp.initializeApp(options);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
