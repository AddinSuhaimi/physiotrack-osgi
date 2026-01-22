package com.physiotrack.test.api.model;

public class Question {

    private Long questionId;
    private String questionDesc;
    private String questionCat; // General, Upper, Lower, Daily
    private String questionAns;

    public Question(String questionDesc, String questionCat, String questionAns) {
        this.questionDesc = questionDesc;
        this.questionCat = questionCat;
        this.questionAns = questionAns;
    }

    public Question() {
        this("", "", "");
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }
    
    public String getQuestionDesc() {
        return questionDesc;
    }

    public void setQuestionDesc(String questionDesc) {
        this.questionDesc = questionDesc;
    }

    public String getQuestionAns() {
        return questionAns;
    }

    public void setQuestionAns(String questionAns) {
        this.questionAns = questionAns;
    }

    public String getQuestionCat() {
        return questionCat;
    }

    public void setQuestionCat(String questionCat) {
        this.questionCat = questionCat;
    }
}
