package be.pxl.services;

import be.pxl.services.client.PostClient;
import be.pxl.services.domain.PostStatus;
import be.pxl.services.domain.dto.PostResponse;
import be.pxl.services.domain.dto.RejectRequest;
import be.pxl.services.repository.ReviewRepository;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
public class ReviewServiceTests {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ReviewRepository repository;

    @MockBean
    private PostClient client;

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

        List<PostResponse> pendingResponses = new ArrayList<>();
        pendingResponses.add(new PostResponse(
                1L,
                "Published post",
                "This is a published post",
                "John Doe",
                LocalDateTime.now(),
                PostStatus.PENDING
        ));

        Mockito.when(client.getPendingPosts("editor")).thenReturn(pendingResponses);
    }

    @Test
    public void getPendingPosts_ShouldReturnPosts_WhenRoleIsEditor() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/review/pending")
                        .header("X-Role", "editor")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Published post"))
                .andExpect(jsonPath("$[0].status").value("PENDING"));
    }

    @Test
    public void approvePost_ShouldReturnOk_WhenRoleIsEditor() throws Exception {
        Long postId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.post("/api/review/{postId}/approve", postId)
                .header("X-Role", "editor")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void approvePost_ShouldReturnForbidden_WhenRoleIsNotEditor() throws Exception {
        Long postId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.post("/api/review/{postId}/approve", postId)
                        .header("X-Role", "user")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void rejectPost_ShouldReturnOk_WhenRoleIsEditor() throws Exception {
        Long postId = 1L;

        RejectRequest rejectRequest = new RejectRequest("Reject reason");

        String requestString = objectMapper.writeValueAsString(rejectRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/review/{postId}/reject", postId)
                        .header("X-Role", "editor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestString))
                        .andExpect(status().isOk());
    }

    @Test
    public void rejectPost_ShouldReturnForbidden_WhenRoleIsNotEditor() throws Exception {
        Long postId = 1L;

        RejectRequest rejectRequest = new RejectRequest("Reject reason");

        String requestString = objectMapper.writeValueAsString(rejectRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/review/{postId}/reject", postId)
                        .header("X-Role", "user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestString))
                        .andExpect(status().isForbidden());
    }
}
