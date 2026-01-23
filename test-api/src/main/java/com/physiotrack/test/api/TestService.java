package com.physiotrack.test.api;

import com.physiotrack.test.api.model.Question;
import java.util.List;

public interface TestService {
    String ping();
    int evaluate(List<String> answers);
    List<Question> getScrenningTestQuestions();
}
