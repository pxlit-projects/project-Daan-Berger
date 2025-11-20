package be.pxl.services;

import be.pxl.services.domain.Post;
import be.pxl.services.domain.PostStatus;
import be.pxl.services.domain.dto.PostEditDto;
import be.pxl.services.repository.PostRepository;
import be.pxl.services.services.PostService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(MockitoExtension.class)
public class PostServiceUnitTest {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostService postService;

    @Test
    public void editPost_ShouldThrowException_WhenPostIdNotFound() {
        when(postRepository.findById(anyLong())).thenReturn(Optional.empty());

        PostEditDto editDto = new PostEditDto();
        editDto.setTitle("Nieuwe Titel");

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