package dev.asedem.mediacloud.service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import dev.asedem.mediacloud.database.entity.Image;
import dev.asedem.mediacloud.database.repository.ImageRepository;

@Service
public class ImageService {
    
    private static final String UPLOAD_DIR = "./stored_images/";
    private static final String ALGORITHM = "AES";
    private static final String KEY = "e8519eb540c73be13b8d91439089f6c152ecf23f477cbd1dd4a5c4a838e37188"; 

    private final ImageRepository imageRepository;

    public ImageService(ImageRepository imageRepository) {
        new File(UPLOAD_DIR).mkdirs();
        this.imageRepository = imageRepository;
    }

    public Image uploadImage(String title, MultipartFile file) throws Exception {
        String path = this.saveEncryptedFile(file);
        Image image = new Image(title, path);
        return this.imageRepository.save(image);
    }

    public List<Image> getAllImages() {
        return this.imageRepository.findAll();
    }

    public byte[] getImageData(Integer id) throws Exception {
        Image image = imageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Image not found"));
        return this.loadDecryptedFile(image.getFilePath());
    }

    private String saveEncryptedFile(MultipartFile file) throws Exception {
        String fileName = UUID.randomUUID().toString() + ".enc";
        String fullPath = UPLOAD_DIR + fileName;

        SecretKeySpec secretKey = new SecretKeySpec(KEY.substring(0, 16).getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        byte[] encryptedBytes = cipher.doFinal(file.getBytes());
        Files.write(Paths.get(fullPath), encryptedBytes);

        return fullPath;
    }

    private byte[] loadDecryptedFile(String path) throws Exception {
        byte[] encryptedBytes = Files.readAllBytes(Paths.get(path));

        SecretKeySpec secretKey = new SecretKeySpec(KEY.substring(0, 16).getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        return cipher.doFinal(encryptedBytes);
    }
}
