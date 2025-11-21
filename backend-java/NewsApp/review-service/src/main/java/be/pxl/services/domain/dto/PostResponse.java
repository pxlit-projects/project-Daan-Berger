package be.pxl.services.domain.dto;

import be.pxl.services.domain.PostStatus;

import java.time.LocalDateTime;

public record PostResponse(
        Long id,
        String title,
        String content,
        String author,
        LocalDateTime creationDate,
        PostStatus status
) {}