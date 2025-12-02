package be.pxl.services;

import be.pxl.services.client.PostClient;
import be.pxl.services.domain.Comment;
import be.pxl.services.domain.dto.CommentResponse;
import be.pxl.services.domain.dto.CommentUpdateDto;
import be.pxl.services.domain.dto.CreateCommentRequest;
import be.pxl.services.repository.CommentRepository;
import be.pxl.services.services.CommentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;



@ExtendWith(MockitoExtension.class)
public class CommentServiceUnitTests {

    @Mock
    CommentRepository repository;

    @Mock
    PostClient postClient;

    @InjectMocks
    CommentService service;

    @Test
    public void createComment_ShouldSaveComment() {
        long postId = 1L;

        CreateCommentRequest commentRequest = CreateCommentRequest.builder()
                .content("This is a test comment")
                .author("Bob")
                .build();

        service.createComment(commentRequest,postId);

        Mockito.verify(repository, Mockito.times(1))
                .save(Mockito.any(Comment.class));
    }

    @Test
    public void getAllComments_ShouldReturnListOfCommentResponses() {
        Comment comment1 = Comment.builder()
                .id(1L)
                .postId(10L)
                .content("First comment")
                .author("Bob")
                .creationDate(LocalDateTime.now())
                .build();

        Comment comment2 = Comment.builder()
                .id(2L)
                .postId(11L)
                .content("Second comment")
                .author("Alice")
                .creationDate(LocalDateTime.now())
                .build();

        Mockito.when(repository.findAll()).thenReturn(List.of(comment1, comment2));

        List<CommentResponse> responses = service.getAllComments();

        assertEquals(2, responses.size());
        assertEquals("Bob", responses.get(0).getAuthor());
        assertEquals("First comment", responses.get(0).getContent());
        assertEquals("Alice", responses.get(1).getAuthor());
    }

    @Test
    public void updateComment_ShouldUpdateAndSaveComment() {
        Comment existingComment = Comment.builder()
                .id(1L)
                .postId(10L)
                .content("Old content")
                .author("Bob")
                .creationDate(LocalDateTime.now())
                .build();

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(existingComment));

        CommentUpdateDto updateDto = new CommentUpdateDto();
        updateDto.setContent("Updated content");

        service.updateComment(1L, updateDto);

        assertEquals("Updated content", existingComment.getContent());
        Mockito.verify(repository, Mockito.times(1)).save(existingComment);
    }

    @Test
    public void updateComment_ShouldThrowExceptionWhenIdNotFound() {
        Mockito.when(repository.findById(anyLong())).thenReturn(Optional.empty());

        CommentUpdateDto updateDto = new CommentUpdateDto();
        updateDto.setContent("New content");

        assertThrows(RuntimeException.class, () -> service.updateComment(1L, updateDto));
    }

    @Test
    public void deleteComment_ShouldDeleteComment() {
        Comment comment = Comment.builder()
                .id(1L)
                .postId(10L)
                .content("Test comment")
                .author("Bob")
                .creationDate(LocalDateTime.now())
                .build();

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(comment));

        service.deleteComment(1L);

        Mockito.verify(repository, Mockito.times(1)).delete(comment);
    }

    @Test
    public void deleteComment_ShouldThrowExceptionWhenIdNotFound() {
        Mockito.when(repository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.deleteComment(1L));
    }


}
