package jadie.ticketorder.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArticleService {

    private final ArticleRepository articleRepository;

    public ArticleService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    public void findByNameLie(String name) {
        PageRequest pageRequest = PageRequest.of(1, 10);
        Page<Article> articlePages = articleRepository.findByWriterNameLike(name, pageRequest);
        List<Article> content = articlePages.getContent(); // 조회결과 목록
        long count = articlePages.getTotalElements(); // 조건에 해당하는 전체 개수
        int totalPages = articlePages.getTotalPages();// 전체 페이지 번호
        int number = articlePages.getNumber(); // 현재 페이지 번호
        int numberOfElements = articlePages.getNumberOfElements(); // 조회 결과 개수
        int size = articlePages.getSize(); // 페이지 크기
    }
}
