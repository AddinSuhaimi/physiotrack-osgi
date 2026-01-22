package com.physiotrack.test.impl;

import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.physiotrack.test.api.TestManageService;
import com.physiotrack.test.api.TestService;
import com.physiotrack.test.api.model.TestType;
import com.physiotrack.test.api.model.Test;
import com.physiotrack.test.api.model.Question;
import com.physiotrack.test.impl.Repository.TestRepository;
import com.physiotrack.test.impl.Repository.QuestionRepository;

public class Activator implements BundleActivator {

    private ServiceRegistration<TestService> testReg;
    private ServiceRegistration<TestManageService> manageReg;

    private TestRepository testRepository;
    private QuestionRepository questionRepository;

    @Override
    public void start(BundleContext context) throws Exception {

        // Create repository
        testRepository = new TestRepository();
        questionRepository = new QuestionRepository();

        // Optionally seed INITIAL_SCREENING test if not exists
        seedInitialScreeningTest();

        // Create service implementations
        TestService testService = new TestServiceImpl(testRepository);
        TestManageService manageService = new TestManageServiceImpl(testRepository, questionRepository);

        // Register services in OSGi
        testReg = context.registerService(TestService.class, testService, null);
        manageReg = context.registerService(TestManageService.class, manageService, null);

        System.out.println("[test-impl] TestService & TestManageService registered");
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        if (testReg != null) testReg.unregister();
        if (manageReg != null) manageReg.unregister();
        System.out.println("[test-impl] stopped");
    }

    private void seedInitialScreeningTest() {
        // Check if the test already exists
        if (testRepository.findByType(TestType.INITIAL_SCREENING).isPresent()) {
            System.out.println("[SEED] INITIAL_SCREENING test already exists, skipping.");
            return;
        }

        // Create the test
        Test initialTest = new Test();
        initialTest.setTestName("Initial Screening Test");
        initialTest.setType(TestType.INITIAL_SCREENING);
        initialTest.setScore(0);
        initialTest.setResponseList(new ArrayList<>());

        // Create questions
        List<Question> questions = new ArrayList<>();
        questions.add(new Question(
                "Do you feel the pain or discomfort on your knee? (Y/N)",
                "Knee Pain",
                "Y"
        ));
        questions.add(new Question(
                "Do you feel the pain or discomfort on your back? (Y/N)",
                "Back Pain",
                "Y"
        ));
        questions.add(new Question(
                "Do you feel the pain or discomfort on your shoulder? (Y/N)",
                "Shoulder Pain",
                "Y"
        ));
        questions.add(new Question(
                "Do you have difficulty moving or using the affected body part? (Y/N)",
                "MOBILITY",
                "Y"
        ));
        questions.add(new Question(
                "Does the pain or discomfort affect your daily activities (walking, working, sleeping)? (Y/N)",
                "DAILY_ACTIVITY",
                "Y"
        ));
        questions.add(new Question(
                "Do you have swelling, numbness, tingling, or recent injury? (Y/N)",
                "RED_FLAG",
                "Y"
        ));

        // Assign questions to test
        initialTest.setQuestionList(questions);

        // Save to repository
        questionRepository.saveQuestions(questions);
        testRepository.save(initialTest);

        System.out.println("[SEED] INITIAL_SCREENING test seeded with " + questions.size() + " questions.");
    }

}
