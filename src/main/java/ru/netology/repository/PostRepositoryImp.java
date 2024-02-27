package ru.netology.repository;

import org.springframework.stereotype.Repository;
import ru.netology.exception.NotFoundException;
import ru.netology.model.Post;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class PostRepositoryImp implements PostRepository {
    private final ConcurrentHashMap<Long, Post> data = new ConcurrentHashMap<>();
    private final AtomicLong idPost = new AtomicLong(0);

    public List<Post> all() {
        List<Post> posts = new CopyOnWriteArrayList<>();
        posts.addAll(data.values().stream()
                .filter(post -> !post.isRemoved())
                .collect(Collectors.toList()));
        return posts;
    }

    public Optional<Post> getById(long id) {
        if (!data.get(id).isRemoved()) {
            return Optional.ofNullable(data.get(id));
        } else {
            throw new NotFoundException();
        }
    }

    public Post save(Post post) {
        if (post.getId() == 0) {
            //сохранение новых данных
            post.setId(idPost.get());
            data.put(idPost.get(), post);
            idPost.incrementAndGet();
        } else {
            //обновление данных
            if (data.containsKey(post.getId()) && !data.get(post.getId()).isRemoved()) {
                data.put(post.getId(), post);
            } else {
                //по указанному id нет данных
                throw new NotFoundException();
            }
        }
        return post;
    }

    public void removeById(long id) {
        if (data.containsKey(id)) {
            data.get(id).setRemoved(true);
//            data.remove(id);
        }
    }
}
