package dev.asedem.mediacloud.model.request;

import java.util.List;

public record ImageUpdateRequest(
    String title,
    List<Integer> tagIds
) {}
