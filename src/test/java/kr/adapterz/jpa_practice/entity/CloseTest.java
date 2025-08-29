package kr.adapterz.jpa_practice.entity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class CloseTest {

    @Autowired
    EntityManagerFactory entityManagerFactory;

    @Test
    @Rollback(false)
    void closeTest() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        User user = new User("tester@adapterz.kr", "123aS!", "Adapterz");
        entityManager.getTransaction().begin();
        entityManager.persist(user);
        entityManager.getTransaction().commit();

        entityManager.close();
        System.out.println("=== EntityManager close() 호출됨 ===");


        try {
            System.out.println("=== close 이후 find() 호출 ===");
            entityManager.find(User.class, user.getId());
        } catch(Exception e) {
            System.out.println("예외 발생 : " + e.getClass().getSimpleName());
        }
    }
}