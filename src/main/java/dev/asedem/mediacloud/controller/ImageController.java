package dev.asedem.mediacloud.controller;

import dev.asedem.mediacloud.model.ImageDTO;
import dev.asedem.mediacloud.service.ImageService;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping("/upload")
    public ResponseEntity<ImageDTO> uploadImage(@RequestParam String title, 
                                             @RequestParam MultipartFile file) throws Exception {
        return ResponseEntity.ok(new ImageDTO(this.imageService.uploadImage(title, file)));
    }

    @GetMapping
    public ResponseEntity<List<ImageDTO>> getAllImages() {
        return ResponseEntity.ok(this.imageService
            .getAllImages()
            .stream()
            .map(ImageDTO::new)
            .toList());
    }

    @GetMapping(value = "/{id}/preview", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getThumbnailData(@PathVariable Integer id) throws Exception {
        return ResponseEntity.ok(this.imageService.getThumbnailData(id));
    }

    @GetMapping(value = "/{id}/raw", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getImageData(@PathVariable Integer id) throws Exception {
        return ResponseEntity.ok(this.imageService.getImageData(id));
    }
}
