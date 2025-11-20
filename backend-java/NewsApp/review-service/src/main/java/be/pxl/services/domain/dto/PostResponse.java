package be.pxl.services.domain.dto;

import java.time.LocalDateTime;

public record PostResponse(
        Long id,
        String title,
        String content,
        String author,
        LocalDateTime creationDate,
        String status
) {}