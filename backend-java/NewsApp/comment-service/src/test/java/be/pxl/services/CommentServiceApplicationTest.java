package be.pxl.services;


import be.pxl.services.domain.Comment;
import be.pxl.services.domain.dto.CreateCommentRequest;
import be.pxl.services.repository.CommentRepository;
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

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
public class CommentServiceApplicationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CommentRepository repository;

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
    public void createComment() throws Exception {
        CreateCommentRequest request = CreateCommentRequest.builder()
                .content("New comment")
                .author("Bob")
                .build();

        String requestString = objectMapper.writeValueAsString(request);

        long postId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.post("/api/comments/{postId}", postId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestString))
                .andExpect(status().isCreated());

    }

    @Test
    public void getAllComments() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/api/comments")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].author").value("Bob"))
                .andExpect(jsonPath("$[0].content").value("This is a test comment"))
                .andExpect(jsonPath("$[1].author").value("Alice"))
                .andExpect(jsonPath("$[1].content").value("This is a second test comment"));
    }

    @Test
    public void deleteComment() throws Exception {
        Comment comment = repository.findAll().getFirst();
        long commentId = comment.getId();

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/comments/{commentId}", commentId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted());
    }

}
