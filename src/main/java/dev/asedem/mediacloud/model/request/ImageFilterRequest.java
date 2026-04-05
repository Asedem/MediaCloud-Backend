package dev.asedem.mediacloud.model.request;

import java.util.List;

import dev.asedem.mediacloud.model.TagDTO;

public record ImageFilterRequest(
        List<TagDTO> tags,
        String title,
        String filterMode) {
}
