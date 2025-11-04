package com.thecoffeetaster.review.infrastructure.persistence.entity;

import com.thecoffeetaster.coffee.infrastructure.adapter.out.persistence.entity.CoffeeEntity;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
public class CoffeeReview {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @ManyToOne
    private CoffeeEntity coffee;
    private String author;
    private Date date;
    private String brewMethod;
    private byte rating;
}
