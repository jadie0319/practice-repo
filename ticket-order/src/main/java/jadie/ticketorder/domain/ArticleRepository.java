package jadie.ticketorder.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    //List<Article> findByWriterNameLike(String name, Pageable pageable);

    Page<Article> findByWriterNameLike(String name, Pageable pageable);
}
