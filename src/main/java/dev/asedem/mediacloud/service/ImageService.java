package dev.asedem.mediacloud.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.sksamuel.scrimage.ImmutableImage;

import dev.asedem.mediacloud.database.entity.Image;
import dev.asedem.mediacloud.database.entity.Tag;
import dev.asedem.mediacloud.database.repository.ImageRepository;
import jakarta.transaction.Transactional;

@Service
public class ImageService {

    private static final String UPLOAD_DIR = "./stored_images/";
    private static final String THUMB_DIR = "./stored_thumbnails/";
    private static final String ALGORITHM = "AES";
    private static final String KEY = "e8519eb540c73be13b8d91439089f6c152ecf23f477cbd1dd4a5c4a838e37188";

    private final ImageRepository imageRepository;
    private final TagService tagService;

    public ImageService(ImageRepository imageRepository, TagService tagService) {
        new File(UPLOAD_DIR).mkdirs();
        new File(THUMB_DIR).mkdirs();
        this.imageRepository = imageRepository;
        this.tagService = tagService;
    }

    public Image uploadImage(String title, MultipartFile file) throws Exception {
        byte[] fileBytes = file.getBytes();
        return processAndSaveImage(title, fileBytes);
    }

    public Image uploadImageFromUrl(String title, String imageUrl) throws Exception {
        byte[] imageBytes = new RestTemplate()
                .getForObject(imageUrl, byte[].class);

        if (imageBytes == null) {
            throw new RuntimeException("Could not download image from the provided source");
        }

        return this.processAndSaveImage(title, imageBytes);
    }

    private Image processAndSaveImage(String title, byte[] fileBytes) throws Exception {
        String uuid = UUID.randomUUID().toString();

        this.saveEncryptedFile(fileBytes, UPLOAD_DIR, uuid);
        this.saveEncryptedThumbnail(fileBytes, uuid);

        Image image = new Image(title, uuid);
        return this.imageRepository.save(image);
    }

    public List<Image> getAllImages() {
        return this.imageRepository.findAll();
    }

    public List<Image> getFilteredImages(List<Tag> tags) {
        return this.imageRepository.findImagesByAnyTag(new HashSet<>(tags));
    }

    public byte[] getImageData(Integer id) throws Exception {
        Image image = imageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Image not found"));
        return this.loadDecryptedFile(UPLOAD_DIR + image.getName() + ".enc");
    }

    public byte[] getThumbnailData(Integer id) throws Exception {
        Image image = imageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Image not found"));

        return this.loadDecryptedFile(THUMB_DIR + image.getName() + ".enc");
    }

    public void deleteImage(Integer id) {
        Image image = imageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Image not found"));
        try {
            Files.deleteIfExists(Paths.get(UPLOAD_DIR + image.getName() + ".enc"));
            Files.deleteIfExists(Paths.get(THUMB_DIR + image.getName() + ".enc"));
            this.imageRepository.delete(image);
        } catch (IOException e) {
            throw new RuntimeException("Could not delete physical files", e);
        }
    }

    @Transactional
    public Image addTags(Integer id, List<Integer> tagIds) {
        Image image = imageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Image not found"));
        tagIds.stream()
                .map(this.tagService::getTagById)
                .forEach(image::addTag);
        return this.imageRepository.save(image);
    }

    private void saveEncryptedFile(byte[] data, String directory, String uuid) throws Exception {
        String fullPath = directory + uuid + ".enc";
        SecretKeySpec secretKey = new SecretKeySpec(KEY.substring(0, 16).getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        byte[] encryptedBytes = cipher.doFinal(data);
        Files.write(Paths.get(fullPath), encryptedBytes);
    }

    private void saveEncryptedThumbnail(byte[] originalData, String uuid) throws Exception {
        ImmutableImage img = ImmutableImage.loader().fromBytes(originalData);
        byte[] thumbBytes = img.max(350, 350)
                .bytes(com.sksamuel.scrimage.nio.JpegWriter.Default);
        saveEncryptedFile(thumbBytes, THUMB_DIR, uuid);
    }

    private byte[] loadDecryptedFile(String path) throws Exception {
        byte[] encryptedBytes = Files.readAllBytes(Paths.get(path));

        SecretKeySpec secretKey = new SecretKeySpec(KEY.substring(0, 16).getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        return cipher.doFinal(encryptedBytes);
    }
}