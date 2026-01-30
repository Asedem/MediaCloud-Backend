package dev.asedem.mediacloud.service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageService {
    
    private static final String UPLOAD_DIR = "./stored_images/";
    private static final String ALGORITHM = "AES";
    private static final String KEY = "e8519eb540c73be13b8d91439089f6c152ecf23f477cbd1dd4a5c4a838e37188"; 

    public ImageService() {
        new File(UPLOAD_DIR).mkdirs();
    }

    public String saveEncryptedFile(MultipartFile file) throws Exception {
        String fileName = UUID.randomUUID().toString() + ".enc";
        String fullPath = UPLOAD_DIR + fileName;

        SecretKeySpec secretKey = new SecretKeySpec(KEY.substring(0, 16).getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        byte[] encryptedBytes = cipher.doFinal(file.getBytes());
        Files.write(Paths.get(fullPath), encryptedBytes);

        return fullPath;
    }

    public byte[] loadDecryptedFile(String path) throws Exception {
        byte[] encryptedBytes = Files.readAllBytes(Paths.get(path));

        SecretKeySpec secretKey = new SecretKeySpec(KEY.substring(0, 16).getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        return cipher.doFinal(encryptedBytes);
    }
}
