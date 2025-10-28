package com.thecoffeetaster.entity;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
public class Coffee {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
        private UUID id;
    @ManyToOne
    private BeanType beanType;
    private String origin;
    private int altitude;
    private String brand;
    private String process;
    private String tastingNotes;
    private String roaster;
    private String roastLevel;
    private String price;
    private Date date;
}
