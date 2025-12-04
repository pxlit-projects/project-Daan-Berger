package be.pxl.services;

import be.pxl.services.domain.Post;
import be.pxl.services.domain.PostStatus;
import be.pxl.services.domain.dto.PostEditDto;
import be.pxl.services.domain.dto.PostRequest;
import be.pxl.services.domain.dto.PostResponse;
import be.pxl.services.repository.PostRepository;
import be.pxl.services.services.PostService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(MockitoExtension.class)
public class PostServiceUnitTest {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostService postService;

    @Test
    public void getPublishedPosts_ShouldReturnPublishedPosts() {

        Post publishedPost = Post.builder()
                .id(1L)
                .title("Test post")
                .content("This is a test post")
                .author("John")
                .creationDate(LocalDateTime.now())
                .postStatus(PostStatus.PUBLISHED)
                .build();

        when(postRepository.findByPostStatus(PostStatus.PUBLISHED))
                .thenReturn(List.of(publishedPost));

        List<PostResponse> publishedPosts = postService.getPublishedPosts(null, null, null);

        assertEquals(1, publishedPosts.size());
        assertEquals("Test post", publishedPosts.getFirst().getTitle());
        assertEquals("PUBLISHED", publishedPosts.getFirst().getStatus());
    }

    @Test
    public void getPublishedPosts_ShouldFilterByContent() {
        Post post1 = Post.builder()
                .id(1L)
                .title("Test")
                .content("specific content")
                .author("John")
                .postStatus(PostStatus.PUBLISHED)
                .build();

        Post post2 = Post.builder()
                .id(2L)
                .title("Test 2")
                .content("other content")
                .author("Alice")
                .postStatus(PostStatus.PUBLISHED)
                .build();

        when(postRepository.findByPostStatus(PostStatus.PUBLISHED))
                .thenReturn(List.of(post1, post2));

        List<PostResponse> result = postService.getPublishedPosts("specific content", null, null);

        assertEquals(1, result.size());
        assertEquals("specific content", result.getFirst().getContent());
    }


    @Test
    public void getAllPostsForEditor_ShouldReturnAllPosts_WhenNoStatusProvided() {
        List<Post> posts = List.of(
                Post.builder()
                        .id(1L)
                        .title("Post 1")
                        .postStatus(PostStatus.DRAFT)
                        .build(),

                Post.builder()
                        .id(2L)
                        .title("Post 2")
                        .postStatus(PostStatus.PUBLISHED)
                        .build()
        );

        when(postRepository.findAll()).thenReturn(posts);

        List<PostResponse> result = postService.getAllPostsForEditor(null);

        assertEquals(2, result.size());
    }

    @Test
    public void getAllPostsForEditor_ShouldFilterByStatus() {
        Post draftPost = Post.builder()
                .id(1L)
                .title("Draft Post")
                .postStatus(PostStatus.DRAFT)
                .build();

        when(postRepository.findByPostStatus(PostStatus.DRAFT))
                .thenReturn(List.of(draftPost));

        List<PostResponse> result = postService.getAllPostsForEditor("DRAFT");

        assertEquals(1, result.size());
        assertEquals("DRAFT", result.getFirst().getStatus());
    }

    @Test
    public void getAllPostsForEditor_ShouldReturnEmptyList_WhenInvalidStatus() {
        List<PostResponse> result = postService.getAllPostsForEditor("INVALID_STATUS");

        assertTrue(result.isEmpty());
    }

    @Test
    public void addNewPost_ShouldSetDraftStatus_WhenIsDraftTrue() {
        String author = "Bob";

        PostRequest request = PostRequest.builder()
                .title("Test")
                .content("Content")
                .draft(true)
                .build();

        postService.addNewPost(request, author);

        verify(postRepository).save(argThat(post ->
                post.getPostStatus() == PostStatus.DRAFT));
    }

    @Test
    public void addNewPost_ShouldSetPendingStatus_WhenIsDraftFalse() {
        String author = "Bob";

        PostRequest request = PostRequest.builder()
                .title("Test")
                .content("Content")
                .draft(false)
                .build();

        postService.addNewPost(request, author);

        verify(postRepository).save(argThat(post ->
                post.getPostStatus() == PostStatus.PENDING));
    }



    @Test
    public void editPost_ShouldThrowException_WhenPostIdNotFound() {
        when(postRepository.findById(anyLong())).thenReturn(Optional.empty());

        PostEditDto editDto = new PostEditDto();
        editDto.setTitle("New Title");

        assertThrows(RuntimeException.class, () -> postService.editPost(editDto, 99L));
    }

    @Test
    public void editPost_ShouldUpdateStatusToPending_WhenNotDraft() {
        Post existingPost = Post.builder()
                .id(1L)
                .title("Old Title")
                .postStatus(PostStatus.DRAFT)
                .build();

        when(postRepository.findById(1L)).thenReturn(Optional.of(existingPost));

        PostEditDto editDto = new PostEditDto();
        editDto.setDraft(false);

        postService.editPost(editDto, 1L);

        assert(existingPost.getPostStatus() == PostStatus.PENDING);
    }
}