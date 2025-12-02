package be.pxl.services.services;

import be.pxl.services.client.PostClient;
import be.pxl.services.domain.Comment;
import be.pxl.services.domain.dto.CommentResponse;
import be.pxl.services.domain.dto.CommentUpdateDto;
import be.pxl.services.domain.dto.CreateCommentRequest;
import be.pxl.services.repository.CommentRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService implements ICommentService{

    private final CommentRepository repository;
    private final PostClient postClient;

    private static final Logger logger = LoggerFactory.getLogger(CommentService.class);

    @Override
    public void createComment(CreateCommentRequest commentRequest, Long postId) {

        try {
            postClient.getPostById(postId);
        } catch (FeignException.NotFound e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Can't find post with id " + postId);
        } catch (FeignException e) {
            logger.error("Can't access Post Service", e);

            throw new RuntimeException("Can't create comment.");
        }

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
    public void updateComment(Long commentId, CommentUpdateDto updateDto) {
        Comment comment = repository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("comment with " + commentId + " not found"));

        comment.setContent(updateDto.getContent());

        repository.save(comment);
    }

    @Override
    public void deleteComment(Long commentId) {
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
