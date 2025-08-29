package kr.adapterz.jpa_practice;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import kr.adapterz.jpa_practice.entity.Post;
import kr.adapterz.jpa_practice.entity.PostType;
import kr.adapterz.jpa_practice.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@Transactional
public class FetchJoinTest {

    @PersistenceContext
    EntityManager entityManager;

    @Test
    @Rollback(false)
    void nPlusOneProblemTest() {
        // 3개의 유저 및 게시글 더미 데이터 추가
        for (int i = 1; i <= 3; i++) {
            User user = new User(
                    "tester" + i + "@adapterz.kr",
                    "123aS!" + i,
                    "Adapterz" + i
            );
            entityManager.persist(user);

            Post post = new Post(
                    "test title" + i,
                    "test content" + i,
                    PostType.NOTICE,
                    user
            );
            entityManager.persist(post);
        }

        entityManager.flush();
        entityManager.clear();

        // 모든 User를 한 번에 조회
        List<User> findAllUsers = entityManager.createQuery("select u from User u", User.class)
                .getResultList();

        // 각 User의 posts 접근 시점에 LAZY 로딩으로 추가 SELECT 발생
        for (User findUser : findAllUsers) {
            int size = findUser.getPosts().size(); // 여기서 사용자 별로 select 문 날라감
            System.out.println("findUser.getNickname() = " + findUser.getNickname() + ", posts.size() = " + size);
        }
    }

    @Test
    @Rollback(false)
    void solvedWithFetchJoinTest() {
        // 3개의 유저 및 게시글 더미 데이터 추가
        for (int i = 1; i <= 3; i++) {
            User user = new User(
                    "tester" + i + "@adapterz.kr",
                    "123aS!" + i,
                    "Adapterz" + i
            );
            entityManager.persist(user);

            Post post = new Post(
                    "test title" + i,
                    "test content" + i,
                    PostType.NOTICE,
                    user
            );
            entityManager.persist(post);
        }

        entityManager.flush();
        entityManager.clear();

        // 모든 User를 한 번에 조회 (fetch join 사용)
        List<User> findAllUsers = entityManager.createQuery("select u from User u join fetch u.posts", User.class)
                .getResultList();

        // posts 접근 시 추가 쿼리 없음
        for (User findUser : findAllUsers) {
            int size = findUser.getPosts().size(); // 여기서 사용자 별로 select 문 날라감
            System.out.println("findUser.getNickname() = " + findUser.getNickname() + ", posts.size() = " + size);
        }
    }
}
