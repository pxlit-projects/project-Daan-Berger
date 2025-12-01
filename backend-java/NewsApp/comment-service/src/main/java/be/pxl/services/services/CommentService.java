package be.pxl.services.services;

import be.pxl.services.domain.Comment;
import be.pxl.services.domain.dto.CommentResponse;
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
        return List.of();
    }

    @Override
    public void updateComment(long commentId) {

    }

    @Override
    public void deleteComment(long commentId) {

    }
}
