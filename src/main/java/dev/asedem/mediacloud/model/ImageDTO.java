package dev.asedem.mediacloud.model;

import dev.asedem.mediacloud.database.entity.Image;

public record ImageDTO(
    Integer id,
    String title,
    String filePath
) {

    public ImageDTO(Image image) {
        this(
            image.getId(),
            image.getTitle(),
            image.getFilePath()
        );
    }

    public Image toEntity() {
        return new Image(
            this.id,
            this.title,
            this.filePath
        );
    }
}
