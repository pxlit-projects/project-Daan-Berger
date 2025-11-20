package be.pxl.services.services;

import be.pxl.services.domain.Post;
import be.pxl.services.domain.dto.PostResponse;
import be.pxl.services.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService implements IPostService{

    private final PostRepository repository;

    @Override
    public List<PostResponse> getAllPosts() {
        List<Post> posts = repository.findAll();
        return posts.stream().map(p -> new PostResponse(
                p.getTitle(), p.getContent(), p.getAuthor(), p.getCreationDate())).toList();
    }

}
