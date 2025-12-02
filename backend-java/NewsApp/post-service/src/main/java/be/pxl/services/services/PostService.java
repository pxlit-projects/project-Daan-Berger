package be.pxl.services.services;

import be.pxl.services.domain.Post;
import be.pxl.services.domain.PostStatus;
import be.pxl.services.domain.dto.PostEditDto;
import be.pxl.services.domain.dto.PostRequest;
import be.pxl.services.domain.dto.PostResponse;
import be.pxl.services.domain.dto.PostStatusRequest;
import be.pxl.services.repository.PostRepository;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService implements IPostService{

    private final PostRepository repository;

    @Override
    public List<PostResponse> getPublishedPosts(String content, String author, String date) {
        List<Post> posts = repository.findByPostStatus(PostStatus.PUBLISHED);

        if (content != null) {
            posts = posts.stream().filter(p -> p.getContent().equals(content)).toList();
        }

        if (author != null) {
            posts = posts.stream().filter(p -> p.getAuthor().equals(author)).toList();
        }

        if (date != null) {
            posts = posts.stream().filter(p -> p.getCreationDate().toString().equals(date)).toList();
        }

        return posts.stream().map(this::mapToPostResponse).toList();
    }

    @Override
    public List<PostResponse> getAllPostsForEditor(String status) {
        List<Post> posts;

        if (status != null && !status.isBlank()) {
            try {
                PostStatus requestedStatus = PostStatus.valueOf(status.toUpperCase());
                posts = repository.findByPostStatus(requestedStatus);
            } catch (IllegalArgumentException e) {
                return List.of();
            }
        } else {
            posts = repository.findAll();
        }

        return posts.stream().map(this::mapToPostResponse).toList();
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

    @Override
    public void updatePostStatus(long postId, PostStatusRequest statusRequest) {
            Post post = repository.findById(postId)
                    .orElseThrow(() -> new NotFoundException("Post not found with id: " + postId));

            post.setPostStatus(statusRequest.status());
            repository.save(post);
    }

    @Override
    public PostResponse getPostById(long postId) {
        Post post = repository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Can't find post with id " + postId));
        return mapToPostResponse(post);
    }


    private PostResponse mapToPostResponse(Post post) {
        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .author(post.getAuthor())
                .content(post.getContent())
                .creationDate(post.getCreationDate())
                .status(post.getPostStatus().toString())
                .build();
    }

}
