package be.pxl.services.services;

import be.pxl.services.client.PostClient;
import be.pxl.services.domain.Comment;
import be.pxl.services.domain.dto.CommentResponse;
import be.pxl.services.domain.dto.CommentUpdateDto;
import be.pxl.services.domain.dto.CreateCommentRequest;
import be.pxl.services.repository.CommentRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService implements ICommentService{

    private final CommentRepository repository;
    private final PostClient postClient;

    @Override
    public void createComment(CreateCommentRequest commentRequest, Long postId, String author) {
        try {
            postClient.getPostById(postId);
        } catch (FeignException.NotFound e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Can't find post with id " + postId);
        } catch (FeignException e) {
            log.error("Can't access Post Service", e);
            throw new RuntimeException("Can't create comment.");
        }

        Comment comment = Comment.builder()
                .postId(postId)
                .content(commentRequest.getContent())
                .author(author)
                .creationDate(LocalDateTime.now())
                .build();

        repository.save(comment);
        log.info("Comment with id: {}, created by {}", comment.getId(), author);
    }

    @Override
    public List<CommentResponse> getAllComments() {
        List<Comment> comments = repository.findAll();
        log.info("Fetched {} comments", comments.size());
        return comments.stream().map(this::mapToCommentResponse).toList();
    }

    @Override
    public void updateComment(Long commentId, CommentUpdateDto updateDto) {
        Comment comment = repository.findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "comment with " + commentId + " not found"));

        comment.setContent(updateDto.getContent());

        repository.save(comment);
        log.info("Updated comment with id: {} to: {}", commentId, comment);
    }

    @Override
    public void deleteComment(Long commentId) {
        Comment comment = repository.findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "comment with " + commentId + " not found"));

        repository.delete(comment);
        log.info("Deleted comment with id: {}", commentId);
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
