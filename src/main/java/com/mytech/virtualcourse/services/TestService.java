package com.mytech.virtualcourse.services;

import com.mytech.virtualcourse.dtos.*;
import com.mytech.virtualcourse.entities.*;
import com.mytech.virtualcourse.enums.QuestionType;
import com.mytech.virtualcourse.exceptions.ResourceNotFoundException;
import com.mytech.virtualcourse.mappers.QuestionMapper;
import com.mytech.virtualcourse.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.mytech.virtualcourse.enums.StatusTest;
import com.mytech.virtualcourse.mappers.TestMapper;
import com.mytech.virtualcourse.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class TestService {
    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private InstructorRepository instructorRepository;

    @Autowired
    private TestMapper testMapper;

    @Autowired
    private SecurityUtils securityUtils;

    @Autowired
    private TestRepository testRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private StudentAnswerRepository studentAnswerRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private StudentTestSubmissionRepository submissionRepository;

    @Autowired
    private AnswerOptionRepository answerOptionRepository;

        public List<TestDTO> getTestsByCourse(Long courseId) {
                Course course = courseRepository.findById(courseId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found with ID: " + courseId));

                Long loggedInInstructorId = getLoggedInInstructorId();
                if (!course.getInstructor().getId().equals(loggedInInstructorId)) {
                        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to view tests for this course.");
                }

                List<Test> tests = testRepository.findByCourseId(courseId);
                return tests.stream()
                        .map(testMapper::testToTestDTO)
                        .collect(Collectors.toList());
        }

        public List<TestDTO> getTestsByInstructorIdAndCourseId(Long instructorId, Long courseId) {
            List<Test> tests = testRepository.findByInstructorIdAndCourseId(instructorId, courseId);

            return tests.stream()
                    .map(testMapper::testToTestDTO)
                    .collect(Collectors.toList());
        }

        public TestDTO createTestForCourse(Long courseId, TestDTO testDTO, Long InstructorId) {
                // Lấy khóa học
                Course course = courseRepository.findById(courseId)
                        .orElseThrow(() -> new IllegalArgumentException("Course not found with ID: " + courseId));

                // Xác thực giảng viên có quyền
                if (!course.getInstructor().getId().equals(InstructorId)) {
                        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to manage this course.");
                }

                Test test = testMapper.testDTOToTest(testDTO);
                test.setCourse(course);
                test.setInstructor(course.getInstructor());

                if (test.getStatusTest() == null) {
                        test.setStatusTest(StatusTest.INACTIVE);
                }

                if (testDTO.getQuestions() != null && !testDTO.getQuestions().isEmpty()) {
                        List<Question> questions = questionRepository.findAllById(
                                testDTO.getQuestions().stream().map(QuestionDTO::getId).collect(Collectors.toList())
                        );
                        boolean allQuestionsBelongToCourse = questions.stream()
                                .allMatch(q -> q.getCourse().getId().equals(courseId));
                        if (!allQuestionsBelongToCourse) {
                                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Some questions do not belong to the course.");
                        }
                        test.setQuestions(questions);
                } else {
                        test.setQuestions(new ArrayList<>());
                }

                testRepository.save(test);

                return testMapper.testToTestDTO(test);
        }

        public TestDTO updateTest(Long id, TestDTO updatedTestDTO, Long loggedInInstructorId) {
                Test existingTest = testRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Test not found with ID: " + id));

                if (!existingTest.getCourse().getInstructor().getId().equals(loggedInInstructorId)) {
                        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to update this test.");
                }

                existingTest.setTitle(updatedTestDTO.getTitle());
                existingTest.setDescription(updatedTestDTO.getDescription());
                existingTest.setTotalMarks(updatedTestDTO.getTotalMarks());
                existingTest.setPassPercentage(updatedTestDTO.getPassPercentage());
                existingTest.setDuration(updatedTestDTO.getDuration());
                existingTest.setIsFinalTest(updatedTestDTO.getIsFinalTest());
                existingTest.setStatusTest(updatedTestDTO.getStatusTest());

                if (updatedTestDTO.getQuestions() != null && !updatedTestDTO.getQuestions().isEmpty()) {
                        List<Question> questions = questionRepository.findAllById(
                                updatedTestDTO.getQuestions().stream().map(QuestionDTO::getId).collect(Collectors.toList())
                        );

                        boolean allQuestionsBelongToCourse = questions.stream()
                                .allMatch(q -> q.getCourse().getId().equals(existingTest.getCourse().getId()));
                        if (!allQuestionsBelongToCourse) {
                                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Some questions do not belong to the course.");
                        }

                        existingTest.setQuestions(questions);
                } else {
                        existingTest.setQuestions(new ArrayList<>());
                }

                Test savedTest = testRepository.save(existingTest);
                return testMapper.testToTestDTO(savedTest);
        }


        public void deleteTest(Long id, Long loggedInInstructorId) {
                Test test = testRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Test not found with ID: " + id));

                if (!test.getCourse().getInstructor().getId().equals(loggedInInstructorId)) {
                        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to delete this test.");
                }

                testRepository.delete(test);
        }

        public Long getLoggedInInstructorId() {
                Long accountId = securityUtils.getLoggedInAccountId();
                Instructor instructor = instructorRepository.findByAccountId(accountId)
                        .orElseThrow(() -> new IllegalArgumentException("Instructor not found for account ID: " + accountId));
                return instructor.getId();
        }

    @Transactional
    public TestResultDTO submitTest(StudentTestSubmissionDTO submissionDTO) {
        Test test = testRepository.findById(submissionDTO.getTestId())
                .orElseThrow(() -> new ResourceNotFoundException("Test not found"));

        Student student = studentRepository.findById(submissionDTO.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        Optional<StudentTestSubmission> existingSubmission = submissionRepository
                .findTopByTestIdAndStudentIdOrderByMarksObtainedDesc(submissionDTO.getTestId(), submissionDTO.getStudentId());

        StudentTestSubmission submission = existingSubmission.orElse(new StudentTestSubmission());
        submission.setStudent(student);
        submission.setTest(test);
        submission.setSubmittedAt(new Timestamp(System.currentTimeMillis()));
        submission.setMarksObtained(0);
        submission.setPassed(false);
        submission.setDuration(test.getDuration());

        submission = submissionRepository.save(submission);

        studentAnswerRepository.deleteBySubmissionId(submission.getId());

        int obtainedMarks = 0;
        int totalMarks = test.getTotalMarks();
        List<StudentAnswer> studentAnswers = new ArrayList<>();

        for (QuestionAnswerDTO qa : submissionDTO.getAnswers()) {
            Question question = questionRepository.findById(qa.getQuestionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Question not found"));

            List<AnswerOption> correctOptions = question.getAnswerOptions().stream()
                    .filter(AnswerOption::getIsCorrect)
                    .toList();
            List<Long> correctOptionIds = correctOptions.stream().map(AnswerOption::getId).toList();

            List<AnswerOption> selectedOptions = answerOptionRepository.findAllById(qa.getSelectedOptionIds());

            boolean isCorrect = correctOptionIds.size() == qa.getSelectedOptionIds().size()
                    && correctOptionIds.containsAll(qa.getSelectedOptionIds());

            if (isCorrect) {
                obtainedMarks += question.getMarks();
            }

            for (AnswerOption selectedOption : selectedOptions) {
                StudentAnswer studentAnswer = new StudentAnswer();
                studentAnswer.setSubmission(submission);
                studentAnswer.setQuestion(question);
                studentAnswer.setSelectedOption(selectedOption);
                studentAnswers.add(studentAnswer);
            }
        }

        studentAnswerRepository.saveAll(studentAnswers);

        submission.setMarksObtained(obtainedMarks);
        submission.setPassed(obtainedMarks >= (test.getPassPercentage() * totalMarks) / 100);
        submissionRepository.save(submission);

        return getTestResult(test.getId(), student.getId());
    }

    public TestResultDTO getTestResult(Long testId, Long studentId) {

        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Test not found"));

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));

        Optional<StudentTestSubmission> submissionOpt =
                submissionRepository.findByTestIdAndStudentId(testId, studentId);

        if (submissionOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No submission found for this test");
        }

        StudentTestSubmission submission = submissionOpt.get();
        int marksObtained = (submission.getMarksObtained() != null) ? submission.getMarksObtained() : 0;
        boolean passed = (submission.getPassed() != null) ? submission.getPassed() : false;

        TestResultDTO result = new TestResultDTO();
        result.setTestId(test.getId());
        result.setStudentId(student.getId());
        result.setMarksObtained(marksObtained);
        result.setPercentage((marksObtained * 100.0) / test.getTotalMarks());
        result.setPassed(passed);

        List<StudentQuestionDTO> answeredQuestions = new ArrayList<>();

        for (Question question : test.getQuestions()) {
            StudentQuestionDTO studentQuestion = new StudentQuestionDTO();
            studentQuestion.setId(question.getId());
            studentQuestion.setContent(question.getContent());
            studentQuestion.setType(question.getType());
            studentQuestion.setMarks(question.getMarks());

            List<StudentAnswer> studentAnswers = submission.getAnswers().stream()
                    .filter(a -> a.getQuestion().getId().equals(question.getId()))
                    .toList();

            List<Long> selectedOptionIds = studentAnswers.stream()
                    .map(a -> a.getSelectedOption().getId())
                    .distinct()
                    .toList();

            List<AnswerOption> correctOptions = question.getAnswerOptions().stream()
                    .filter(AnswerOption::getIsCorrect)
                    .toList();
            List<Long> correctOptionIds = correctOptions.stream()
                    .map(AnswerOption::getId)
                    .toList();

            boolean isCorrect;
            if (question.getType() == QuestionType.MULTIPLE) {
                isCorrect = selectedOptionIds.size() == correctOptionIds.size()
                        && selectedOptionIds.containsAll(correctOptionIds);
            } else {
                isCorrect = selectedOptionIds.equals(correctOptionIds);
            }

            studentQuestion.setGivenAnswers(selectedOptionIds.stream()
                    .map(id -> {
                        AnswerOption opt = answerOptionRepository.findById(id).orElse(null);
                        return opt != null ? new AnswerOptionDTO(opt.getId(), opt.getContent(), opt.getIsCorrect(), question.getId()) : null;
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList()));

            studentQuestion.setCorrectAnswers(correctOptions.stream()
                    .map(opt -> new AnswerOptionDTO(opt.getId(), opt.getContent(), opt.getIsCorrect(), question.getId()))
                    .collect(Collectors.toList()));

            studentQuestion.setCorrect(isCorrect);

            answeredQuestions.add(studentQuestion);
        }

        result.setQuestions(answeredQuestions);
        return result;
    }
}
