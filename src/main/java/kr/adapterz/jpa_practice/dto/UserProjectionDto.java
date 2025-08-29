package kr.adapterz.jpa_practice.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter @Setter
public class UserProjectionDto {

    private final String email;
    private final String nickname;

    public UserProjectionDto(String email, String nickname) {
        this.email = email;
        this.nickname = nickname;
    }
}