package be.pxl.services.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostEditDto {
    private String title;
    private String content;
    private String author;
    private boolean isDraft;
}
