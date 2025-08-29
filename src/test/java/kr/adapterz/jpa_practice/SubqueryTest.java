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
public class SubqueryTest {

    @PersistenceContext
    EntityManager entityManager;

    /**
     * WHERE 절 서브쿼리: 글이 하나라도 있는 사용자만
     * 표준 JPQL: where exists (subquery)
     */
    @Test
    @Rollback(false)
    void whereExistSubqueryTest() {
        // 3개의 유저 더미 데이터 추가
        User user1 = new User("tester1@adapterz.kr", "pw", "tester1", UserRole.ADMIN);
        User user2 = new User("tester2@adapterz.kr", "pw", "tester2", UserRole.ADMIN);
        User user3 = new User("tester3@adapterz.kr", "pw", "tester3", UserRole.ADMIN);
        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.persist(user3);

        // 3개의 더미 게시글 데이터 추가
        entityManager.persist(new Post("제목1", "내용1", PostType.NOTICE, user1));
        entityManager.persist(new Post("제목2", "내용2", PostType.FREE, user1));
        entityManager.persist(new Post("제목3", "내용3", PostType.FREE, user2));

        entityManager.flush();
        entityManager.clear();

        List<User> findAuthor = entityManager.createQuery(
                        "select u from User u " +
                                "where exists (select p.id from Post p where p.author = u)", User.class)
                .getResultList();

        System.out.println("글이 있는 사용자:");
        findAuthor.forEach(u -> System.out.println(u.getNickname()));
    }

    /**
     * WHERE 절 서브쿼리: 글 수가 2건 이상인 사용자
     * 표준 JPQL: where (select count(...)) >= :n
     */
    @Test
    @Rollback(false)
    void where_count_subquery() {
        // 3개의 유저 더미 데이터 추가
        User user1 = new User("tester1@adapterz.kr", "pw", "tester1", UserRole.ADMIN);
        User user2 = new User("tester2@adapterz.kr", "pw", "tester2", UserRole.ADMIN);
        User user3 = new User("tester3@adapterz.kr", "pw", "tester3", UserRole.ADMIN);
        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.persist(user3);

        // 3개의 더미 게시글 데이터 추가
        entityManager.persist(new Post("제목1", "내용1", PostType.NOTICE, user1));
        entityManager.persist(new Post("제목2", "내용2", PostType.FREE, user1));
        entityManager.persist(new Post("제목3", "내용3", PostType.FREE, user2));

        entityManager.flush();
        entityManager.clear();

        List<User> findAuthor = entityManager.createQuery(
                        "select u from User u " +
                                "where (select count(p) from Post p where p.author = u) >= :min", User.class)
                .setParameter("min", 2L)
                .getResultList();

        System.out.println("게시글 2건 이상 사용자:");
        findAuthor.forEach(u -> System.out.println(u.getNickname()));
    }

    /**
     * HAVING 절 서브쿼리: 사용자별 게시글 수가 전체 사용자 평균 이상인 사용자
     * 표준 JPQL: group by + having (subquery)
     */
    @Test
    @Rollback(false)
    void havingSubqueryTest() {
        // 3개의 유저 더미 데이터 추가
        User user1 = new User("tester1@adapterz.kr", "pw", "tester1", UserRole.ADMIN);
        User user2 = new User("tester2@adapterz.kr", "pw", "tester2", UserRole.ADMIN);
        User user3 = new User("tester3@adapterz.kr", "pw", "tester3", UserRole.ADMIN);
        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.persist(user3);

        // 3개의 더미 게시글 데이터 추가
        entityManager.persist(new Post("제목1", "내용1", PostType.NOTICE, user1));
        entityManager.persist(new Post("제목2", "내용2", PostType.FREE, user1));
        entityManager.persist(new Post("제목3", "내용3", PostType.FREE, user2));

        entityManager.flush();
        entityManager.clear();

        List<Object[]> findAuthor = entityManager.createQuery(
                        "select u.nickname, count(p) " +
                                "from User u left join u.posts p " +
                                "group by u.id, u.nickname " +
                                "having count(p) >= (" +
                                "  select avg( (select count(p2) from Post p2 where p2.author = u2) ) " +
                                "  from User u2" +
                                ")", Object[].class)
                .getResultList();

        System.out.println("평균 이상 작성자:");
        findAuthor.forEach(r -> System.out.println(r[0] + " / posts=" + r[1]));
    }

    /**
     * SELECT 절 서브쿼리 (구현체 확장: Hibernate HQL에서 지원)
     * 각 사용자별 게시글 수를 함께 프로젝션
     * 이식성 관점에서 필요 시 조인/그룹바이로 대체 권장
     */
    @Test
    @Rollback(false)
    void selectSubqueryTest() {
        // 3개의 유저 더미 데이터 추가
        User user1 = new User("tester1@adapterz.kr", "pw", "tester1", UserRole.ADMIN);
        User user2 = new User("tester2@adapterz.kr", "pw", "tester2", UserRole.ADMIN);
        User user3 = new User("tester3@adapterz.kr", "pw", "tester3", UserRole.ADMIN);
        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.persist(user3);

        // 3개의 더미 게시글 데이터 추가
        entityManager.persist(new Post("제목1", "내용1", PostType.NOTICE, user1));
        entityManager.persist(new Post("제목2", "내용2", PostType.FREE, user1));
        entityManager.persist(new Post("제목3", "내용3", PostType.FREE, user2));

        entityManager.flush();
        entityManager.clear();

        List<Object[]> findPostData = entityManager.createQuery(
                        "select u.nickname, " +
                                "       (select count(p) from Post p where p.author = u) " +
                                "from User u order by u.id", Object[].class)
                .getResultList();

        System.out.println("사용자 / 게시글수:");
        findPostData.forEach(r -> System.out.println(r[0] + " / " + r[1]));
    }

    @Test
    @Rollback(false)
    void fromSubqueryTest() {
        // 3개의 유저 더미 데이터 추가
        User user1 = new User("tester1@adapterz.kr", "pw", "tester1", UserRole.ADMIN);
        User user2 = new User("tester2@adapterz.kr", "pw", "tester2", UserRole.ADMIN);
        User user3 = new User("tester3@adapterz.kr", "pw", "tester3", UserRole.ADMIN);
        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.persist(user3);

        // 3개의 더미 게시글 데이터 추가
        entityManager.persist(new Post("제목1", "내용1", PostType.NOTICE, user1));
        entityManager.persist(new Post("제목2", "내용2", PostType.FREE, user1));
        entityManager.persist(new Post("제목3", "내용3", PostType.FREE, user2));

        entityManager.flush();
        entityManager.clear();

        List<String> findAuthorWithSubquery = entityManager.createQuery(
                        "select u.nickname " +
                                "from (select distinct p.author as a from Post p) as t " +
                                "join User u on u = t.a", String.class)
                .getResultList();

        // "게시글을 하나라도 작성한 사용자 닉네임" 조회 (조인 버전)
        List<String> findAuthorWithJoin = entityManager.createQuery(
                    "select distinct u.nickname " +
                            "from User u join u.posts p", String.class)
            .getResultList();

        System.out.println("파생테이블로 뽑은 작성자 닉네임:");
        findAuthorWithSubquery.forEach(System.out::println);

        System.out.println("조인으로 뽑은 작성자 닉네임:");
        findAuthorWithJoin.forEach(System.out::println);
    }
}
