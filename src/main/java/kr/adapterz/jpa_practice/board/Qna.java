package kr.adapterz.jpa_practice.board;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Qna extends Board {

    private boolean solved;
}