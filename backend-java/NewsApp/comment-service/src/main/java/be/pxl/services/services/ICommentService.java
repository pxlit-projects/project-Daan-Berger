package be.pxl.services.services;

import be.pxl.services.domain.dto.CommentResponse;
import be.pxl.services.domain.dto.CommentUpdateDto;
import be.pxl.services.domain.dto.CreateCommentRequest;

import java.util.List;

public interface ICommentService {
    CommentResponse createComment(CreateCommentRequest commentRequest, Long postId, String author, String role);
    List<CommentResponse> getAllComments();
    CommentResponse updateComment(Long commentId, CommentUpdateDto updateDto, String author);
    void deleteComment(Long commentId, String author);
}
