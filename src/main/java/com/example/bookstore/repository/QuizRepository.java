package com.example.bookstore.repository;

import com.example.bookstore.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.ArrayList;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
    @Override
    ArrayList<Quiz> findAll();
    @Query(value = "SELECT id FROM jdbc.quiz WHERE id = (SELECT MAX(id) FROM jdbc.quiz)", nativeQuery = true)
    Long findMaxId();
}
