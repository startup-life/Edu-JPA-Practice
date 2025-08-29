package kr.adapterz.jpa_practice.board;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("NOTICE")
@Getter @Setter
public class Notice extends Board {

    private String noticeLevel;
}