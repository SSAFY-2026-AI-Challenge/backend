package com.example.seed.repository;

import com.example.seed.entity.Classroom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClassroomRepository extends JpaRepository<Classroom, Integer> {
}