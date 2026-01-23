package com.physiotrack.test.impl;

import com.physiotrack.test.api.TestService;
import com.physiotrack.test.api.model.Question;
import com.physiotrack.test.api.model.Test;
import com.physiotrack.test.api.model.TestType;
import com.physiotrack.test.impl.Repository.TestRepository;


import java.util.List;

public class TestServiceImpl implements TestService {

    public final TestRepository testRepository;
    public final Test screenningTest;

    public TestServiceImpl(TestRepository testRepository) {
        this.testRepository = testRepository;
        this.screenningTest = testRepository.findByType(TestType.INITIAL_SCREENING).orElseThrow(()->
                                new RuntimeException("Initial screening test not found"));
    }

    @Override
    public String ping() {
        return "test module OK";
    }

    @Override
    public int evaluate(List<String> answers) {
        int score = 0;
        List<Question> questionList = screenningTest.getQuestionList();
        for (int i = 0; i < questionList.size(); i++) {
            Question question = questionList.get(i);
            String correctAns = question.getQuestionAns();
            String patientAns = answers.size() > i ? answers.get(i) : "";
            if (correctAns != null && correctAns.equalsIgnoreCase(patientAns)) {
                score += 1;
            }
        }
        return score;
    }

    @Override
    public List<Question> getScrenningTestQuestions() {
        return screenningTest.getQuestionList();
    }
}
