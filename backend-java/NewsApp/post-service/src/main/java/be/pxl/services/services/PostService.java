package be.pxl.services.services;

import be.pxl.services.domain.Post;
import be.pxl.services.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService implements IPostService{

    private final PostRepository repository;

    @Override
    public List<Post> getAllPosts() {
        return repository.findAll();
    }
}
