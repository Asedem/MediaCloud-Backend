package dev.asedem.mediacloud.model;

import dev.asedem.mediacloud.database.entity.Tag;
import dev.asedem.mediacloud.database.entity.TagCategory;

import java.util.List;
import java.util.stream.Collectors;

public record TagCategoryDTO(
        Integer id,
        String title,
        List<TagDTO> tags) {

    public TagCategoryDTO(TagCategory category) {
        this(
                category.getId(),
                category.getTitle(),
                category.getTags() == null ? List.of()
                        : category.getTags().stream()
                                .map(TagDTO::new)
                                .toList());
    }

    public TagCategory toEntity() {
        TagCategory category = new TagCategory();
        category.setId(this.id);
        category.setTitle(this.title);

        List<Tag> tagEntities = this.tags.stream()
                .map(dto -> {
                    Tag tag = dto.toEntity();
                    tag.setCategory(category);
                    return tag;
                })
                .collect(Collectors.toList());
        category.setTags(tagEntities);

        return category;
    }
}