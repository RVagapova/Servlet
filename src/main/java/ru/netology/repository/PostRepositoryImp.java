package ru.netology.repository;

import org.springframework.stereotype.Repository;
import ru.netology.model.Post;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class PostRepositoryImp implements PostRepository {
    private final ConcurrentHashMap<Long, Post> data = new ConcurrentHashMap<>();
    private final AtomicLong idPost = new AtomicLong(0);

    public List<Post> all() {
        List<Post> posts = new CopyOnWriteArrayList<>();
        if (!data.isEmpty()) {
            posts.addAll(data.values());
        }
        return posts;
    }

    public Optional<Post> getById(long id) {
        return Optional.ofNullable(data.get(id));
    }

    public Post save(Post post) {
        if (post.getId() == 0) {
            //сохранение новых данных
            post.setId(idPost.get());
            data.put(idPost.get(), post);
            idPost.incrementAndGet();
        } else {
            //обновление данных
            if (data.containsKey(post.getId())) {
                data.put(post.getId(), post);
            } else {
                //по указанному id нет данных
                post.setContent("NOT FOUND ID " + post.getId());
            }
        }
        return post;
    }

    public void removeById(long id) {
        if (data.containsKey(id)) {
            data.remove(id);
        }
    }
}
