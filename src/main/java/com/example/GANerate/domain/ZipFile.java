package com.example.GANerate.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ZipFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "zipfile_id")
    private Long id;

    @NotNull
    private String title;

    @NotNull
    private int size;

    @NotNull
    private String zipFileUrl;

    @Builder
    public ZipFile(String title, int size, String zipFileUrl){
        this.title=title;
        this.size=size;
        this.zipFileUrl=zipFileUrl;
    }

}
