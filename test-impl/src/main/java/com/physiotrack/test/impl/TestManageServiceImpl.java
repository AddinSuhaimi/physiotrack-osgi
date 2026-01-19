package com.physiotrack.test.impl;

import com.physiotrack.test.api.TestManageService;
import com.physiotrack.test.api.model.Question;
import com.physiotrack.test.api.model.Test;
import com.physiotrack.test.api.model.TestType;
import com.physiotrack.test.impl.Repository.TestRepository;
import com.physiotrack.test.impl.Repository.QuestionRepository;

import java.util.List;

public class TestManageServiceImpl implements TestManageService {

    private final TestRepository testRepository;
    private final QuestionRepository questionRepository;
    private final Test screeningTest; // Loaded once at initialization

    public TestManageServiceImpl(TestRepository testRepository, QuestionRepository questionRepository) {
        this.testRepository = testRepository;
        this.questionRepository = questionRepository;
        // Load or create INITIAL_SCREENING test
        this.screeningTest = testRepository.findByType(TestType.INITIAL_SCREENING)
                .orElseGet(() -> {
                    Test t = new Test();
                    t.setTestName("Initial Screening Test");
                    t.setType(TestType.INITIAL_SCREENING);
                    testRepository.save(t);
                    return t;
                });
    }

    @Override
    public String ping() {
        return "test module OK";
    }

    @Override
    public List<Question> getQuestionList() {
        return screeningTest.getQuestionList();
    }

    @Override
    public void addQuestion(Question question) {
        if (question == null) {
            throw new IllegalArgumentException("Question cannot be null");
        }
        questionRepository.save(question);
        screeningTest.addQuestion(question);
        testRepository.save(screeningTest);
    }

    @Override
    public void editQuestion(Question question) {
        if (question == null || question.getQuestionId() == null) {
            throw new IllegalArgumentException("Question or Question ID cannot be null");
        }
        screeningTest.updateQuestion(question);
        testRepository.save(screeningTest);
    }

    @Override
    public void removeQuestion(Question question) {
        if (question == null || question.getQuestionId() == null) {
            throw new IllegalArgumentException("Question or Question ID cannot be null");
        }
        screeningTest.deleteQuestion(question);
        testRepository.save(screeningTest);
    }
}
