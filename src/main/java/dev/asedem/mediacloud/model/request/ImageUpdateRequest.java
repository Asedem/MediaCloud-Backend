package dev.asedem.mediacloud.model.request;

import java.util.List;
import java.util.Map;

public record ImageUpdateRequest(
    String title,
    List<Integer> tagIds,
    Map<Integer, Double> staticTagValues
) {}
