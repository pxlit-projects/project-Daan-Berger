package be.pxl.services.services;

import be.pxl.services.domain.dto.CommentResponse;
import be.pxl.services.domain.dto.CreateCommentRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService implements ICommentService{
    @Override
    public void createComment(CreateCommentRequest commentRequest) {

    }

    @Override
    public List<CommentResponse> getAllComments() {
        return List.of();
    }

    @Override
    public void updateComment(Long commentId) {

    }

    @Override
    public void deleteComment(Long commentId) {

    }
}
