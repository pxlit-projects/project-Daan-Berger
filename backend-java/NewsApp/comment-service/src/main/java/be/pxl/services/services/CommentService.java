package be.pxl.services.services;

import be.pxl.services.domain.Comment;
import be.pxl.services.domain.dto.CommentResponse;
import be.pxl.services.domain.dto.CommentUpdateDto;
import be.pxl.services.domain.dto.CreateCommentRequest;
import be.pxl.services.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService implements ICommentService{

    private final CommentRepository repository;

    @Override
    public void createComment(CreateCommentRequest commentRequest, long postId) {
        Comment comment = Comment.builder()
                .postId(postId)
                .content(commentRequest.getContent())
                .author(commentRequest.getAuthor())
                .creationDate(LocalDateTime.now())
                .build();

        repository.save(comment);
    }

    @Override
    public List<CommentResponse> getAllComments() {
        List<Comment> comments = repository.findAll();
        return comments.stream().map(this::mapToCommentResponse).toList();
    }

    @Override
    public void updateComment(long commentId, CommentUpdateDto updateDto) {
        Comment comment = repository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("comment with " + commentId + " not found"));

        comment.setContent(updateDto.getContent());

        repository.save(comment);
    }

    @Override
    public void deleteComment(long commentId) {
        Comment comment = repository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("comment with " + commentId + " not found"));

        repository.delete(comment);
    }

    private CommentResponse mapToCommentResponse(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .postId(comment.getPostId())
                .content(comment.getContent())
                .author(comment.getAuthor())
                .creationDate(comment.getCreationDate())
                .build();
    }
}
