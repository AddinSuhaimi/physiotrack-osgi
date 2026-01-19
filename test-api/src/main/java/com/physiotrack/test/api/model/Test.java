package com.physiotrack.test.api.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Test {

    private Long testId;
    private String testName;
    private String testDesc;
    private LocalDateTime testTakenDateTime;
    private LocalDateTime testFinishDateTime;
    private TestType type;
    private List<Question> questionList = new ArrayList<>();
    private List<String> responseList = new ArrayList<>();
    private Integer score;

    public Test() {

    }


    public Test(
        Long testId,
        String testName,
        String testDesc,
        TestType type,
        LocalDateTime testTakenDateTime
    ) {
        this.testId = testId;
        this.testName = testName;
        this.testDesc = testDesc;
        this.type = type;
        this.testTakenDateTime = testTakenDateTime;
    }   

    public int getScore() {
        if (score == null) score = 0;
        for (int i = 0; i < questionList.size(); i++) {
            Question question = questionList.get(i);
            String correctAns = question.getQuestionAns();
            String patientAns = responseList.size() > i ? responseList.get(i) : "";
            if (correctAns != null && correctAns.equalsIgnoreCase(patientAns)) {
                score += 1;
            }
        }
        return score;
    }

    public Long getTestId() {
        return testId;
    }

    public void setTestId(Long testId) {
        this.testId = testId;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public String getTestDesc() {
        return testDesc;
    }

    public void setTestDesc(String testDesc) {
        this.testDesc = testDesc;
    }

    public LocalDateTime getTestTakenDateTime() {
        return testTakenDateTime;
    }

    public void setTestTakenDateTime(LocalDateTime testTakenDateTime) {
        this.testTakenDateTime = testTakenDateTime;
    }

    public LocalDateTime getTestFinishDateTime() {
        return testFinishDateTime;
    }

    public void setTestFinishDateTime(LocalDateTime testFinishDateTime) {
        this.testFinishDateTime = testFinishDateTime;
    }

    public TestType getType() {
        return type;
    }

    public void setType(TestType type) {
        this.type = type;
    }

    public List<Question> getQuestionList() {
        return questionList;
    }

    public void setQuestionList(List<Question> questionList) {
        this.questionList = (questionList != null) ? questionList : new ArrayList<>();
    }

    public List<String> getResponseList() {
        return responseList;
    }

    public void setResponseList(List<String> responseList) {
        this.responseList = (responseList != null) ? responseList : new ArrayList<>();
    }

    public Integer getScoreValue() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }


    // =========================
    // Helper methods for Questions
    // =========================
    public void addQuestion(Question question) {
        questionList.add(question);
    }

    public void updateQuestion(Question question) {
        if (question == null || question.getQuestionId() == null) {
            throw new IllegalArgumentException("Question or Question ID cannot be null");
        }

        // Find the existing question in this Test's questionList
        List<Question> questions = this.getQuestionList(); // assuming getQuestionList() is available

        boolean found = false;
        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            if (q.getQuestionId().equals(question.getQuestionId())) {
                // Update fields
                q.setQuestionDesc(question.getQuestionDesc());
                q.setQuestionCat(question.getQuestionCat());
                found = true;
                break;
            }
        }

        if (!found) {
            throw new RuntimeException("Question with ID " + question.getQuestionId() + " not found in this test");
        }
    }

    public void deleteQuestion(Question question) {
        questionList.remove(question);
    }
}
