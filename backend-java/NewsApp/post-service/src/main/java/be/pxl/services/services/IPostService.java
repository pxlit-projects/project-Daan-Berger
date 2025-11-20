package be.pxl.services.services;

import be.pxl.services.domain.dto.PostEditDto;
import be.pxl.services.domain.dto.PostRequest;
import be.pxl.services.domain.dto.PostResponse;
import jakarta.validation.Valid;

import java.util.List;

public interface IPostService {
    List<PostResponse> getPublishedPosts(String content, String author, String date);

    List<PostResponse> getAllPostsForEditor();

    void addNewPost(@Valid PostRequest postRequest);

    void editPost(PostEditDto postEditDto, long postId);
}
