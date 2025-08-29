package kr.adapterz.jpa_practice.board;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("FREE")
@Getter @Setter
public class Free extends Board {

    private String category;
}