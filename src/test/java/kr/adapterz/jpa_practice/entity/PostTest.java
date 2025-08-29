package kr.adapterz.jpa_practice.entity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Transactional
class PostTest {
    @PersistenceContext
    EntityManager entityManager;

    @Test
    @Rollback(false)
    void unidirectionalManyToOneTest() {
        // 유저 저장
        User user = new User("tester@adapterz.kr", "123aS!", "tester", UserRole.ADMIN);
        entityManager.persist(user);
        entityManager.flush();

        // 게시글 저장
        Post post = new Post("공지 글", "내용", PostType.NOTICE, user);
        entityManager.persist(post);
        entityManager.flush();

        // 1차 캐시 초기화 후 조회
        entityManager.clear();
        Post findPost = entityManager.find(Post.class, post.getId());
        System.out.println("findPost.getId() : " + findPost.getId());
        System.out.println("findPost.getTitle() : " + findPost.getTitle());
        System.out.println("findPost.getauthor().getNickname() : " + findPost.getAuthor().getNickname());
    }

    @Test
    @Rollback(false)
    void elementCollectionTest() {
        // 작성자 생성 및 저장
        User user = new User("tester@adapterz.kr", "123aS!", "tester", UserRole.ADMIN);
        entityManager.persist(user);

        // 게시글 생성
        Post post = new Post("태그 실습", "내용", PostType.FREE, user);

        // 태그 추가
        post.getTags().add(new Tag("Adaterz"));
        post.getTags().add(new Tag("Startupcode"));
        post.getTags().add(new Tag("Infinitybrain"));

        entityManager.persist(post);

        entityManager.flush();
        entityManager.clear();

        Post findPost = entityManager.find(Post.class, post.getId());
        System.out.println("게시글 제목 = " + findPost.getTitle());
        System.out.println("태그 개수 = " + findPost.getTags().size());
        findPost.getTags().forEach(tag ->
                System.out.println("태그 이름 = " + tag.getName())
        );
    }

    @Test
    @Rollback(false)
    void implicitJoinTest() {
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

        List<String> result = entityManager.createQuery(
                "select p.author.nickname from Post p",
                String.class
        ).getResultList();

        System.out.println("닉네임 목록 = " + result);
    }

    @Test
    @Rollback(false)
    void explicitJoinTest() {
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

        List<Post> result = entityManager.createQuery(
                        "select p from User u join u.posts p where u.nickname = :nickname",
                        Post.class
                ).setParameter("nickname", "Adapterz1")
                .getResultList();

        System.out.println("Adapterz1의 게시글 수(명시적 조인) = " + result.size());
    }
}