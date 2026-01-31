package dev.asedem.mediacloud.model;

import dev.asedem.mediacloud.database.entity.Tag;

public record TagDTO(
        Integer id,
        String title,
        String description,
        String color) {

    public TagDTO(Tag tag) {
        this(
                tag.getId(),
                tag.getTitle(),
                tag.getDescription(),
                tag.getColor());
    }

    public Tag toEntity() {
        return new Tag(
                this.id,
                this.title,
                this.description,
                this.color,
                null);
    }
}