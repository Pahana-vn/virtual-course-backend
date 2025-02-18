package com.mytech.virtualcourse.services;

import com.mytech.virtualcourse.dtos.*;
import com.mytech.virtualcourse.entities.*;
import com.mytech.virtualcourse.enums.QuestionType;
import com.mytech.virtualcourse.exceptions.ResourceNotFoundException;
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

                // Chuyển DTO thành entity và lưu
                Test test = testMapper.testDTOToTest(testDTO);
                test.setCourse(course);
                test.setInstructor(course.getInstructor());

                if (test.getStatusTest() == null) {
                        test.setStatusTest(StatusTest.INACTIVE);
                }

                // Lấy danh sách câu hỏi từ database
                if (testDTO.getQuestions() != null && !testDTO.getQuestions().isEmpty()) {
                        List<Question> questions = questionRepository.findAllById(
                                testDTO.getQuestions().stream().map(QuestionDTO::getId).collect(Collectors.toList())
                        );
                        // Kiểm tra các câu hỏi có thuộc khóa học này không
                        boolean allQuestionsBelongToCourse = questions.stream()
                                .allMatch(q -> q.getCourse().getId().equals(courseId));
                        if (!allQuestionsBelongToCourse) {
                                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Some questions do not belong to the course.");
                        }
                        test.setQuestions(questions); // Liên kết câu hỏi với bài kiểm tra
                } else {
                        test.setQuestions(new ArrayList<>()); // Không có câu hỏi được chọn
                }

                testRepository.save(test);

                return testMapper.testToTestDTO(test);
        }

        public TestDTO updateTest(Long id, TestDTO updatedTestDTO, Long loggedInInstructorId) {
                // Lấy bài kiểm tra
                Test existingTest = testRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Test not found with ID: " + id));

                // Kiểm tra quyền sở hữu bài kiểm tra
                if (!existingTest.getCourse().getInstructor().getId().equals(loggedInInstructorId)) {
                        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to update this test.");
                }

                // Chỉ cập nhật các trường cần thiết
                existingTest.setTitle(updatedTestDTO.getTitle());
                existingTest.setDescription(updatedTestDTO.getDescription());
                existingTest.setTotalMarks(updatedTestDTO.getTotalMarks());
                existingTest.setPassPercentage(updatedTestDTO.getPassPercentage());
                existingTest.setDuration(updatedTestDTO.getDuration());
                existingTest.setIsFinalTest(updatedTestDTO.getIsFinalTest());
                existingTest.setStatusTest(updatedTestDTO.getStatusTest());

                // Liên kết lại câu hỏi nếu có
                if (updatedTestDTO.getQuestions() != null && !updatedTestDTO.getQuestions().isEmpty()) {
                        List<Question> questions = questionRepository.findAllById(
                                updatedTestDTO.getQuestions().stream().map(QuestionDTO::getId).collect(Collectors.toList())
                        );

                        // Kiểm tra câu hỏi có thuộc khóa học không
                        boolean allQuestionsBelongToCourse = questions.stream()
                                .allMatch(q -> q.getCourse().getId().equals(existingTest.getCourse().getId()));
                        if (!allQuestionsBelongToCourse) {
                                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Some questions do not belong to the course.");
                        }

                        existingTest.setQuestions(questions);
                } else {
                        existingTest.setQuestions(new ArrayList<>()); // Xóa câu hỏi nếu danh sách rỗng
                }

                // Lưu bài kiểm tra đã cập nhật
                Test savedTest = testRepository.save(existingTest);
                return testMapper.testToTestDTO(savedTest);
        }


        public void deleteTest(Long id, Long loggedInInstructorId) {
                // Lấy bài kiểm tra
                Test test = testRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Test not found with ID: " + id));

                // Kiểm tra quyền sở hữu bài kiểm tra
                if (!test.getCourse().getInstructor().getId().equals(loggedInInstructorId)) {
                        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to delete this test.");
                }

                // Xóa bài kiểm tra
                testRepository.delete(test);
        }

        public Long getLoggedInInstructorId() {
                Long accountId = securityUtils.getLoggedInAccountId();
                Instructor instructor = instructorRepository.findByAccountId(accountId)
                        .orElseThrow(() -> new IllegalArgumentException("Instructor not found for account ID: " + accountId));
                return instructor.getId();
        }
    public TestResultDTO submitTest(StudentTestSubmissionDTO submissionDTO) {
        // Lấy test
        Test test = testRepository.findById(submissionDTO.getTestId())
                .orElseThrow(() -> new ResourceNotFoundException("Test not found"));

        Student student = studentRepository.findById(submissionDTO.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        // Tính điểm
        int totalMarks = test.getTotalMarks();
        int obtainedMarks = 0;
        for (QuestionAnswerDTO qa : submissionDTO.getAnswers()) {
            Question q = questionRepository.findById(qa.getQuestionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Question not found"));
            boolean correct = checkAnswer(q, qa.getSelectedOptionIds());
            if (correct) {
                obtainedMarks += q.getMarks();
            }
        }

        double percentage = (obtainedMarks * 100.0) / totalMarks;
        boolean passed = percentage >= test.getPassPercentage();

        // Lưu submission
        StudentTestSubmission sts = new StudentTestSubmission();
        sts.setStudent(student);
        sts.setTest(test);
        sts.setSubmittedAt(new Timestamp(System.currentTimeMillis()));
        sts.setMarksObtained(obtainedMarks);
        sts.setPassed(passed);
        sts.setDurationTest(test.getDuration());
        submissionRepository.save(sts);

        // Trả về kết quả
        TestResultDTO result = new TestResultDTO();
        result.setTestId(test.getId());
        result.setStudentId(student.getId());
        result.setMarksObtained(obtainedMarks);
        result.setPercentage(percentage);
        result.setPassed(passed);
        return result;
    }

    private boolean checkAnswer(Question q, List<Long> selectedOptionIds) {
        List<AnswerOption> correctOptions = q.getAnswerOptions().stream()
                .filter(AnswerOption::getIsCorrect)
                .collect(Collectors.toList());
        List<Long> correctOptionIds = correctOptions.stream().map(AnswerOption::getId).collect(Collectors.toList());

        // Kiểm tra nếu số đáp án chọn bằng số đáp án đúng và tất cả đều đúng
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
}
