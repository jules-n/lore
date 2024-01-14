package year.exp.lore.controllers;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import year.exp.lore.dto.Initiative;
import year.exp.lore.services.DocumentService;
import year.exp.lore.services.GcpStorageService;

import java.io.IOException;
import java.net.URL;

@RestController
@RequestMapping("/documents")
public class DocumentController {

    @Setter(onMethod_ = {@Autowired})
    GcpStorageService gcpStorageService;

    @Setter(onMethod_ = {@Autowired})
    DocumentService documentService;

    @PostMapping("/export")
    public ResponseEntity<URL> export(@RequestBody Initiative dto) {
        String path = "agreements/"+dto.getId()+".p7s";
        if (gcpStorageService.isExist(path)) {
           return ResponseEntity.ok(gcpStorageService.signFile(path));
        }
        try {
            String localPath = documentService.export(dto);
            String bucketPath = gcpStorageService.upload(localPath, "agreements", dto.getId()+".docx");
            return ResponseEntity.ok(gcpStorageService.signFile(bucketPath));
        } catch (IOException ex) {
            return ResponseEntity.internalServerError().build();
        }
    }


    @PostMapping("/import")
    public ResponseEntity upload(MultipartFile file, @RequestParam String id) {
        if (!getExtension(file).equals("p7s")) {
            return ResponseEntity.badRequest().build();
        }
        try {
            gcpStorageService.upload(file, "agreements", id+".p7s");
            return ResponseEntity.noContent().build();
        } catch (IOException ex) {
            return ResponseEntity.internalServerError().build();
        }
    }

    private String getExtension(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        if (fileName != null && fileName.contains(".")) {
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        }
        return null;
    }
}
