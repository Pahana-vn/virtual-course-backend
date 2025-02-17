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
    private QuestionMapper questionMapper;

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

        public TestDTO createTestForCourse(Long courseId, TestDTO testDTO, Long loggedInInstructorId) {
                Course course = courseRepository.findById(courseId)
                        .orElseThrow(() -> new IllegalArgumentException("Course not found with ID: " + courseId));

                if (!course.getInstructor().getId().equals(loggedInInstructorId)) {
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
    public TestResultDTO submitTest(StudentTestSubmissionDTO submissionDTO) {
        Test test = testRepository.findById(submissionDTO.getTestId())
                .orElseThrow(() -> new ResourceNotFoundException("Test not found"));

        Student student = studentRepository.findById(submissionDTO.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        // Kiểm tra xem học viên đã từng làm bài chưa
        Optional<StudentTestSubmission> existingSubmission = submissionRepository.findTopByTestIdAndStudentIdOrderByMarksObtainedDesc(submissionDTO.getTestId(), submissionDTO.getStudentId());

        StudentTestSubmission sts;
        if (existingSubmission.isPresent()) {
            // Nếu đã có bài làm, cập nhật điểm nếu tốt hơn
            sts = existingSubmission.get();
        } else {
            // Nếu chưa có bài làm, tạo mới
            sts = new StudentTestSubmission();
            sts.setStudent(student);
            sts.setTest(test);
            sts.setSubmittedAt(new Timestamp(System.currentTimeMillis()));
        }

        int obtainedMarks = 0;
        int totalMarks = test.getTotalMarks();

        for (QuestionAnswerDTO qa : submissionDTO.getAnswers()) {
            Question question = questionRepository.findById(qa.getQuestionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Question not found"));

            boolean isCorrect = checkAnswer(question, qa.getSelectedOptionIds());
            if (isCorrect) {
                obtainedMarks += question.getMarks();
            }
        }

        double percentage = (obtainedMarks * 100.0) / totalMarks;
        boolean passed = percentage >= test.getPassPercentage();

        // Cập nhật điểm nếu điểm mới cao hơn điểm cũ
        if (obtainedMarks > sts.getMarksObtained()) {
            sts.setMarksObtained(obtainedMarks);
            sts.setPassed(passed);
            submissionRepository.save(sts);
        }

        // Trả về kết quả
        TestResultDTO result = new TestResultDTO();
        result.setTestId(test.getId());
        result.setStudentId(student.getId());
        result.setMarksObtained(sts.getMarksObtained());
        result.setPercentage((sts.getMarksObtained() * 100.0) / test.getTotalMarks());
        result.setPassed(sts.getPassed());

        return result;
    }


    private boolean checkAnswer(Question q, List<Long> selectedOptionIds) {
        List<AnswerOption> correctOptions = q.getAnswerOptions().stream()
                .filter(AnswerOption::getIsCorrect)
                .collect(Collectors.toList());
        List<Long> correctOptionIds = correctOptions.stream().map(AnswerOption::getId).collect(Collectors.toList());
        return correctOptionIds.size() == selectedOptionIds.size() && correctOptionIds.containsAll(selectedOptionIds);
    }


    private QuestionDTO mapToQuestionDTO(Question q) {
        QuestionDTO dto = new QuestionDTO();
        dto.setId(q.getId());
        dto.setContent(q.getContent());
        dto.setType(QuestionType.valueOf(q.getType().toString()));
        dto.setAnswerOptions(q.getAnswerOptions().stream().map(opt -> {
            AnswerOptionDTO aodto = new AnswerOptionDTO();
            aodto.setId(opt.getId());
            aodto.setContent(opt.getContent());
            return aodto;
        }).collect(Collectors.toList()));
        return dto;
    }

    public TestResultDTO getTestResult(Long testId, Long studentId) {
        // Kiểm tra xem bài kiểm tra có tồn tại không
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Test not found"));

        // Kiểm tra xem học viên có tồn tại không
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));

        // Kiểm tra xem có bài nộp không
        Optional<StudentTestSubmission> submissionOpt =
                submissionRepository.findByTestIdAndStudentId(testId, studentId);

        if (submissionOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No submission found for this test");
        }

        StudentTestSubmission submission = submissionOpt.get();

        if (submission.getMarksObtained() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Marks not calculated yet");
        }

        // Tạo DTO để trả về kết quả
        TestResultDTO result = new TestResultDTO();
        result.setTestId(test.getId());
        result.setStudentId(student.getId());
        result.setMarksObtained(submission.getMarksObtained());
        result.setPercentage((submission.getMarksObtained() * 100.0) / test.getTotalMarks());
        result.setPassed(submission.getPassed());

        return result;
    }
}
