package com.physiotrack.test.impl.Repository;

import com.physiotrack.test.api.model.Question;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class QuestionRepository {

    private final ConcurrentHashMap<Long, Question> questions = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong(1);

    public long count() {
        return questions.size();
    }

    public Question save(Question question) {
        if (question.getQuestionId() == null) {
            question.setQuestionId(seq.getAndIncrement());
        }
        questions.put(question.getQuestionId(), question);
        return question;
    }

    public List<Question> saveQuestions(List<Question> questionList) {
        for(Question question : questionList){
            if (question.getQuestionId() == null) {
                question.setQuestionId(seq.getAndIncrement());
            }
            questions.put(question.getQuestionId(), question);
        }

        return questionList;
    }

    public Optional<Question> findById(Long id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(questions.get(id));
    }

    public List<Question> findAll() {
        return new ArrayList<>(questions.values());
    }
}
