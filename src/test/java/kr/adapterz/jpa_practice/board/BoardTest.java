package kr.adapterz.jpa_practice.board;

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
class BoardTest {

    @PersistenceContext
    EntityManager entityManager;

    @Test
    @Rollback(false)
    void selectAllBoardTest() {
        Notice noticePost = new Notice();
        noticePost.setTitle("공지사항");
        noticePost.setContent("공지사항 내용");
        noticePost.setNoticeLevel("긴급");
        entityManager.persist(noticePost);

        Free freePost = new Free();
        freePost.setTitle("자유게시판");
        freePost.setContent("자유게시판 내용");
        freePost.setCategory("일반");
        entityManager.persist(freePost);

        Qna qnaPost = new Qna();
        qnaPost.setTitle("질문게시판");
        qnaPost.setContent("질문게시판 내용");
        qnaPost.setSolved(false);
        entityManager.persist(qnaPost);

        entityManager.flush();
        entityManager.clear();

        // 부모 타입으로 조회하면 자식 모두 포함
        List<Board> result = entityManager
                .createQuery("select b from Board b", Board.class)
                .getResultList();

        System.out.println("총 개수 = " + result.size());
        for (Board b : result) {
            System.out.println("id=" + b.getId() + ", title=" + b.getTitle() + ", class=" + b.getClass().getSimpleName());
        }
    }

    @Test
    @Rollback(false)
    void selectNoticeWithTYPETest() {
        Notice noticePost = new Notice();
        noticePost.setTitle("공지사항");
        noticePost.setContent("공지사항 내용");
        noticePost.setNoticeLevel("긴급");
        entityManager.persist(noticePost);

        Free freePost = new Free();
        freePost.setTitle("자유게시판");
        freePost.setContent("자유게시판 내용");
        freePost.setCategory("일반");
        entityManager.persist(freePost);

        Qna qnaPost = new Qna();
        qnaPost.setTitle("질문게시판");
        qnaPost.setContent("질문게시판 내용");
        qnaPost.setSolved(false);
        entityManager.persist(qnaPost);

        entityManager.flush();
        entityManager.clear();

        // 특정 자식 타입만 선별: TYPE(b) = Notice
        List<Board> result = entityManager
                .createQuery("select b from Board b where type(b) = Notice", Board.class)
                .getResultList();

        System.out.println("공지 개수 = " + result.size());
        result.forEach(b -> System.out.println("Notice: id=" + b.getId() + ", title=" + b.getTitle()));
    }

    @Test
    @Rollback(false)
    void selectSeveralTYPETest() {
        Notice noticePost = new Notice();
        noticePost.setTitle("공지사항");
        noticePost.setContent("공지사항 내용");
        noticePost.setNoticeLevel("긴급");
        entityManager.persist(noticePost);

        Free freePost = new Free();
        freePost.setTitle("자유게시판");
        freePost.setContent("자유게시판 내용");
        freePost.setCategory("일반");
        entityManager.persist(freePost);

        Qna qnaPost = new Qna();
        qnaPost.setTitle("질문게시판");
        qnaPost.setContent("질문게시판 내용");
        qnaPost.setSolved(false);
        entityManager.persist(qnaPost);

        entityManager.flush();
        entityManager.clear();

        // 여러 자식 타입 선택: TYPE(b) in (Notice, Free)
        List<Board> result = entityManager
                .createQuery("select b from Board b where type(b) in (Notice, Free)", Board.class)
                .getResultList();

        System.out.println("Notice+Free 개수 = " + result.size());
        result.forEach(b -> System.out.println("type=" + b.getClass().getSimpleName() + ", title=" + b.getTitle()));
    }

    @Test
    @Rollback(false)
    void selectNoticeLevelWithTREATTest() {
        Notice noticePost = new Notice();
        noticePost.setTitle("공지사항");
        noticePost.setContent("공지사항 내용");
        noticePost.setNoticeLevel("긴급");
        entityManager.persist(noticePost);

        Free freePost = new Free();
        freePost.setTitle("자유게시판");
        freePost.setContent("자유게시판 내용");
        freePost.setCategory("일반");
        entityManager.persist(freePost);

        Qna qnaPost = new Qna();
        qnaPost.setTitle("질문게시판");
        qnaPost.setContent("질문게시판 내용");
        qnaPost.setSolved(false);
        entityManager.persist(qnaPost);

        entityManager.flush();
        entityManager.clear();

        // Notice.noticeLevel만 추출
        List<String> levels = entityManager
                .createQuery(
                        "select treat(b as Notice).noticeLevel from Board b where type(b) = Notice",
                        String.class)
                .getResultList();

        System.out.println("noticeLevel 목록 = " + levels);
    }
}