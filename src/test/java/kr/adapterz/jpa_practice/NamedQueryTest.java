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
public class NamedQueryTest {

    @PersistenceContext
    EntityManager entityManager;

    @Test
    @Rollback(false)
    void namedQueryTest() {
        // 유저 더미 데이터 생성
        User user1 = new User("master@adapterz.kr", "pw", "Master", UserRole.ADMIN);
        User user2 = new User("tester@adapterz.kr", "pw", "Tester", UserRole.USER);
        entityManager.persist(user1);
        entityManager.persist(user2);

        // 게시글 더미 데이터 생성
        entityManager.persist(new Post("공지사항", "내용", PostType.NOTICE, user1));
        entityManager.persist(new Post("규칙안내", "내용", PostType.FREE, user1));
        entityManager.persist(new Post("안녕하세요", "내용", PostType.FREE, user2));

        entityManager.flush();
        entityManager.clear();

        // User.findByEmail
        User findByEmailResult = entityManager
                .createNamedQuery("User.findByEmail", User.class)
                .setParameter("email", "tester@adapterz.kr")
                .getSingleResult();
        System.out.println("findByEmail : " + findByEmailResult.getNickname());

        // User.searchByNickname
        List<User> searchByNicknameResult = entityManager
                .createNamedQuery("User.searchByNickname", User.class)
                .setParameter("keyword", "Tester")
                .getResultList();
        searchByNicknameResult.forEach(u -> System.out.println("searchByNickname : " + u.getEmail() + " / " + u.getNickname()));

        // Post.findByAuthorId
        List<Post> findByAuthorIdResult = entityManager
                .createNamedQuery("Post.findByAuthorId", Post.class)
                .setParameter("authorId", user2.getId())
                .getResultList();
        System.out.println("Post.findByAuthorId size = " + findByAuthorIdResult.size());
        findByAuthorIdResult.forEach(p -> System.out.println("post : " + p.getTitle()));

        // Post.titlesByAuthorNickname
        List<String> titlesByAuthorNicknameResult = entityManager
                .createNamedQuery("Post.titlesByAuthorNickname", String.class)
                .setParameter("nickname", "Master")
                .getResultList();
        titlesByAuthorNicknameResult.forEach(t -> System.out.println("titlesByAuthorNickname : " + t));

        // Post.countByAuthorId
        Long countByAuthorIdResult = entityManager
                .createNamedQuery("Post.countByAuthorId", Long.class)
                .setParameter("authorId", user1.getId())
                .getSingleResult();
        System.out.println("Post.countByAuthorId : " + countByAuthorIdResult);
    }
}