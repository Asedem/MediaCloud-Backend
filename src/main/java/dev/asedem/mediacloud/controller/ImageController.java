package dev.asedem.mediacloud.controller;

import dev.asedem.mediacloud.database.entity.Image;
import dev.asedem.mediacloud.database.repository.ImageRepository;
import dev.asedem.mediacloud.service.ImageService;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    private final ImageRepository imageRepository;
    private final ImageService imageService;

    public ImageController(ImageRepository imageRepository, ImageService imageService) {
        this.imageRepository = imageRepository;
        this.imageService = imageService;
    }

    @PostMapping("/upload")
    public ResponseEntity<Image> uploadImage(@RequestParam String title, 
                                             @RequestParam MultipartFile file) throws Exception {
        String path = imageService.saveEncryptedFile(file);
        
        Image image = new Image(title, path);
        
        return ResponseEntity.ok(imageRepository.save(image));
    }

    @GetMapping
    public List<Image> getAllImages() {
        return imageRepository.findAll();
    }

    @GetMapping(value = "/{id}/raw", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getImageData(@PathVariable Integer id) throws Exception {
        Image image = imageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Image not found"));

        byte[] imageBytes = imageService.loadDecryptedFile(image.getFilePath());
        return ResponseEntity.ok(imageBytes);
    }
}
