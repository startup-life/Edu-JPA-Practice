package kr.adapterz.jpa_practice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NamedQueries({
        // 이메일로 단건 조회
        @NamedQuery(
                name = "User.findByEmail",
                query = "select u from User u where u.email = :email"
        ),
        // 닉네임 like 검색
        @NamedQuery(
                name = "User.searchByNickname",
                query = "select u from User u where u.nickname like concat('%', :keyword, '%')"
        )
})
@Getter @Setter
public class User {

    @Id @GeneratedValue
    @Column(name = "user_id")
    private Long id;

    private String email;
    private String password;

    @Transient
    private String passwordConfirm;

    private String nickname;

    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    @OneToMany(mappedBy = "author")
    private List<Post> posts = new ArrayList<>();

    @Embedded
    private ProfileInfo profileInfo;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    protected User() {}

    public User(String email, String password, String nickname) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
    }

    public User(String email, String password, String nickname, UserRole userRole) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.userRole = userRole;
    }

    public User(String email, String password, String passwordConfirm, String nickname, UserRole userRole) {
        this.email = email;
        this.password = password;
        this.passwordConfirm = passwordConfirm;
        this.nickname = nickname;
        this.userRole = userRole;
    }

    public User(String email, String password, String nickname, UserRole userRole, ProfileInfo profileInfo) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.userRole = userRole;
        this.profileInfo = profileInfo;
    }

    // 편의 메서드: 양쪽 동기화
    public void addPost(Post post) {
        this.posts.add(post);
        post.setAuthor(this);
    }

    public void removePost(Post post) {
        this.posts.remove(post);
        post.setAuthor(null);
    }
}