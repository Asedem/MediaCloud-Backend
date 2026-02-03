package dev.asedem.mediacloud.model;

import dev.asedem.mediacloud.database.entity.Image;

import java.util.Comparator;
import java.util.List;

public record ImageDTO(
        Integer id,
        String title,
        String uploadPath,
        String thumbnailPath,
        List<TagDTO> tags) {

    public ImageDTO(Image image) {
        this(
                image.getId(),
                image.getTitle(),
                image.getUploadPath(),
                image.getThumbnailPath(),
                image.getTags() == null ? List.of()
                        : image.getTags().stream()
                                .map(TagDTO::new)
                                .sorted(Comparator.comparing(TagDTO::id))
                                .sorted(Comparator.comparing(TagDTO::color))
                                .toList());
    }

    public Image toEntity() {
        Image image = new Image();
        image.setId(this.id);
        image.setTitle(this.title);
        image.setUploadPath(this.uploadPath);
        image.setThumbnailPath(this.thumbnailPath);

        if (this.tags != null) {
            this.tags.forEach(tagDto -> {
                image.addTag(tagDto.toEntity());
            });
        }

        return image;
    }
}