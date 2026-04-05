package dev.asedem.mediacloud.model;

import dev.asedem.mediacloud.database.entity.Image;

import java.util.Comparator;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record ImageDTO(
        Integer id,
        String title,
        @JsonIgnore String name,
        List<TagDTO> tags,
        List<ImageStaticTagValueDTO> staticTagValues) {

    public ImageDTO(Image image) {
        this(
                image.getId(),
                image.getTitle(),
                image.getName(),
                image.getTags() == null ? List.of()
                        : image.getTags().stream()
                                .map(TagDTO::new)
                                .sorted(Comparator.comparing(TagDTO::id))
                                .sorted(Comparator.comparing(TagDTO::color))
                                .toList(),
                image.getStaticTagValues() == null ? List.of()
                        : image.getStaticTagValues().stream()
                                .map(ImageStaticTagValueDTO::new)
                                .toList());
    }

    public Image toEntity() {
        Image image = new Image();
        image.setId(this.id);
        image.setTitle(this.title);
        image.setName(this.name);

        if (this.tags != null) {
            this.tags.forEach(tagDto -> {
                image.addTag(tagDto.toEntity());
            });
        }

        return image;
    }
}