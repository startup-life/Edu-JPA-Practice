package kr.adapterz.jpa_practice;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import kr.adapterz.jpa_practice.entity.Post;
import kr.adapterz.jpa_practice.entity.PostType;
import kr.adapterz.jpa_practice.entity.User;
import kr.adapterz.jpa_practice.entity.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@Transactional
public class PagingTest {

    @PersistenceContext
    EntityManager entityManager;

    @Test
    @Rollback(false)
    void pagingTest() {
        User masterUser = new User("master@adapterz.kr", "pw", "Master", UserRole.ADMIN);
        User userUser = new User("tester@adapterz.kr", "pw", "Tester", UserRole.USER);
        entityManager.persist(masterUser);
        entityManager.persist(userUser);

        for (int i = 1; i <= 100; i++) {
            User author = (i % 2 == 0) ? masterUser : userUser;
            PostType type = (i % 4 == 0) ? PostType.FREE : PostType.NOTICE;
            entityManager.persist(new Post("제목" + i, "내용" + i, type, author));
        }

        entityManager.flush();
        entityManager.clear();

        int page = 0;  // 0부터 시작: 0, 1, 2 ...
        int size = 10; // 페이지 크기
        int offset = page * size;

        // 안정적인 페이징을 위해 정렬 필수
        List<Post> pageData = entityManager.createQuery("select p from Post p order by p.id", Post.class)
                .setFirstResult(offset)
                .setMaxResults(size)
                .getResultList();

        Long total = entityManager.createQuery("select count(p) from Post p", Long.class)
                .getSingleResult();

        int totalPages = (int) Math.ceil(total / (double) size);

        System.out.println("current page = " + page);
        System.out.println("page size = " + size);
        System.out.println("total elements = " + total);
        System.out.println("total pages = " + totalPages);

        pageData.forEach(p ->
                System.out.println("postId=" + p.getId() + ", title=" + p.getTitle())
        );
    }

    @Test
    @Rollback(false)
    void pagingWithFetchJoinTest() {
        User masterUser = new User("master@adapterz.kr", "pw", "Master", UserRole.ADMIN);
        User userUser = new User("tester@adapterz.kr", "pw", "Tester", UserRole.USER);
        entityManager.persist(masterUser);
        entityManager.persist(userUser);

        for (int i = 1; i <= 100; i++) {
            User author = (i % 2 == 0) ? masterUser : userUser;
            PostType type = (i % 4 == 0) ? PostType.FREE : PostType.NOTICE;
            entityManager.persist(new Post("제목" + i, "내용" + i, type, author));
        }

        entityManager.flush();
        entityManager.clear();

        int page = 0;
        int size = 5;
        int offset = page * size;

        // ManyToOne fetch join 은 페이징과 함께 사용 가능
        List<Post> result = entityManager.createQuery(
                        "select p from Post p join fetch p.author order by p.id", Post.class)
                .setFirstResult(offset)
                .setMaxResults(size)
                .getResultList();

        // author 접근 시 추가 쿼리 없음
        result.forEach(p ->
                System.out.println("postId=" + p.getId()
                        + ", title=" + p.getTitle()
                        + ", author.Nickname=" + p.getAuthor().getNickname())
        );
    }
}
