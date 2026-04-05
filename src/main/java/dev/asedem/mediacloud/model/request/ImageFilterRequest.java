package dev.asedem.mediacloud.model.request;

import java.util.List;
import java.util.Map;

import dev.asedem.mediacloud.model.TagDTO;
import dev.asedem.mediacloud.model.RangeFilterDTO;

public record ImageFilterRequest(
        List<TagDTO> tags,
        String title,
        String filterMode,
        Map<Integer, RangeFilterDTO> staticTagFilters,
        int page,
        int size,
        String sortBy,
        String sortDirection) {
}
