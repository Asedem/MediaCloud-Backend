package dev.asedem.mediacloud.model;

import dev.asedem.mediacloud.database.entity.ImageStaticTagValue;

public record ImageStaticTagValueDTO(
    Integer id,
    StaticTagDefinitionDTO definition,
    Double value
) {
    public ImageStaticTagValueDTO(ImageStaticTagValue entity) {
        this(entity.getId(), new StaticTagDefinitionDTO(entity.getStaticTagDefinition()), entity.getValue());
    }
}
