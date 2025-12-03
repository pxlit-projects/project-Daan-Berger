package be.pxl.services;


import be.pxl.services.client.PostClient;
import be.pxl.services.domain.Comment;
import be.pxl.services.domain.dto.CreateCommentRequest;
import be.pxl.services.domain.dto.PostResponse;
import be.pxl.services.repository.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
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
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.cloud.config.enabled=false",
})
public class CommentServiceApplicationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CommentRepository repository;

    @MockBean
    private PostClient postClient;

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

        Comment firstComment = Comment.builder()
                .postId(10L)
                .content("This is a test comment")
                .author("Bob")
                .creationDate(LocalDateTime.now())
                .build();

        Comment secondComment = Comment.builder()
                .postId(11L)
                .content("This is a second test comment")
                .author("Alice")
                .creationDate(LocalDateTime.now().plusHours(1))
                .build();

        repository.save(firstComment);
        repository.save(secondComment);
    }

    @Test
    public void createComment_ShouldSaveComment() throws Exception {
        CreateCommentRequest request = CreateCommentRequest.builder()
                .content("New comment")
                .build();

        String requestString = objectMapper.writeValueAsString(request);

        long postId = 1L;

        Mockito.when(postClient.getPostById(postId)).thenReturn(
                new PostResponse(
                        postId,
                        "Title",
                        "This is a post",
                        "Bob",
                        LocalDateTime.now(),
                        "PUBLISHED"
                ));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/comment/{postId}", postId)
                        .header("X-User", "TestUser")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestString))
                .andExpect(status().isCreated());

        Comment savedComment = repository.findAll().stream()
                .filter(c -> c.getContent().equals("New comment"))
                .findFirst()
                .orElseThrow();

        assertEquals("TestUser", savedComment.getAuthor());
    }

    @Test
    public void getAllComments_ShouldReturnComments() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/api/comment")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].author").value("Bob"))
                .andExpect(jsonPath("$[0].content").value("This is a test comment"))
                .andExpect(jsonPath("$[1].author").value("Alice"))
                .andExpect(jsonPath("$[1].content").value("This is a second test comment"));
    }

    @Test
    public void deleteComment_ShouldDeleteComment() throws Exception {
        Comment comment = repository.findAll().getFirst();
        long commentId = comment.getId();

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/comment/{commentId}", commentId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

}
