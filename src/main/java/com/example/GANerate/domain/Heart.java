package com.example.GANerate.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Heart extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="heart_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "data_product_id")
    private DataProduct dataProduct;

    @Builder
    public Heart(Long id, User user, DataProduct dataProduct){
        this.id = id;
        this.user = user;
        this.dataProduct = dataProduct;
    }

    public void setUser(User user){
        this.user = user;
        user.getHearts().add(this);
    }

}
