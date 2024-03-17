package com.example.bookstore.controller;

import com.example.bookstore.dto.QuizCheck;
import com.example.bookstore.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;

@Controller
public class QuizController {

    @Autowired
    private QuizService quizService;

    @GetMapping("/main")
    public String main(){
        return "main";
    }

    @GetMapping("/quiz/start")
    public String quizStart(){
        Long id = quizService.start();
        return "redirect:/quiz/" + id;
    }

    @GetMapping("/quiz/{id}")
    public String quizProgress(@PathVariable Long id, Model model){
        ArrayList<Double> ratios = quizService.progress(id);
        String keyword = quizService.getKeyword(id);
        int score = quizService.getScore();
        model.addAttribute("ratios", ratios);
        model.addAttribute("keyword", keyword);
        model.addAttribute("round", score + 1);
        return "quiz";
    }

    @GetMapping("/quiz/{id}/check")
    public String quizCheck(QuizCheck quizCheck){
        if(quizService.check(quizCheck))
            return "redirect:/quiz/" + quizService.nextRound();
        else
            return "redirect:/quiz/end";
    }

    @GetMapping("/quiz/end")
    public String quizEnd(Model model){
        int score = quizService.getScore();
        model.addAttribute("score", score);
        return "end";
    }

    @GetMapping("/quiz/0")
    public String quizClear(){
        return "redirect:/quiz/end";
    }

}
