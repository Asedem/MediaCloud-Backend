package dev.asedem.mediacloud.controller;

import dev.asedem.mediacloud.database.entity.Image;
import dev.asedem.mediacloud.database.entity.Tag;
import dev.asedem.mediacloud.model.ImageDTO;
import dev.asedem.mediacloud.model.TagDTO;
import dev.asedem.mediacloud.model.request.ImageFilterRequest;
import dev.asedem.mediacloud.service.ImageService;
import lombok.AllArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/images")
@AllArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @PostMapping("/upload")
    public ResponseEntity<ImageDTO> uploadImage(@RequestParam String title,
            @RequestParam MultipartFile file) throws Exception {
        return ResponseEntity.ok(new ImageDTO(this.imageService.uploadImage(title, file)));
    }

    @PostMapping("/upload-remote")
    public ResponseEntity<ImageDTO> uploadRemoteImage(
            @RequestParam String title,
            @RequestParam String sourceUrl) throws Exception {
        return ResponseEntity.ok(new ImageDTO(this.imageService.uploadImageFromUrl(title, sourceUrl)));
    }

    @GetMapping
    public ResponseEntity<List<ImageDTO>> getAllImages() {
        return ResponseEntity.ok(this.imageService
                .getAllImages()
                .stream()
                .map(ImageDTO::new)
                .toList());
    }

    @PostMapping("/filtered")
    public ResponseEntity<List<ImageDTO>> getFilteredImages(@RequestBody ImageFilterRequest filter) {
        List<Tag> tagEntities = (filter.tags() != null)
                ? filter.tags().stream().map(TagDTO::toEntity).toList()
                : null;

        List<Image> images = this.imageService.getFilteredImages(tagEntities, filter.title());

        return ResponseEntity.ok(
                images.stream()
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

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteImage(@PathVariable Integer id) {
        this.imageService.deleteImage(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/tags")
    public ResponseEntity<ImageDTO> addTagsToImage(@PathVariable Integer id, @RequestBody List<Integer> tagIds) {
        return ResponseEntity.ok(new ImageDTO(this.imageService.addTags(id, tagIds)));
    }
}
