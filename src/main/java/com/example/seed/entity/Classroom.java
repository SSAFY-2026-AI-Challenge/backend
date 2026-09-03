package com.example.seed.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "classroom")
public class Classroom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false, unique = true, length = 45)
    private String name;

    protected Classroom() {
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}