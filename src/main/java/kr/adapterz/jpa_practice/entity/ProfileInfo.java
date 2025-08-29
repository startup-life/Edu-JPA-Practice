package kr.adapterz.jpa_practice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter @Setter
public class ProfileInfo {

    private String imageUrl;
    private String introduction;

    protected ProfileInfo() {}

    public ProfileInfo(String imageUrl, String introduction) {
        this.imageUrl = imageUrl;
        this.introduction = introduction;
    }

}