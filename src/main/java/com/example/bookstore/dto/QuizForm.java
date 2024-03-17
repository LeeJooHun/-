package com.example.bookstore.dto;

import com.example.bookstore.entity.Quiz;
import lombok.AllArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@ToString
public class QuizForm {
    private Long id;
    private String keyword;
    private String category;
}
