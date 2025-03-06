package com.mytech.virtualcourse.services;
import com.mytech.virtualcourse.dtos.ReviewDTO;
import com.mytech.virtualcourse.dtos.ReviewStatisticsDTO;
import com.mytech.virtualcourse.entities.Course;
import com.mytech.virtualcourse.entities.Instructor;
import com.mytech.virtualcourse.entities.Review;
import com.mytech.virtualcourse.entities.Student;
import com.mytech.virtualcourse.exceptions.ResourceNotFoundException;
import com.mytech.virtualcourse.exceptions.UnauthorizedActionException;
import com.mytech.virtualcourse.mappers.ReviewMapper;
import com.mytech.virtualcourse.repositories.CourseRepository;
import com.mytech.virtualcourse.repositories.EnrollmentRepository;
import com.mytech.virtualcourse.repositories.InstructorRepository;
import com.mytech.virtualcourse.repositories.ReviewRepository; import com.mytech.virtualcourse.repositories.StudentRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page; import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator; import java.util.HashMap;
import java.util.List; import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service @Transactional public class ReviewService {
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
     * Create a new review.
     *
     * @param dto Review data.
     * @return Saved ReviewDTO.
     */
    @Transactional
    public ReviewDTO createReview(ReviewDTO dto) {
        logger.info("Creating review for studentId={} and courseId={}", dto.getStudentId(), dto.getCourseId());

        Student student = studentRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id=" + dto.getStudentId()));

        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id=" + dto.getCourseId()));

        // Check if student is enrolled in the course
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

    /**
     * Check if user is the owner of a review.
     *
     * @param reviewId Review ID.
     * @param userId User ID.
     * @return true if user is the owner, false otherwise.
     */
    public boolean isReviewOwner(Long reviewId, Long userId) {
        return reviewRepository.findById(reviewId)
                .map(review -> review.getStudent().getAccount().getId().equals(userId))
                .orElse(false);
    }

    /**
     * Get reviews for a specific course with pagination.
     *
     * @param courseId Course ID.
     * @param pageable Pagination information.
     * @return Page of ReviewDTO.
     */
    @Transactional(readOnly = true)
    public Page<ReviewDTO> getReviewsByCourse(Long courseId, Pageable pageable) {
        logger.info("Fetching reviews for courseId={} with pageable={}", courseId, pageable);

        if (!courseRepository.existsById(courseId)) {
            throw new ResourceNotFoundException("Course not found with id = " + courseId);
        }

        Page<Review> reviews = reviewRepository.findByCourseId(courseId, pageable);
        return reviews.map(review -> {
            ReviewDTO dto = reviewMapper.toDTO(review);

            // Enhance DTO with additional information
            if (review.getStudent() != null) {
                dto.setStudentName(review.getStudent().getFirstName() + " " + review.getStudent().getLastName());
                dto.setStudentAvatar(review.getStudent().getAvatar());
            }

            if (review.getCourse() != null) {
                dto.setCourseTitle(review.getCourse().getTitleCourse());
            }

            if (review.getInstructor() != null) {
                dto.setInstructorName(review.getInstructor().getFirstName() + " " + review.getInstructor().getLastName());
            }

            return dto;
        });
    }

    /**
     * Delete a review by ID.
     *
     * @param reviewId Review ID.
     */
    public void deleteReview(Long reviewId) {
        if (!reviewRepository.existsById(reviewId)) {
            throw new ResourceNotFoundException("Review not found with id=" + reviewId);
        }
        reviewRepository.deleteById(reviewId);
        logger.info("Review deleted with id={}", reviewId);
    }

    /**
     * Update a review.
     *
     * @param reviewId Review ID.
     * @param dto Updated review data.
     * @return Updated ReviewDTO.
     */
    @Transactional
    public ReviewDTO updateReview(Long reviewId, ReviewDTO dto) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id=" + reviewId));

        // Check if user has permission to edit the review
        if (!review.getStudent().getId().equals(dto.getStudentId())) {
            throw new UnauthorizedActionException("You cannot edit someone else's review!");
        }

        // Update necessary fields
        review.setRating(dto.getRating());
        review.setComment(dto.getComment());

        Review updatedReview = reviewRepository.save(review);
        logger.info("Review updated with id={}", updatedReview.getId());

        return reviewMapper.toDTO(updatedReview);
    }

    /**
     * Get all reviews with pagination and filtering (admin function).
     *
     * @param courseId Optional filter by course ID.
     * @param studentId Optional filter by student ID.
     * @param rating Optional filter by rating.
     * @param pageable Pagination information.
     * @return Page of ReviewDTO.
     */
    @Transactional(readOnly = true)
    public Page<ReviewDTO> getAllReviews(Long courseId, Long studentId, Integer rating, Pageable pageable) {
        logger.info("Fetching all reviews with filters: courseId={}, studentId={}, rating={}", courseId, studentId, rating);

        Specification<Review> spec = Specification.where(null);

        if (courseId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("course").get("id"), courseId));
        }

        if (studentId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("student").get("id"), studentId));
        }

        if (rating != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("rating"), rating));
        }

        Page<Review> reviews = reviewRepository.findAll(spec, pageable);
        return reviews.map(review -> {
            ReviewDTO dto = reviewMapper.toDTO(review);

            // Enhance DTO with additional information
            if (review.getStudent() != null) {
                dto.setStudentName(review.getStudent().getFirstName() + " " + review.getStudent().getLastName());
                dto.setStudentAvatar(review.getStudent().getAvatar());
            }

            if (review.getCourse() != null) {
                dto.setCourseTitle(review.getCourse().getTitleCourse());
            }

            if (review.getInstructor() != null) {
                dto.setInstructorName(review.getInstructor().getFirstName() + " " + review.getInstructor().getLastName());
            }

            return dto;
        });
    }

    /**
     * Get review statistics with optional filtering.
     *
     * @param courseId Optional filter by course ID.
     * @param instructorId Optional filter by instructor ID.
     * @param startDate Optional filter by start date.
     * @param endDate Optional filter by end date.
     * @return ReviewStatisticsDTO.
     */
    @Transactional(readOnly = true)
    public ReviewStatisticsDTO getReviewStatistics(Long courseId, Long instructorId, LocalDate startDate, LocalDate endDate) {
        logger.info("Fetching review statistics with filters: courseId={}, instructorId={}, startDate={}, endDate={}",
                courseId, instructorId, startDate, endDate);

        ReviewStatisticsDTO statistics = new ReviewStatisticsDTO();

        // Build base query conditions
        Specification<Review> spec = Specification.where(null);

        if (courseId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("course").get("id"), courseId));
        }

        if (instructorId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("course").get("instructor").get("id"), instructorId));
        }

        if (startDate != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("createdAt"), startDate.atStartOfDay()));
        }

        if (endDate != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("createdAt"), endDate.plusDays(1).atStartOfDay()));
        }

        // Get total reviews
        Long totalReviews = reviewRepository.count(spec);
        statistics.setTotalReviews(totalReviews);

        if (totalReviews == 0) {
            statistics.setAverageRating(0.0);
            return statistics;
        }

        // Get average rating
        Double averageRating = reviewRepository.findAll(spec)
                .stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);
        statistics.setAverageRating(averageRating);

        // Get rating distribution
        Map<Integer, Long> ratingDistribution = reviewRepository.findAll(spec)
                .stream()
                .collect(Collectors.groupingBy(Review::getRating, Collectors.counting()));
        statistics.setRatingDistribution(ratingDistribution);

        // Calculate positive and negative reviews
        Long positiveReviews = ratingDistribution.entrySet().stream()
                .filter(entry -> entry.getKey() >= 4)
                .mapToLong(Map.Entry::getValue)
                .sum();

        Long negativeReviews = ratingDistribution.entrySet().stream()
                .filter(entry -> entry.getKey() <= 2)
                .mapToLong(Map.Entry::getValue)
                .sum();

        statistics.setTotalPositiveReviews(positiveReviews);
        statistics.setTotalNegativeReviews(negativeReviews);

        // Get monthly statistics
        Map<String, ReviewStatisticsDTO.MonthlyReviewData> monthlyData = new HashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");

        reviewRepository.findAll(spec).forEach(review -> {
            String month = review.getCreatedAt().format(formatter);
            monthlyData.computeIfAbsent(month, k -> new ReviewStatisticsDTO.MonthlyReviewData(0L, 0.0));

            ReviewStatisticsDTO.MonthlyReviewData data = monthlyData.get(month);
            data.setCount(data.getCount() + 1);

                // Update running average
            double currentTotal = data.getAverageRating() * (data.getCount() - 1);
            data.setAverageRating((currentTotal + review.getRating()) / data.getCount());
        });

        statistics.setMonthlyReviews(monthlyData);

        // Get top rated courses (limited to 5)
        if (courseId == null) { // Only if not filtering by a specific course
            List<ReviewStatisticsDTO.TopRatedItemDTO> topCourses = courseRepository.findAll().stream()
                    .filter(course -> course.getReviews() != null && !course.getReviews().isEmpty())
                    .map(course -> {
                        double avgRating = course.getReviews().stream()
                                .mapToInt(Review::getRating)
                                .average()
                                .orElse(0.0);

                        return new ReviewStatisticsDTO.TopRatedItemDTO(
                                course.getId(),
                                course.getTitleCourse(),
                                avgRating,
                                (long) course.getReviews().size()
                        );
                    })
                    .sorted(Comparator.comparing(ReviewStatisticsDTO.TopRatedItemDTO::getAverageRating).reversed())
                    .limit(5)
                    .collect(Collectors.toList());

            statistics.setTopCourses(topCourses);
        }

        // Get top rated instructors (limited to 5)
        if (instructorId == null) { // Only if not filtering by a specific instructor
            List<ReviewStatisticsDTO.TopRatedItemDTO> topInstructors = instructorRepository.findAll().stream()
                    .filter(instructor -> instructor.getCourses() != null &&
                            instructor.getCourses().stream()
                                    .anyMatch(course -> course.getReviews() != null && !course.getReviews().isEmpty()))
                    .map(instructor -> {
                        List<Review> instructorReviews = instructor.getCourses().stream()
                                .flatMap(course -> course.getReviews() != null ? course.getReviews().stream() : Stream.empty())
                                .collect(Collectors.toList());

                        double avgRating = instructorReviews.stream()
                                .mapToInt(Review::getRating)
                                .average()
                                .orElse(0.0);

                        return new ReviewStatisticsDTO.TopRatedItemDTO(
                                instructor.getId(),
                                instructor.getFirstName() + " " + instructor.getLastName(),
                                avgRating,
                                (long) instructorReviews.size()
                        );
                    })
                    .sorted(Comparator.comparing(ReviewStatisticsDTO.TopRatedItemDTO::getAverageRating).reversed())
                    .limit(5)
                    .collect(Collectors.toList());

            statistics.setTopInstructors(topInstructors);
        }

        return statistics;
    }

    /**
     * Get a specific review by ID.
     *
     * @param reviewId Review ID.
     * @return ReviewDTO.
     */
    @Transactional(readOnly = true)
    public ReviewDTO getReviewById(Long reviewId) {
        logger.info("Fetching review with id={}", reviewId);

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id=" + reviewId));

        ReviewDTO dto = reviewMapper.toDTO(review);

        // Enhance DTO with additional information
        if (review.getStudent() != null) {
            dto.setStudentName(review.getStudent().getFirstName() + " " + review.getStudent().getLastName());
            dto.setStudentAvatar(review.getStudent().getAvatar());
        }

        if (review.getCourse() != null) {
            dto.setCourseTitle(review.getCourse().getTitleCourse());
        }

        if (review.getInstructor() != null) {
            dto.setInstructorName(review.getInstructor().getFirstName() + " " + review.getInstructor().getLastName());
        }

        return dto;
    }

    /**
     * Moderate a review (admin function).
     *
     * @param reviewId Review ID.
     * @param dto Updated review data.
     * @return Updated ReviewDTO.
     */
    @Transactional
    public ReviewDTO moderateReview(Long reviewId, ReviewDTO dto) {
        logger.info("Moderating review with id={}", reviewId);

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id=" + reviewId));

        // Update fields that can be moderated
        if (dto.getRating() != null) {
            review.setRating(dto.getRating());
        }

        if (dto.getComment() != null) {
            review.setComment(dto.getComment());
        }

        Review updatedReview = reviewRepository.save(review);
        logger.info("Review moderated successfully: id={}", reviewId);

        return reviewMapper.toDTO(updatedReview);
    }

    /**
     * Get reviews by course and rating.
     *
     * @param courseId Course ID.
     * @param rating Rating value.
     * @param pageable Pagination information.
     * @return Page of ReviewDTO.
     */
    @Transactional(readOnly = true)
    public Page<ReviewDTO> getReviewsByCourseAndRating(Long courseId, Integer rating, Pageable pageable) {
        logger.info("Fetching reviews for courseId={} and rating={}", courseId, rating);

        // This would require a custom repository method
        Page<Review> reviews = reviewRepository.findByCourseIdAndRating(courseId, rating, pageable);
        return reviews.map(reviewMapper::toDTO);
    }

    /**
     * Get reviews by student and rating.
     *
     * @param studentId Student ID.
     * @param rating Rating value.
     * @param pageable Pagination information.
     * @return Page of ReviewDTO.
     */
    @Transactional(readOnly = true)
    public Page<ReviewDTO> getReviewsByStudentAndRating(Long studentId, Integer rating, Pageable pageable) {
        logger.info("Fetching reviews for studentId={} and rating={}", studentId, rating);

        // This would require a custom repository method
        Page<Review> reviews = reviewRepository.findByStudentIdAndRating(studentId, rating, pageable);
        return reviews.map(reviewMapper::toDTO);
    }

    /**
     * Get reviews by course and student.
     *
     * @param courseId Course ID.
     * @param studentId Student ID.
     * @param pageable Pagination information.
     * @return Page of ReviewDTO.
     */
    @Transactional(readOnly = true)
    public Page<ReviewDTO> getReviewsByCourseAndStudent(Long courseId, Long studentId, Pageable pageable) {
        logger.info("Fetching reviews for courseId={} and studentId={}", courseId, studentId);

        // This would require a custom repository method
        Page<Review> reviews = reviewRepository.findByCourseIdAndStudentId(courseId, studentId, pageable);
        return reviews.map(reviewMapper::toDTO);
    }

    /**
     * Get reviews by course, student and rating.
     *
     * @param courseId Course ID.
     * @param studentId Student ID.
     * @param rating Rating value.
     * @param pageable Pagination information.
     * @return Page of ReviewDTO.
     */
    @Transactional(readOnly = true)
    public Page<ReviewDTO> getReviewsByCourseAndStudentAndRating(Long courseId, Long studentId, Integer rating, Pageable pageable) {
        logger.info("Fetching reviews for courseId={}, studentId={} and rating={}", courseId, studentId, rating);

        // This would require a custom repository method
        Page<Review> reviews = reviewRepository.findByCourseIdAndStudentIdAndRating(courseId, studentId, rating, pageable);
        return reviews.map(reviewMapper::toDTO);
    }

    /**
     * Get reviews by rating.
     *
     * @param rating Rating value.
     * @param pageable Pagination information.
     * @return Page of ReviewDTO.
     */
    @Transactional(readOnly = true)
    public Page<ReviewDTO> getReviewsByRating(Integer rating, Pageable pageable) {
        logger.info("Fetching reviews for rating={}", rating);

        Page<Review> reviews = reviewRepository.findByRating(rating, pageable);
        return reviews.map(reviewMapper::toDTO);
    }

    /**
     * Get reviews by instructor.
     *
     * @param instructorId Instructor ID.
     * @param pageable Pagination information.
     * @return Page of ReviewDTO.
     */
    @Transactional(readOnly = true)
    public Page<ReviewDTO> getReviewsByInstructor(Long instructorId, Pageable pageable) {
        logger.info("Fetching reviews for instructorId={}", instructorId);

        Page<Review> reviews = reviewRepository.findByInstructorId(instructorId, pageable);
        return reviews.map(reviewMapper::toDTO);
    }

    /**
     * Get reviews by instructor and rating.
     *
     * @param instructorId Instructor ID.
     * @param rating Rating value.
     * @param pageable Pagination information.
     * @return Page of ReviewDTO.
     */
    @Transactional(readOnly = true)
    public Page<ReviewDTO> getReviewsByInstructorAndRating(Long instructorId, Integer rating, Pageable pageable) {
        logger.info("Fetching reviews for instructorId={} and rating={}", instructorId, rating);

        Page<Review> reviews = reviewRepository.findByInstructorIdAndRating(instructorId, rating, pageable);
        return reviews.map(reviewMapper::toDTO);
    }

    /**
     * Get reviews by student.
     *
     * @param studentId Student ID.
     * @param pageable Pagination information.
     * @return Page of ReviewDTO.
     */
    @Transactional(readOnly = true)
    public Page<ReviewDTO> getReviewsByStudent(Long studentId, Pageable pageable) {
        logger.info("Fetching reviews for studentId={}", studentId);

        // This would require a custom repository method
        Page<Review> reviews = reviewRepository.findByStudentId(studentId, pageable);
        return reviews.map(reviewMapper::toDTO);
    }

    /**
     * Get average rating for a course.
     *
     * @param courseId Course ID.
     * @return Average rating.
     */
    @Transactional(readOnly = true)
    public Double getAverageRatingForCourse(Long courseId) {
        logger.info("Fetching average rating for courseId={}", courseId);

        return reviewRepository.getAverageRatingForCourse(courseId);
    }

    /**
     * Get average rating for an instructor.
     *
     * @param instructorId Instructor ID.
     * @return Average rating.
     */
    @Transactional(readOnly = true)
    public Double getAverageRatingForInstructor(Long instructorId) {
        logger.info("Fetching average rating for instructorId={}", instructorId);

        return reviewRepository.getAverageRatingForInstructor(instructorId);
    }

    /**
     * Count reviews by course ID.
     *
     * @param courseId Course ID.
     * @return Number of reviews.
     */
    @Transactional(readOnly = true)
    public Long countReviewsByCourseId(Long courseId) {
        logger.info("Counting reviews for courseId={}", courseId);

        return reviewRepository.countReviewsByCourseId(courseId);
    }

    /**
     * Count reviews by instructor ID and rating.
     *
     * @param instructorId Instructor ID.
     * @param rating Rating value.
     * @return Number of reviews.
     */
    @Transactional(readOnly = true)
    public Long countReviewsByInstructorIdAndRating(Long instructorId, Integer rating) {
        logger.info("Counting reviews for instructorId={} and rating={}", instructorId, rating);

        return reviewRepository.countReviewsByInstructorIdAndRating(instructorId, rating);
    }

    /**
     * Count reviews by instructor ID and date range.
     *
     * @param instructorId Instructor ID.
     * @param startDate Start date.
     * @param endDate End date.
     * @return Number of reviews.
     */
    @Transactional(readOnly = true)
    public Long countReviewsByInstructorIdAndDateRange(Long instructorId, LocalDate startDate, LocalDate endDate) {
        logger.info("Counting reviews for instructorId={} between {} and {}", instructorId, startDate, endDate);

        return reviewRepository.countReviewsByInstructorIdAndDateRange(instructorId, startDate, endDate);
    }
}
