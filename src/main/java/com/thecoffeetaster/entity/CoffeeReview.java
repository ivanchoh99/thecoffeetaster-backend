package com.thecoffeetaster.entity;

import jakarta.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
public class CoffeeReview {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @ManyToOne
    private Coffee coffee;
    private String author;
    private Date date;
    private String brewMethod;
    private byte rating;
}
