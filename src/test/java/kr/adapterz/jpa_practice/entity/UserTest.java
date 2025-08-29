package kr.adapterz.jpa_practice.entity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserTest {

    @PersistenceContext
    EntityManager entityManager;

    @Test
    @Rollback(false)
    void idTest() {
        User user = new User("tester@adapterz.kr", "123aS!", "Adapterz");
        // entityManager.persist(user);
    }

    @Test
    @Rollback(false)
    void idStrategyTest(){
        // 5개의 더미데이터 추가
        for (int i = 1; i <= 5; i++) {
            User user = new User(
                    "tester" + i + "@adapterz.kr",
                    "123aS!" + i,
                    "Adapterz" + i
            );
            entityManager.persist(user);
        }
    }

    @Test
    @Rollback(false)
    void createdUpdatedAtTest() {
        User user = new User("tester@adapterz.kr", "123aS!", "Adapterz");
        LocalDateTime now = LocalDateTime.now();

        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        entityManager.persist(user);
    }

    @Test
    @Rollback(false)
    void enumeratedTest() {
        User user = new User("tester@adapterz.kr", "123aS!", "Adapterz", UserRole.ADMIN);
        entityManager.persist(user);
    }

    @Test
    @Rollback(false)
    void transientTest() {
        User user = new User("tester@adapterz.kr", "123aS!", "123aS!", "Adapterz", UserRole.ADMIN);
        entityManager.persist(user);
    }

    @Test
    @Rollback(false)
    void flushTest() {
        User user = new User("tester@adapterz.kr", "123aS!", "Adapterz", UserRole.ADMIN);

        System.out.println("=== Flush (아무것도 없음) ===");
        entityManager.flush(); // 아무 것도 없음 (정상)
        System.out.println("==============");

        System.out.println("=== Persist ===");
        entityManager.persist(user);     // 영속화 (아직 INSERT 미발행)
        System.out.println("==============");

        System.out.println("=== Flush (INSERT 발생) ===");
        entityManager.flush();           // 여기서 INSERT 발생
        System.out.println("==============");
    }

    @Test
    @Rollback(false)
    void removeTest() {
        User user = new User("delete@adapterz.kr", "123aS!", "DeleteUser", UserRole.USER);
        entityManager.persist(user);

        entityManager.flush(); // INSERT 실행
        System.out.println("=== INSERT 쿼리 실행됨 ===");

        entityManager.remove(user);
        System.out.println("=== remove 호출 (아직 DELETE 쿼리 안 나감) ===");

        entityManager.flush();
        System.out.println("=== DELETE 쿼리 실행됨 ===");
    }

    @Test
    @Rollback(false)
    void clearTest() {
        User user = new User("clear@adapterz.kr", "123aS!", "ClearUser", UserRole.USER);

        System.out.println("=== Persist ===");
        entityManager.persist(user); // 영속화 (아직 INSERT 미발행)
        System.out.println("==============");

        System.out.println("=== Flush (INSERT 발생) ===");
        entityManager.flush(); // INSERT 실행
        System.out.println("==============");

        System.out.println("=== Clear (영속성 컨텍스트 초기화) ===");
        entityManager.clear(); // 영속성 컨텍스트 비움 → user는 준영속 상태
        System.out.println("==============");

        System.out.println("=== 1차 캐시 확인용 find() 호출 ===");
        User found = entityManager.find(User.class, user.getId());
        System.out.println("조회된 사용자 닉네임: " + found.getNickname());
        System.out.println("==============");
    }

    @Test
    @Rollback(false)
    void detachTest() {
        for (int i = 1; i <= 5; i++) {
            User user = new User(
                    "tester" + i + "@adapterz.kr",
                    "123aS!" + i,
                    "Adapterz" + i
            );
            entityManager.persist(user);
        }
        entityManager.flush();
        System.out.println("=== INSERT 5명 실행됨 ===");

        // 두 번째 유저 detach
        User detachUser = entityManager.find(User.class, 2L);
        entityManager.detach(detachUser);
        System.out.println("=== 두 번째 유저 detach 됨 ===");

        // detach 된 유저 정보 수정
        detachUser.setNickname("ChangedNickname");
        entityManager.flush();
        System.out.println("=== flush 호출됨 ===");

        // DB에서 확인
        User findUser = entityManager.find(User.class, detachUser.getId());
        System.out.println("findUser.getNickname() : " + findUser.getNickname());
        // 여전히 "Adapterz2" (ChangeNickname 반영 안 됨)
    }

    @Test
    @Rollback(false)
    void mergeAfterDetachUpdatesDB() {
        // 저장
        User user = new User("tester@adapterz.kr", "123aS!", "BeforeMerge", UserRole.USER);
        entityManager.persist(user);
        entityManager.flush();
        entityManager.clear(); // 영속성 컨텍스트 비우기

        // 준영속 엔티티 수정
        user.setNickname("AfterMerge");
        System.out.println("=== flush, clear 후 엔티티 수정 (DB 반영 X) ===");

        // merge 실행
        User managed = entityManager.merge(user);
        System.out.println("=== merge 실행 → 새로운 영속 엔티티 반환 ===");

        // 두 객체 비교
        System.out.println("user 객체 hashCode = " + System.identityHashCode(user));
        System.out.println("managed 객체 hashCode = " + System.identityHashCode(managed));
        System.out.println("user == managed : " + (user == managed));

        // flush로 DB 반영
        entityManager.flush();
        System.out.println("=== flush 실행 → UPDATE 쿼리 발생 ===");

        // 확인
        User findUser = entityManager.find(User.class, managed.getId());
        System.out.println("nickname = " + findUser.getNickname()); // AfterMerge
    }

    @Test
    @Rollback(false)
    void mergeOverWritesNulls() {
        // 유저 저장
        User user = new User("tester@adapterz.kr", "123aS!", "Adapterz", UserRole.USER);
        entityManager.persist(user);
        entityManager.flush();
        entityManager.clear();

        // 일부 필드만 세팅한 준영속 객체 생성
        User detachUser = new User();
        detachUser.setId(user.getId()); // 동일 ID
        detachUser.setEmail("detach@adapterz.kr"); // email만 세팅, 나머지는 null

        // merge 실행
        User managed = entityManager.merge(detachUser);
        System.out.println("=== merge 실행: email은 'detach@adapterz.kr', 나머지 필드는 null로 덮일 수 있음 ===");

        // flush
        entityManager.flush();
        System.out.println("=== flush 실행 → UPDATE 발생 ===");

        // DB 조회
        User findUser = entityManager.find(User.class, managed.getId());
        System.out.println("DB email = " + findUser.getEmail());   // detach@adapterz.kr
        System.out.println("DB nickname = " + findUser.getNickname()); // null 로 덮였을 수 있음
    }

    @Test
    @Rollback(false)
    void embeddedTypeTest() {

        // 값 타입(ProfileInfo) 생성
        ProfileInfo profileInfo = new ProfileInfo(
                "http://example.com/image.png",
                "안녕하세요, 자기소개입니다."
        );

        // User 엔티티 생성 시 ProfileInfo 주입
        User user = new User(
                "tester@adapterz.kr",
                "123aS!",
                "tester",
                UserRole.ADMIN,
                profileInfo
        );
        entityManager.persist(user);

        entityManager.flush();
        entityManager.clear();

        User findUser = entityManager.find(User.class, user.getId());
        System.out.println("닉네임 = " + findUser.getNickname());
        System.out.println("프로필 이미지 = " + findUser.getProfileInfo().getImageUrl());
        System.out.println("자기소개 = " + findUser.getProfileInfo().getIntroduction());
    }

    @Test
    @Rollback(false)
    void bulkUpdateTest() {
        // 100개의 유저 더미 데이터 추가
        for (int i = 1; i <= 100; i++) {
            User user = new User(
                    "tester" + i + "@adapterz.kr",
                    "123aS!" + i,
                    "Adapterz" + i,
                    UserRole.USER
            );
            entityManager.persist(user);
        }
        entityManager.flush();
        entityManager.clear();

        // 벌크 연산 실행 (모든 유저 닉네임을 'Bulked' 로)
        int bulkUpdated = entityManager.createQuery(
                        "update User u set u.nickname = :nickname")
                .setParameter("nickname", "Bulked")
                .executeUpdate();
        System.out.println("bulkUpdated rows = " + bulkUpdated);

        // flush/clear 로 영속성 컨텍스트 초기화 (중요!)
        entityManager.flush();
        entityManager.clear();

        // 다시 조회 → DB의 결과가 그대로 보인다
        List<User> findAllUsers = entityManager.createQuery(
                        "select u from User u order by u.id", User.class)
                .setMaxResults(5)
                .getResultList();

        System.out.println("after bulk nicknames:");
        findAllUsers.forEach(u -> System.out.println("id=" + u.getId() + ", nickname=" + u.getNickname()));
    }

    @Test
    @Rollback(false)
    void bulkUpdateWithoutClear() {
        // 100개의 유저 더미 데이터 추가
        for (int i = 1; i <= 100; i++) {
            User user = new User(
                    "tester" + i + "@adapterz.kr",
                    "123aS!" + i,
                    "Adapterz" + i,
                    UserRole.USER
            );
            entityManager.persist(user);
        }
        entityManager.flush();
        entityManager.clear();

        // 일부 유저를 먼저 로딩해 영속성 컨텍스트에 올려둔다
        List<User> loadedUsers = entityManager.createQuery(
                        "select u from User u order by u.id", User.class)
                .setMaxResults(5)
                .getResultList();
        System.out.println("before bulk (loadedUsers):");
        loadedUsers.forEach(u -> System.out.println("id=" + u.getId() + ", nickname=" + u.getNickname()));
        // 현재 영속성 컨텍스트에는 Adapterz1, Adapterz2, Adapterz3 같은 값이 올라가 있음

        // 벌크 UPDATE (DB 직접 변경, 영속성 컨텍스트는 모름)
        int updatedUsers = entityManager.createQuery(
                        "update User u set u.nickname = :nickname")
                .setParameter("nickname", "Bulked")
                .executeUpdate();
        System.out.println("bulk updatedUsers rows = " + updatedUsers);

        // flush/clear를 생략하고 바로 조회
        // 이미 1차 캐시에 올라간 엔티티는 캐시(이전값)로 남아있어 변경이 반영되지 않는다
        List<User> findUsers = entityManager.createQuery(
                        "select u from User u where u.id in (:ids) order by u.id", User.class)
                .setParameter("ids", List.of(loadedUsers.get(0).getId(), loadedUsers.get(1).getId(), loadedUsers.get(2).getId()))
                .getResultList();

        System.out.println("after bulk without clear nicknames:");
        findUsers.forEach(u -> System.out.println("id=" + u.getId() + ", nickname=" + u.getNickname()));
        // 여기서는 여전히 Adapterz1/2/3 출력 (영속성 컨텍스트에 남아있는 스냅샷)
        // 반면, 아직 로딩하지 않았던 다른 레코드를 새로 조회하면 DB의 'Bulked' 로 보임
    }
}