package com.example.exam_portal.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.exam_portal.domain.Question;
import com.example.exam_portal.repository.QuestionRepository;

@Service
public class QuestionService {
    private  final QuestionRepository questionRepository;

    public QuestionService(QuestionRepository questionRepository){
        this.questionRepository=questionRepository;
    }

    public List<Question> getQuestionsByExamId(Long id) {
        return this.questionRepository.findByExamId(id);
    }

    public Question handleSaveQuestion(Question question) {
        Question eric = this.questionRepository.save(question);
        return eric;
    }

    public Question getQuestionById(long id) {
        return this.questionRepository.findById(id);
    }

    public void deleteAQuestion(long id) {
        this.questionRepository.deleteById(id);
    }
}
