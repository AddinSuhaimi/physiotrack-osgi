package com.physiotrack.test.impl.Repository;

import com.physiotrack.test.api.model.Test;
import com.physiotrack.test.api.model.TestType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class TestRepository {

    private final ConcurrentHashMap<Long, Test> tests = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong(1);

    public long count() {
        return tests.size();
    }

    public Test save(Test test) {
        if (test.getTestId() == null) {
            test.setTestId(seq.getAndIncrement());
        }
        tests.put(test.getTestId(), test);
        return test;
    }

    public Optional<Test> findById(Long id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(tests.get(id));
    }

    public Optional<Test> findByType(TestType type) {
        if (type == null) return Optional.empty();

        for (Test test : tests.values()) {
            if (type.equals(test.getType())) {
                return Optional.of(test);
            }
        }
        return Optional.empty();
    }

    public List<Test> findAll() {
        return new ArrayList<>(tests.values());
    }
}
