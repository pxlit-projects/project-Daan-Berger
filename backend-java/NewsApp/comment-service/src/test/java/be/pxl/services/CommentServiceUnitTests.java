package be.pxl.services;

import be.pxl.services.client.PostClient;
import be.pxl.services.domain.Comment;
import be.pxl.services.domain.dto.CommentResponse;
import be.pxl.services.domain.dto.CommentUpdateDto;
import be.pxl.services.domain.dto.CreateCommentRequest;
import be.pxl.services.repository.CommentRepository;
import be.pxl.services.services.CommentService;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

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

    @Captor
    ArgumentCaptor<Comment> captor;

    @Test
    public void createComment_ShouldSaveComment() {
        long postId = 1L;
        String author = "Bob";
        String role = "user";

        CreateCommentRequest commentRequest = CreateCommentRequest.builder()
                .content("This is a test comment")
                .build();

        service.createComment(commentRequest, postId, author, role);

        Mockito.verify(repository, Mockito.times(1))
                .save(captor.capture());
        assertEquals("Bob", captor.getValue().getAuthor());
    }

    @Test
    public void createComment_ShouldThrowExceptionWhenPostNotFound() {
        CreateCommentRequest commentRequest = CreateCommentRequest.builder()
                .content("Test comment")
                .build();

        Mockito.when(postClient.getPostById(1L, "user"))
                .thenThrow(FeignException.NotFound.class);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                service.createComment(commentRequest, 1L, "Bob", "user"));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    public void createComment_ShouldThrowExceptionWhenPostServiceUnavailable() {
        CreateCommentRequest commentRequest = CreateCommentRequest.builder()
                .content("Test comment")
                .build();

        Mockito.when(postClient.getPostById(1L, "user"))
                .thenThrow(FeignException.ServiceUnavailable.class);

        assertThrows(RuntimeException.class, () ->
                service.createComment(commentRequest, 1L, "Bob", "user"));
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

        CommentUpdateDto updateDto = CommentUpdateDto.builder()
                        .content("Updated comment")
                        .build();

        service.updateComment(1L, updateDto, "Bob");

        assertEquals(updateDto.getContent(), existingComment.getContent());
        Mockito.verify(repository, Mockito.times(1)).save(existingComment);
    }

    @Test
    public void updateComment_ShouldThrowExceptionWhenIdNotFound() {
        String author = "Bob";

        Mockito.when(repository.findById(anyLong())).thenReturn(Optional.empty());

        CommentUpdateDto updateDto = CommentUpdateDto.builder()
                        .content("New content")
                        .build();

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                service.updateComment(1L, updateDto, author));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    public void updateComment_ShouldThrowExceptionWhenNotAuthor() {
        Comment existingComment = Comment.builder()
                .id(1L)
                .postId(10L)
                .content("Old content")
                .author("Bob")
                .creationDate(LocalDateTime.now())
                .build();

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(existingComment));

        CommentUpdateDto updateDto = CommentUpdateDto.builder()
                        .content("Updated content")
                        .build();

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                service.updateComment(1L, updateDto, "Alice"));

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
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

        service.deleteComment(1L, "Bob");

        Mockito.verify(repository, Mockito.times(1)).delete(comment);
    }

    @Test
    public void deleteComment_ShouldThrowExceptionWhenIdNotFound() {
        String author = "Bob";

        Mockito.when(repository.findById(anyLong())).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                service.deleteComment(1L, author));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    public void deleteComment_ShouldThrowExceptionWhenNotAuthor() {
        Comment comment = Comment.builder()
                .id(1L)
                .postId(10L)
                .content("Test comment")
                .author("Bob")
                .creationDate(LocalDateTime.now())
                .build();

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(comment));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                service.deleteComment(1L, "Alice"));

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
    }
}
