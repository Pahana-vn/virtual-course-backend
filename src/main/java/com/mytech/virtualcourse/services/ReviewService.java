// src/main/java/com/mytech/virtualcourse/services/ReviewService.java

package com.mytech.virtualcourse.services;

import com.mytech.virtualcourse.dtos.ReviewDTO;
import com.mytech.virtualcourse.entities.Course;
import com.mytech.virtualcourse.entities.Instructor;
import com.mytech.virtualcourse.entities.Review;
import com.mytech.virtualcourse.entities.Student;
import com.mytech.virtualcourse.exceptions.GlobalExceptionHandler.UnauthorizedActionException;
import com.mytech.virtualcourse.exceptions.ResourceNotFoundException;
import com.mytech.virtualcourse.mappers.ReviewMapper;
import com.mytech.virtualcourse.repositories.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ReviewService {

    private static final Logger logger = LoggerFactory.getLogger(ReviewService.class);

    private final ReviewRepository reviewRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final InstructorRepository instructorRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final ReviewMapper reviewMapper;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository,
                         StudentRepository studentRepository,
                         CourseRepository courseRepository,
                         InstructorRepository instructorRepository,
                         EnrollmentRepository enrollmentRepository,
                         ReviewMapper reviewMapper) {
        this.reviewRepository = reviewRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.instructorRepository = instructorRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.reviewMapper = reviewMapper;
    }

    /**
     * Tạo một đánh giá mới.
     *
     * @param dto Dữ liệu đánh giá.
     * @return ReviewDTO đã được lưu.
     */
    @Transactional
    public ReviewDTO createReview(ReviewDTO dto) {
        logger.info("Creating review for studentId={} and courseId={}", dto.getStudentId(), dto.getCourseId());

        Student student = studentRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id=" + dto.getStudentId()));

        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id=" + dto.getCourseId()));

        // Kiểm tra xem student có đang đăng ký khóa học hay không
        boolean isEnrolled = enrollmentRepository.existsByStudentIdAndCourseId(dto.getStudentId(), dto.getCourseId());
        if (!isEnrolled) {
            throw new UnauthorizedActionException("Student is not enrolled in this course.");
        }

        Review review = reviewMapper.toEntity(dto);
        review.setStudent(student);
        review.setCourse(course);

        if (dto.getInstructorId() != null) {
            Instructor instructor = instructorRepository.findById(dto.getInstructorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with id=" + dto.getInstructorId()));
            review.setInstructor(instructor);
        }

        Review savedReview = reviewRepository.save(review);
        logger.info("Review created with id={}", savedReview.getId());
        return reviewMapper.toDTO(savedReview);
    }

    // public List<ReviewDTO> getReviewsByInstructorId(Long instructorId) {
    //     List<Review> reviews = reviewRepository.findByCourseInstructorId(instructorId);
    //     if (reviews.isEmpty()) {
    //         throw new ResourceNotFoundException("No reviews found for instructor with id: " + instructorId);
    //     }
    //     return reviews.stream()
    //             .map(reviewMapper::toDTO)
    //             .collect(Collectors.toList());
    // }

    /**
     * Kiểm tra xem người dùng có phải là chủ sở hữu của đánh giá hay không.
     *
     * @param reviewId ID của đánh giá.
     * @param userId   ID của người dùng.
     * @return true nếu người dùng là chủ sở hữu, ngược lại false.
     */
    public boolean isReviewOwner(Long reviewId, Long userId) {
        return reviewRepository.findById(reviewId)
                .map(review -> review.getStudent().getAccount().getId().equals(userId))
                .orElse(false);
    }

    /**
     * Lấy danh sách đánh giá cho một khóa học cụ thể với phân trang.
     *
     * @param courseId ID của khóa học.
     * @param pageable Thông tin phân trang.
     * @return Page<ReviewDTO>.
     */
    @Transactional(readOnly = true)
    public Page<ReviewDTO> getReviewsByCourse(Long courseId, Pageable pageable) {
        logger.info("Fetching reviews for courseId={} with pageable={}", courseId, pageable);
        if (!courseRepository.existsById(courseId)) {
            throw new ResourceNotFoundException("Course not found with id = " + courseId);
        }
        Page<Review> reviews = reviewRepository.findByCourseId(courseId, pageable);
        return reviews.map(reviewMapper::toDTO);
    }

    /**
     * Xóa đánh giá theo ID.
     *
     * @param reviewId ID của đánh giá.
     */
    public void deleteReview(Long reviewId) {
        if (!reviewRepository.existsById(reviewId)) {
            throw new ResourceNotFoundException("Review not found with id=" + reviewId);
        }
        reviewRepository.deleteById(reviewId);
    }

    /**
     * Cập nhật đánh giá.
     *
     * @param reviewId ID của đánh giá.
     * @param dto      Dữ liệu đánh giá mới.
     * @return ReviewDTO đã được cập nhật.
     */
    @Transactional
    public ReviewDTO updateReview(Long reviewId, ReviewDTO dto) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id=" + reviewId));

        // Kiểm tra quyền sửa review
        if (!review.getStudent().getAccount().getId().equals(dto.getStudentId())) {
            throw new UnauthorizedActionException("You cannot edit someone else's review!");
        }

        // Cập nhật các trường cần thiết
        review.setRating(dto.getRating());
        review.setComment(dto.getComment());
        // Nếu có thêm trường reply từ giảng viên, có thể thêm ở đây

        Review updatedReview = reviewRepository.save(review);
        return reviewMapper.toDTO(updatedReview);
    }
}
