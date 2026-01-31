package dev.asedem.mediacloud.model;

import dev.asedem.mediacloud.database.entity.Image;

public record ImageDTO(
        Integer id,
        String title,
        String uploadPath,
        String thumbnailPath) {

    public ImageDTO(Image image) {
        this(
                image.getId(),
                image.getTitle(),
                image.getUploadPath(),
                image.getThumbnailPath());
    }

    public Image toEntity() {
        return new Image(
                this.id,
                this.title,
                this.uploadPath,
                this.thumbnailPath);
    }
}
