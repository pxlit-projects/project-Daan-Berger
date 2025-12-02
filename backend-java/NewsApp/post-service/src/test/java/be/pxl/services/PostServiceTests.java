package be.pxl.services;

import be.pxl.services.domain.Post;
import be.pxl.services.domain.PostStatus;
import be.pxl.services.domain.dto.PostEditDto;
import be.pxl.services.domain.dto.PostRequest;
import be.pxl.services.domain.dto.PostStatusRequest;
import be.pxl.services.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
public class PostServiceTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PostRepository repository;

    @Container
    private static MySQLContainer sqlContainer =
            new MySQLContainer("mysql:5.7.37");

    @DynamicPropertySource
    static void registerMySQLProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", sqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", sqlContainer::getUsername);
        registry.add("spring.datasource.password", sqlContainer::getPassword);
    }

    @BeforeEach
    public void setup() {
        repository.deleteAll();

        Post publishedPost = Post.builder()
                .title("Published Post")
                .content("Content")
                .author("Bob")
                .postStatus(PostStatus.PUBLISHED)
                .creationDate(LocalDateTime.now())
                .build();

        Post draftPost = Post.builder()
                .title("Draft Post")
                .content("Content")
                .author("Bob")
                .postStatus(PostStatus.DRAFT)
                .creationDate(LocalDateTime.now())
                .build();

        repository.save(publishedPost);
        repository.save(draftPost);
    }

    @Test
    public void getPublishedPostsTest() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/api/post")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Published Post"))
                .andExpect(jsonPath("$[0].status").value("PUBLISHED"));
    }

    @Test
    public void getPostsForEditor_ShouldReturnOk_WhenRoleIsEditor() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/api/post/editor")
                        .header("X-Role", "editor")
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk());
    }

    @Test
    public void getPostsForEditor_ShouldReturnForbidden_WhenRoleIsNotEditor() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/post/editor")
                        .header("X-Role", "user")
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isForbidden());
    }

    @Test
    public void createPost_ShouldReturnOk_whenRequestIsValid() throws Exception {
        PostRequest postRequest = PostRequest.builder()
                .title("Test post")
                .content("This is a test post")
                .author("John Doe")
                .draft(true)
                .build();


        String postString = objectMapper.writeValueAsString(postRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/post")
                .contentType(MediaType.APPLICATION_JSON)
                .content(postString))
                .andExpect(status().isCreated());
    }

    @Test
    public void editPost_ShouldReturnOk_whenPostIdIsValid() throws Exception {
        Post post = repository.findByPostStatus(PostStatus.DRAFT).getFirst();

        Long postId = post.getId();

        PostEditDto postEditDto = PostEditDto.builder()
                .title("New Title")
                .content("New content")
                .build();

        String editString = objectMapper.writeValueAsString(postEditDto);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/post/{postId}", postId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(editString))
                .andExpect(status().isOk());

        Post updatedPost = repository.findById(postId).orElseThrow();
        assertEquals("New Title", updatedPost.getTitle());
        assertEquals("New content", updatedPost.getContent());
    }

    @Test
    public void updatePostStatus_ShouldUpdateStatus() throws Exception {
        Post post = repository.findByPostStatus(PostStatus.DRAFT).getFirst();
        Long postId = post.getId();

        PostStatusRequest statusRequest = new PostStatusRequest(PostStatus.PUBLISHED);
        String statusString = objectMapper.writeValueAsString(statusRequest);

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/post/{postId}/status", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(statusString))
                .andExpect(status().isOk());

        Post updatedPost = repository.findById(postId).orElseThrow();
        assertEquals(PostStatus.PUBLISHED, updatedPost.getPostStatus());
    }

    @Test
    public void updatePostStatus_ShouldReturnNotFound_WhenPostDoesNotExist() throws Exception {
        Long nonExistentPostId = 999L;

        PostStatusRequest statusRequest = new PostStatusRequest(PostStatus.PUBLISHED);
        String statusString = objectMapper.writeValueAsString(statusRequest);

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/post/{postId}/status", nonExistentPostId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(statusString))
                .andExpect(status().isNotFound());
    }

}
