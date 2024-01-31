package year.exp.lore.services;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

@Service
public class GcpStorageService {

    @Value("${gcp.bucket.name}")
    private String bucketName;

    @Autowired
    Storage storage;

    public String upload(MultipartFile file, String folder, String name) throws IOException {
        String fileName = folder+"/"+name;
        BlobId blobId = BlobId.of(bucketName, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).
                setContentType(file.getContentType()).build();
        storage.create(blobInfo, file.getBytes());
        return fileName;
    }

    public String upload(String filePath, String folder, String name) throws IOException {
        String fileName = folder+"/"+name;
        BlobId blobId = BlobId.of(bucketName, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        Path path = Paths.get(filePath);
        storage.create(blobInfo, Files.readAllBytes(path));
        Files.delete(path);
        return fileName;
    }

    public URL signFile(String fileName) {
        BlobId blobId = BlobId.of(bucketName, fileName);
        BlobInfo blobInfo = storage.get(blobId).asBlobInfo();
        return storage.signUrl(blobInfo, 12l, TimeUnit.HOURS, Storage.SignUrlOption.withV4Signature());
    }

    public boolean isExist(String fileName) {
        BlobId blobId = BlobId.of(bucketName, fileName);
        return storage.get(blobId) != null;
    }

}
