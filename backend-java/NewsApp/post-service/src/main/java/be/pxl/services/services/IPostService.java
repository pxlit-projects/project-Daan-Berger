package be.pxl.services.services;

import be.pxl.services.domain.dto.PostRequest;
import be.pxl.services.domain.dto.PostResponse;
import jakarta.validation.Valid;

import java.util.List;

public interface IPostService {
    List<PostResponse> getAllPosts();

    void addNewPost(@Valid PostRequest postRequest);
}
