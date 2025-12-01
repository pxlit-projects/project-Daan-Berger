package be.pxl.services.domain.dto;

public record CreateCommentRequest(Long postId, String content) {}
