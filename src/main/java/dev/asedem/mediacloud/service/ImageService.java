package dev.asedem.mediacloud.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.imageio.ImageIO;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import dev.asedem.mediacloud.database.entity.Image;
import dev.asedem.mediacloud.database.repository.ImageRepository;

@Service
public class ImageService {
    
    private static final String UPLOAD_DIR = "./stored_images/";
    private static final String THUMB_DIR = "./stored_thumbnails/";
    private static final String ALGORITHM = "AES";
    private static final String KEY = "e8519eb540c73be13b8d91439089f6c152ecf23f477cbd1dd4a5c4a838e37188"; 

    private final ImageRepository imageRepository;

    public ImageService(ImageRepository imageRepository) {
        new File(UPLOAD_DIR).mkdirs();
        new File(THUMB_DIR).mkdirs();
        this.imageRepository = imageRepository;
    }

    public Image uploadImage(String title, MultipartFile file) throws Exception {
        String uuid = UUID.randomUUID().toString();
        byte[] fileBytes = file.getBytes();
        
        String uploadPath = this.saveEncryptedFile(fileBytes, UPLOAD_DIR, uuid);
        String thumbnailPath = this.saveEncryptedThumbnail(fileBytes, uuid);

        Image image = new Image(title, uploadPath, thumbnailPath);
        return this.imageRepository.save(image);
    }

    public List<Image> getAllImages() {
        return this.imageRepository.findAll();
    }

    public byte[] getImageData(Integer id) throws Exception {
        Image image = imageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Image not found"));
        return this.loadDecryptedFile(image.getUploadPath());
    }

    public byte[] getThumbnailData(Integer id) throws Exception {
        Image image = imageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Image not found"));
        
        String fileName = new File(image.getUploadPath()).getName();
        return this.loadDecryptedFile(THUMB_DIR + fileName);
    }

    private String saveEncryptedFile(byte[] data, String directory, String uuid) throws Exception {
        String fullPath = directory + uuid + ".enc";
        SecretKeySpec secretKey = new SecretKeySpec(KEY.substring(0, 16).getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        byte[] encryptedBytes = cipher.doFinal(data);
        Files.write(Paths.get(fullPath), encryptedBytes);
        return fullPath;
    }

    private String saveEncryptedThumbnail(byte[] originalData, String uuid) throws Exception {
        BufferedImage img = ImageIO.read(new ByteArrayInputStream(originalData));
        
        int width = img.getWidth();
        int height = img.getHeight();
        double scale = Math.min(300.0 / width, 300.0 / height);
        if (scale > 1.0) scale = 1.0;

        int targetWidth = (int) (width * scale);
        int targetHeight = (int) (height * scale);

        BufferedImage thumb = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = thumb.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(img, 0, 0, targetWidth, targetHeight, null);
        g.dispose();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(thumb, "jpg", baos);
        
        return saveEncryptedFile(baos.toByteArray(), THUMB_DIR, uuid);
    }

    private byte[] loadDecryptedFile(String path) throws Exception {
        byte[] encryptedBytes = Files.readAllBytes(Paths.get(path));

        SecretKeySpec secretKey = new SecretKeySpec(KEY.substring(0, 16).getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        return cipher.doFinal(encryptedBytes);
    }
}