package com.physiotrack.test.api;

import com.physiotrack.test.api.model.Question;
import java.util.List;

public interface TestManageService {
    String ping();

    List<Question> getQuestionList();

    void addQuestion(Question question);

    void editQuestion(Question question);

    void removeQuestion(Question question);
}
