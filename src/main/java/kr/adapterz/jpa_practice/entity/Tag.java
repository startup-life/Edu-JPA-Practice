package kr.adapterz.jpa_practice.entity;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter @Setter
public class Tag {

    private String name;

    protected Tag() {}

    public Tag(String name) {
        this.name = name;
    }
}