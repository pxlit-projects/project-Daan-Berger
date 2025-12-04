package be.pxl.services.services;

import be.pxl.services.domain.Post;
import be.pxl.services.domain.PostStatus;
import be.pxl.services.domain.dto.PostEditDto;
import be.pxl.services.domain.dto.PostRequest;
import be.pxl.services.domain.dto.PostResponse;
import be.pxl.services.domain.dto.PostStatusRequest;
import be.pxl.services.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService implements IPostService{

    private final PostRepository repository;

    @RabbitListener(queues = "post-status-queue")
    public void receiveNotification(String message) {
        log.info("Notification received: {}", message);
    }

    @Override
    public List<PostResponse> getPublishedPosts(String content, String author, String date) {
        log.debug("Fetching published posts with filters - content: {}, author: {}, date: {}", content, author, date);

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

        List<PostResponse> postResponses = posts.stream().map(this::mapToPostResponse).toList();

        log.info("Returning {} published posts", postResponses.size());

        return postResponses;
    }

    @Override
    public List<PostResponse> getAllPostsForEditor(String status) {
        List<Post> posts;

        if (status != null && !status.isBlank()) {
            try {
                PostStatus requestedStatus = PostStatus.valueOf(status.toUpperCase());
                posts = repository.findByPostStatus(requestedStatus);
            } catch (IllegalArgumentException e) {
                log.error("Invalid status requested: {}", status);
                return List.of();
            }
        } else {
            posts = repository.findAll();
        }

        List<PostResponse> postResponses = posts.stream().map(this::mapToPostResponse).toList();

        log.info("returning {} posts for editor", postResponses.size());

        return postResponses;
    }


    @Override
    public PostResponse addNewPost(PostRequest postRequest, String author) {
        Post post = Post.builder()
                .title(postRequest.getTitle())
                .content(postRequest.getContent())
                .author(author)
                .creationDate(LocalDateTime.now())
                .build();

        post.setPostStatus(postRequest.isDraft() ? PostStatus.DRAFT : PostStatus.PENDING);

        repository.save(post);

        log.info("Adding new post: id: {} by {}", post.getId(), post.getAuthor());

        return mapToPostResponse(post);
    }

    @Override
    public PostResponse editPost(PostEditDto postEditDto, long postId) {
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

        log.info("Editing post with id: {}. Edited post: {}", postId, post);

        return mapToPostResponse(post);
    }

    @Override
    public void updatePostStatus(long postId, PostStatusRequest statusRequest) {
            Post post = repository.findById(postId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Can't find post with id " + postId));

            String oldStatus = post.getPostStatus().toString();

            post.setPostStatus(statusRequest.status());

            String newStatus = post.getPostStatus().toString();

            repository.save(post);

            log.info("Updating post status for post with id {}, from {} to {}", postId, oldStatus, newStatus);
    }

    @Override
    public PostResponse getPostById(long postId) {
        Post post = repository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Can't find post with id " + postId));

        log.debug("Fetched post: {}", postId);

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
