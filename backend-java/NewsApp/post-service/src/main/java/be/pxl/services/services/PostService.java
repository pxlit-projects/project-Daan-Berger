package be.pxl.services.services;

import be.pxl.services.domain.Post;
import be.pxl.services.domain.PostStatus;
import be.pxl.services.domain.dto.PostEditDto;
import be.pxl.services.domain.dto.PostRequest;
import be.pxl.services.domain.dto.PostResponse;
import be.pxl.services.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService implements IPostService{

    private final PostRepository repository;

    @Override
    public List<PostResponse> getAllPosts() {
        List<Post> posts = repository.findAll();
        return posts.stream()
                .map(p -> new PostResponse(
                        p.getId(),
                        p.getTitle(),
                        p.getContent(),
                        p.getAuthor(),
                        p.getCreationDate(),
                        p.getPostStatus())).toList();
    }

    @Override
    public void addNewPost(PostRequest postRequest) {
        Post post = Post.builder()
                .title(postRequest.getTitle())
                .content(postRequest.getContent())
                .author(postRequest.getAuthor())
                .creationDate(LocalDateTime.now())
                .build();

        post.setPostStatus(postRequest.isDraft() ? PostStatus.DRAFT : PostStatus.PENDING);

        repository.save(post);
    }

    @Override
    public void editPost(PostEditDto postEditDto, long postId) {
        Post post = repository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post with " + postId + " not found"));

        if (postEditDto != null) {
            if (postEditDto.getTitle() != null && !postEditDto.getTitle().isBlank()) {
                post.setTitle(postEditDto.getTitle());
            }

            if (postEditDto.getContent() != null && !postEditDto.getContent().isBlank()) {
                post.setContent(postEditDto.getContent());
            }

            if (postEditDto.getAuthor() != null && !postEditDto.getAuthor().isBlank()) {
                post.setAuthor(postEditDto.getAuthor());
            }

            if (postEditDto.isDraft()) {
                post.setPostStatus(PostStatus.DRAFT);
            } else {
                post.setPostStatus(PostStatus.PENDING);
            }
        }
        repository.save(post);
    }

}
