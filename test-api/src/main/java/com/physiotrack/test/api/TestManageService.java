package com.physiotrack.test.api;

import com.physiotrack.test.api.model.Question;
import java.util.List;

public interface TestManageService {
    String ping();
    List<Question> getQuestionList();
    Question addQuestion(String questionDesc, String questionCat, String questionAns);
    Question editQuestion(Question question);
    void removeQuestion(Question question);
}
