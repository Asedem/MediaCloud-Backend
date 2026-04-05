package dev.asedem.mediacloud.model;

import dev.asedem.mediacloud.database.entity.StaticTagDefinition;

public record StaticTagDefinitionDTO(
    Integer id,
    String title,
    String description
) {
    public StaticTagDefinitionDTO(StaticTagDefinition definition) {
        this(definition.getId(), definition.getTitle(), definition.getDescription());
    }

    public StaticTagDefinition toEntity() {
        return new StaticTagDefinition(this.id, this.title, this.description);
    }
}
