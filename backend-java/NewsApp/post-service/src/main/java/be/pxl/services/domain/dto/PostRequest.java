package be.pxl.services.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostRequest {
    @NotNull
    private String title;
    @NotNull
    private String content;
    @NotNull
    private String author;
    private boolean isDraft;
}
