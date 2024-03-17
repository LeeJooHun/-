package com.example.quiz.dto;

import lombok.AllArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@ToString
public class QuizForm {
    private Long id;
    private String keyword;
    private String category;
}
