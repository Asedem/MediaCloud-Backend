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
        Tag tag = new Tag();
        tag.setId(this.id);
        tag.setTitle(this.title);
        tag.setDescription(this.description);
        tag.setColor(this.color);
        return tag;
    }
}