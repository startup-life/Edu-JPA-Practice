package kr.adapterz.jpa_practice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;


@Entity
@NamedQueries({
        // 작성자 ID로 게시글 목록
        @NamedQuery(
                name = "Post.findByAuthorId",
                query = "select p from Post p where p.author.id = :authorId"
        ),
        // 작성자 닉네임으로 제목 목록
        @NamedQuery(
                name = "Post.titlesByAuthorNickname",
                query = "select p.title from Post p where p.author.nickname = :nickname"
        ),
        // 작성자별 게시글 수 집계
        @NamedQuery(
                name = "Post.countByAuthorId",
                query = "select count(p) from Post p where p.author.id = :authorId"
        )
})
@Getter @Setter
public class Post {

    @Id @GeneratedValue
    @Column(name = "post_id")
    private Long id;

    private String title;
    private String content;

    @Enumerated(EnumType.STRING)
    private PostType postType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") // FK(post.user_id) → user.user_id
    private User author;

    @ElementCollection
    @CollectionTable(
            name = "post_tags",
            joinColumns = @JoinColumn(name = "post_id")
    )
    private Set<Tag> tags = new HashSet<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public Post() {}

    public Post(String title, String content, PostType postType, User author) {
        this.title = title;
        this.content = content;
        this.postType = postType;
        this.author = author;
    }
}