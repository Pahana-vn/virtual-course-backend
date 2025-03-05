//    package com.mytech.virtualcourse.services;
//
//    import com.mytech.virtualcourse.dtos.StatisticsDTO;
//    import com.mytech.virtualcourse.dtos.TrendsDTO;
//    import com.mytech.virtualcourse.mappers.StatisticsMapper;
//    import com.mytech.virtualcourse.mappers.TrendsMapper;
//    import com.mytech.virtualcourse.repositories.*;
//    import org.springframework.beans.factory.annotation.Autowired;
//    import org.springframework.stereotype.Service;
//    import org.springframework.transaction.annotation.Transactional;
//
//    import java.time.*;
//    import java.time.temporal.WeekFields;
//    import java.util.*;
//
//    @Transactional
//    @Service
//    public class StatisticsService {
//
//        @Autowired
//        private AdminAccountRepository accountRepository;
//
//        @Autowired
//        private InstructorRepository instructorRepository;
//
//        @Autowired
//        private StudentRepository studentRepository;
//
//        @Autowired
//        private CourseRepository courseRepository;
//
//        @Autowired
//        private CategoryRepository categoryRepository;
//
//        @Autowired
//        private RoleRepository roleRepository;
//
//        @Autowired
//        private StatisticsMapper statisticsMapper;
//
//        @Autowired
//        private TrendsMapper trendsMapper;
//
//        /**
//         * Lấy thống kê dựa trên bộ lọc.
//         *
//         * @param filter Bộ lọc: "today", "week", "month", "year", "all"
//         * @return StatisticsDTO chứa số liệu thống kê.
//         */
//        public StatisticsDTO getStatistics(String filter) {
//            StatisticsDTO stats = new StatisticsDTO();
//
//            switch (filter.toLowerCase()) {
//                case "today":
//                    stats.setAccounts(countAccountsToday());
//                    stats.setInstructors(countInstructorsToday());
//                    stats.setStudents(countStudentsToday());
//                    stats.setCourses(countCoursesToday());
//                    stats.setCategories(countCategoriesToday());
//                    stats.setRoles(countRolesToday());
//                    break;
//                case "week":
//                    stats.setAccounts(countAccountsThisWeek());
//                    stats.setInstructors(countInstructorsThisWeek());
//                    stats.setStudents(countStudentsThisWeek());
//                    stats.setCourses(countCoursesThisWeek());
//                    stats.setCategories(countCategoriesThisWeek());
//                    stats.setRoles(countRolesThisWeek());
//                    break;
//                case "month":
//                    stats.setAccounts(countAccountsThisMonth());
//                    stats.setInstructors(countInstructorsThisMonth());
//                    stats.setStudents(countStudentsThisMonth());
//                    stats.setCourses(countCoursesThisMonth());
//                    stats.setCategories(countCategoriesThisMonth());
//                    stats.setRoles(countRolesThisMonth());
//                    break;
//                case "year":
//                    stats.setAccounts(countAccountsThisYear());
//                    stats.setInstructors(countInstructorsThisYear());
//                    stats.setStudents(countStudentsThisYear());
//                    stats.setCourses(countCoursesThisYear());
//                    stats.setCategories(countCategoriesThisYear());
//                    break;
//                default:
//                    // Default to all-time counts
//                    stats.setAccounts(accountRepository.count());
//                    stats.setInstructors(instructorRepository.count());
//                    stats.setStudents(studentRepository.count());
//                    stats.setCourses(courseRepository.count());
//                    stats.setCategories(categoryRepository.count());
//                    stats.setRoles(roleRepository.count());
//                    break;
//            }
//
//            return stats;
//        }
//
//        /**
//         * Lấy xu hướng thống kê dựa trên bộ lọc.
//         *
//         * @param filter Bộ lọc: "today", "week", "month", "year", "all"
//         * @return TrendsDTO chứa dữ liệu xu hướng.
//         */
//        public TrendsDTO getTrends(String filter) {
//            TrendsDTO trends = new TrendsDTO();
//
//            switch (filter.toLowerCase()) {
//                case "today":
//                    trends.setDates(Collections.singletonList(LocalDate.now().toString()));
//                    trends.setAccounts(Collections.singletonList(countAccountsToday()));
//                    trends.setInstructors(Collections.singletonList(countInstructorsToday()));
//                    trends.setStudents(Collections.singletonList(countStudentsToday()));
//                    trends.setCourses(Collections.singletonList(countCoursesToday()));
//                    trends.setCategories(Collections.singletonList(countCategoriesToday()));
//                    trends.setRoles(Collections.singletonList(countRolesToday()));
//                    break;
//                case "week":
//                    trends.setDates(getLast7Days());
//                    trends.setAccounts(getCountsLast7Days("account"));
//                    trends.setInstructors(getCountsLast7Days("instructor"));
//                    trends.setStudents(getCountsLast7Days("student"));
//                    trends.setCourses(getCountsLast7Days("course"));
//                    trends.setCategories(getCountsLast7Days("category"));
//                    trends.setRoles(getCountsLast7Days("role"));
//                    break;
//                case "month":
//                    trends.setDates(getLast30Days());
//                    trends.setAccounts(getCountsLast30Days("account"));
//                    trends.setInstructors(getCountsLast30Days("instructor"));
//                    trends.setStudents(getCountsLast30Days("student"));
//                    trends.setCourses(getCountsLast30Days("course"));
//                    trends.setCategories(getCountsLast30Days("category"));
//                    trends.setRoles(getCountsLast30Days("role"));
//                    break;
//                case "year":
//                    trends.setDates(getLast12Months());
//                    trends.setAccounts(getCountsLast12Months("account"));
//                    trends.setInstructors(getCountsLast12Months("instructor"));
//                    trends.setStudents(getCountsLast12Months("student"));
//                    trends.setCourses(getCountsLast12Months("course"));
//                    trends.setCategories(getCountsLast12Months("category"));
//                    trends.setRoles(getCountsLast12Months("role"));
//                    break;
//                default:
//                    // Default to all-time trends
//                    trends.setDates(Collections.singletonList("All Time"));
//                    trends.setAccounts(Collections.singletonList(accountRepository.count()));
//                    trends.setInstructors(Collections.singletonList(instructorRepository.count()));
//                    trends.setStudents(Collections.singletonList(studentRepository.count()));
//                    trends.setCourses(Collections.singletonList(courseRepository.count()));
//                    trends.setCategories(Collections.singletonList(categoryRepository.count()));
//                    trends.setRoles(Collections.singletonList(roleRepository.count()));
//                    break;
//            }
//
//            return trends;
//        }
//
//        // Helper methods to count based on filter
//        private long countAccountsToday() {
//            LocalDate today = LocalDate.now();
//            LocalDateTime startLDT = today.atStartOfDay();
//            LocalDateTime endLDT = today.atTime(LocalTime.MAX);
//            return accountRepository.countByCreatedAtBetween(startLDT, endLDT);
//        }
//
//        private long countInstructorsToday() {
//            LocalDate today = LocalDate.now();
//            LocalDateTime startLDT = today.atStartOfDay();
//            LocalDateTime endLDT = today.atTime(LocalTime.MAX);
//            return instructorRepository.countByCreatedAtBetween(startLDT, endLDT);
//        }
//
//        private long countStudentsToday() {
//            LocalDate today = LocalDate.now();
//            LocalDateTime startLDT = today.atStartOfDay();
//            LocalDateTime endLDT = today.atTime(LocalTime.MAX);
//            return studentRepository.countByCreatedAtBetween(startLDT, endLDT);
//        }
//
//        private long countCoursesToday() {
//            LocalDate today = LocalDate.now();
//            LocalDateTime startLDT = today.atStartOfDay();
//            LocalDateTime endLDT = today.atTime(LocalTime.MAX);
//            return courseRepository.countByCreatedAtBetween(startLDT, endLDT);
//        }
//
//        private long countCategoriesToday() {
//            LocalDate today = LocalDate.now();
//            LocalDateTime startLDT = today.atStartOfDay();
//            LocalDateTime endLDT = today.atTime(LocalTime.MAX);
//            return categoryRepository.countByCreatedAtBetween(startLDT, endLDT);
//        }
//
//        private long countRolesToday() {
//            return roleRepository.count();
//        }
//
//        private long countAccountsThisWeek() {
//            LocalDate today = LocalDate.now();
//            WeekFields weekFields = WeekFields.of(Locale.getDefault());
//            LocalDate startOfWeek = today.with(weekFields.dayOfWeek(), 1); // Monday
//            LocalDateTime startLDT = startOfWeek.atStartOfDay();
//            LocalDateTime endLDT = today.atTime(LocalTime.MAX);
//            return accountRepository.countByCreatedAtBetween(startLDT, endLDT);
//        }
//
//        private long countInstructorsThisWeek() {
//            LocalDate today = LocalDate.now();
//            WeekFields weekFields = WeekFields.of(Locale.getDefault());
//            LocalDate startOfWeek = today.with(weekFields.dayOfWeek(), 1); // Monday
//            LocalDateTime startLDT = startOfWeek.atStartOfDay();
//            LocalDateTime endLDT = today.atTime(LocalTime.MAX);
//            return instructorRepository.countByCreatedAtBetween(startLDT, endLDT);
//        }
//
//        private long countStudentsThisWeek() {
//            LocalDate today = LocalDate.now();
//            WeekFields weekFields = WeekFields.of(Locale.getDefault());
//            LocalDate startOfWeek = today.with(weekFields.dayOfWeek(), 1); // Monday
//            LocalDateTime startLDT = startOfWeek.atStartOfDay();
//            LocalDateTime endLDT = today.atTime(LocalTime.MAX);
//            return studentRepository.countByCreatedAtBetween(startLDT, endLDT);
//        }
//
//        private long countCoursesThisWeek() {
//            LocalDate today = LocalDate.now();
//            WeekFields weekFields = WeekFields.of(Locale.getDefault());
//            LocalDate startOfWeek = today.with(weekFields.dayOfWeek(), 1); // Monday
//            LocalDateTime startLDT = startOfWeek.atStartOfDay();
//            LocalDateTime endLDT = today.atTime(LocalTime.MAX);
//            return courseRepository.countByCreatedAtBetween(startLDT, endLDT);
//        }
//
//        private long countCategoriesThisWeek() {
//            LocalDate today = LocalDate.now();
//            WeekFields weekFields = WeekFields.of(Locale.getDefault());
//            LocalDate startOfWeek = today.with(weekFields.dayOfWeek(), 1); // Monday
//            LocalDateTime startLDT = startOfWeek.atStartOfDay();
//            LocalDateTime endLDT = today.atTime(LocalTime.MAX);
//            return categoryRepository.countByCreatedAtBetween(startLDT, endLDT);
//        }
//
//        private long countRolesThisWeek() {
//            return roleRepository.count();
//        }
//
//        private long countAccountsThisMonth() {
//            LocalDate today = LocalDate.now();
//            YearMonth currentMonth = YearMonth.from(today);
//            LocalDateTime startLDT = currentMonth.atDay(1).atStartOfDay();
//            LocalDateTime endLDT = today.atTime(LocalTime.MAX);
//            return accountRepository.countByCreatedAtBetween(startLDT, endLDT);
//        }
//
//        private long countInstructorsThisMonth() {
//            LocalDate today = LocalDate.now();
//            YearMonth currentMonth = YearMonth.from(today);
//            LocalDateTime startLDT = currentMonth.atDay(1).atStartOfDay();
//            LocalDateTime endLDT = today.atTime(LocalTime.MAX);
//            return instructorRepository.countByCreatedAtBetween(startLDT, endLDT);
//        }
//
//        private long countStudentsThisMonth() {
//            LocalDate today = LocalDate.now();
//            YearMonth currentMonth = YearMonth.from(today);
//            LocalDateTime startLDT = currentMonth.atDay(1).atStartOfDay();
//            LocalDateTime endLDT = today.atTime(LocalTime.MAX);
//            return studentRepository.countByCreatedAtBetween(startLDT, endLDT);
//        }
//
//        private long countCoursesThisMonth() {
//            LocalDate today = LocalDate.now();
//            YearMonth currentMonth = YearMonth.from(today);
//            LocalDateTime startLDT = currentMonth.atDay(1).atStartOfDay();
//            LocalDateTime endLDT = today.atTime(LocalTime.MAX);
//            return courseRepository.countByCreatedAtBetween(startLDT, endLDT);
//        }
//
//        private long countCategoriesThisMonth() {
//            LocalDate today = LocalDate.now();
//            YearMonth currentMonth = YearMonth.from(today);
//            LocalDateTime startLDT = currentMonth.atDay(1).atStartOfDay();
//            LocalDateTime endLDT = today.atTime(LocalTime.MAX);
//            return categoryRepository.countByCreatedAtBetween(startLDT, endLDT);
//        }
//
//        private long countRolesThisMonth() {
//            return roleRepository.count();
//        }
//
//        private long countAccountsThisYear() {
//            LocalDate today = LocalDate.now();
//            Year currentYear = Year.from(today);
//            LocalDateTime startLDT = currentYear.atDay(1).atStartOfDay();
//            LocalDateTime endLDT = today.atTime(LocalTime.MAX);
//            return accountRepository.countByCreatedAtBetween(startLDT, endLDT);
//        }
//
//        private long countInstructorsThisYear() {
//            LocalDate today = LocalDate.now();
//            Year currentYear = Year.from(today);
//            LocalDateTime startLDT = currentYear.atDay(1).atStartOfDay();
//            LocalDateTime endLDT = today.atTime(LocalTime.MAX);
//            return instructorRepository.countByCreatedAtBetween(startLDT, endLDT);
//        }
//
//        private long countStudentsThisYear() {
//            LocalDate today = LocalDate.now();
//            Year currentYear = Year.from(today);
//            LocalDateTime startLDT = currentYear.atDay(1).atStartOfDay();
//            LocalDateTime endLDT = today.atTime(LocalTime.MAX);
//            return studentRepository.countByCreatedAtBetween(startLDT, endLDT);
//        }
//
//        private long countCoursesThisYear() {
//            LocalDate today = LocalDate.now();
//            Year currentYear = Year.from(today);
//            LocalDateTime startLDT = currentYear.atDay(1).atStartOfDay();
//            LocalDateTime endLDT = today.atTime(LocalTime.MAX);
//            return courseRepository.countByCreatedAtBetween(startLDT, endLDT);
//        }
//
//        private long countCategoriesThisYear() {
//            LocalDate today = LocalDate.now();
//            Year currentYear = Year.from(today);
//            LocalDateTime startLDT = currentYear.atDay(1).atStartOfDay();
//            LocalDateTime endLDT = today.atTime(LocalTime.MAX);
//            return categoryRepository.countByCreatedAtBetween(startLDT, endLDT);
//        }
//
//        // Các phương thức đếm cho xu hướng
//
//        private List<String> getLast7Days() {
//            LocalDate today = LocalDate.now();
//            List<String> days = new ArrayList<>();
//            for (int i = 6; i >= 0; i--) {
//                LocalDate date = today.minusDays(i);
//                days.add(date.getDayOfWeek().toString());
//            }
//            return days;
//        }
//
//        private List<Long> getCountsLast7Days(String type) {
//            LocalDate today = LocalDate.now();
//            List<Long> counts = new ArrayList<>();
//            for (int i = 6; i >= 0; i--) {
//                LocalDate date = today.minusDays(i);
//                counts.add(getCountByDate(type, date));
//            }
//            return counts;
//        }
//
//        private List<String> getLast30Days() {
//            LocalDate today = LocalDate.now();
//            List<String> days = new ArrayList<>();
//            for (int i = 29; i >= 0; i--) {
//                LocalDate date = today.minusDays(i);
//                days.add(date.toString());
//            }
//            return days;
//        }
//
//        private List<Long> getCountsLast30Days(String type) {
//            LocalDate today = LocalDate.now();
//            List<Long> counts = new ArrayList<>();
//            for (int i = 29; i >= 0; i--) {
//                LocalDate date = today.minusDays(i);
//                counts.add(getCountByDate(type, date));
//            }
//            return counts;
//        }
//
//        private List<String> getLast12Months() {
//            YearMonth currentMonth = YearMonth.now();
//            List<String> months = new ArrayList<>();
//            for (int i = 11; i >= 0; i--) {
//                YearMonth month = currentMonth.minusMonths(i);
//                months.add(month.getMonth().toString());
//            }
//            return months;
//        }
//
//        private List<Long> getCountsLast12Months(String type) {
//            YearMonth currentMonth = YearMonth.now();
//            List<Long> counts = new ArrayList<>();
//            for (int i = 11; i >= 0; i--) {
//                YearMonth month = currentMonth.minusMonths(i);
//                counts.add(getCountByMonth(type, month));
//            }
//            return counts;
//        }
//
//        private long getCountByDate(String type, LocalDate date) {
//            LocalDateTime startLDT = date.atStartOfDay();
//            LocalDateTime endLDT = date.atTime(LocalTime.MAX);
//            return switch (type.toLowerCase()) {
//                case "account" -> accountRepository.countByCreatedAtBetween(startLDT, endLDT);
//                case "instructor" -> instructorRepository.countByCreatedAtBetween(startLDT, endLDT);
//                case "student" -> studentRepository.countByCreatedAtBetween(startLDT, endLDT);
//                case "course" -> courseRepository.countByCreatedAtBetween(startLDT, endLDT);
//                case "category" -> categoryRepository.countByCreatedAtBetween(startLDT, endLDT);
//                case "role" -> roleRepository.count();
//                default -> 0;
//            };
//        }
//
//        private long getCountByMonth(String type, YearMonth month) {
//            LocalDateTime startLDT = month.atDay(1).atStartOfDay();
//            LocalDateTime endLDT = month.atEndOfMonth().atTime(LocalTime.MAX);
//            return switch (type.toLowerCase()) {
//                case "account" -> accountRepository.countByCreatedAtBetween(startLDT, endLDT);
//                case "instructor" -> instructorRepository.countByCreatedAtBetween(startLDT, endLDT);
//                case "student" -> studentRepository.countByCreatedAtBetween(startLDT, endLDT);
//                case "course" -> courseRepository.countByCreatedAtBetween(startLDT, endLDT);
//                case "category" -> categoryRepository.countByCreatedAtBetween(startLDT, endLDT);
//                case "role" -> roleRepository.count();
//                default -> 0;
//            };
//        }
//-----------------------------------------------------------------------
package com.mytech.virtualcourse.services;

import com.mytech.virtualcourse.dtos.StatisticsDTO;
import com.mytech.virtualcourse.dtos.TrendsDTO;
import com.mytech.virtualcourse.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.temporal.WeekFields;
import java.util.*;

@Transactional
@Service
public class StatisticsService {

    @Autowired
    private AdminAccountRepository accountRepository;

    @Autowired
    private InstructorRepository instructorRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private RoleRepository roleRepository;

    /**
     * Get statistics based on filter (today, week, month, etc.)
     */
    public StatisticsDTO getStatistics(String filter, String model) {
        StatisticsDTO stats = new StatisticsDTO();

        if ("allTime".equalsIgnoreCase(filter)) {
            switch (model.toLowerCase()) {
                case "accounts":
                    stats.setAccounts(accountRepository.count());
                    break;
                case "instructors":
                    stats.setInstructors(instructorRepository.count());
                    break;
                case "students":
                    stats.setStudents(studentRepository.count());
                    break;
                case "courses":
                    stats.setCourses(courseRepository.count());
                    break;
                case "categories":
                    stats.setCategories(categoryRepository.count());
                    break;
                case "roles":
                    stats.setRoles(roleRepository.count());
                    break;
                default:
                    stats.setAccounts(accountRepository.count());
                    stats.setInstructors(instructorRepository.count());
                    stats.setStudents(studentRepository.count());
                    stats.setCourses(courseRepository.count());
                    stats.setCategories(categoryRepository.count());
                    stats.setRoles(roleRepository.count());
                    break;
            }
        } else {
            stats = handleTimeFilters(stats, filter, model);
        }

        return stats;
    }

    /**
     * Handles the time-based filters like today, week, month, etc.
     */
    private StatisticsDTO handleTimeFilters(StatisticsDTO stats, String filter, String model) {
        switch (filter.toLowerCase()) {
            case "today":
                stats.setAccounts(countAccountsToday());
                stats.setInstructors(countInstructorsToday());
                stats.setStudents(countStudentsToday());
                stats.setCourses(countCoursesToday());
                stats.setCategories(countCategoriesToday());
                stats.setRoles(countRolesToday());
                break;
            case "week":
                stats.setAccounts(countAccountsThisWeek());
                stats.setInstructors(countInstructorsThisWeek());
                stats.setStudents(countStudentsThisWeek());
                stats.setCourses(countCoursesThisWeek());
                stats.setCategories(countCategoriesThisWeek());
                stats.setRoles(countRolesThisWeek());
                break;
            case "month":
                stats.setAccounts(countAccountsThisMonth());
                stats.setInstructors(countInstructorsThisMonth());
                stats.setStudents(countStudentsThisMonth());
                stats.setCourses(countCoursesThisMonth());
                stats.setCategories(countCategoriesThisMonth());
                stats.setRoles(countRolesThisMonth());
                break;
            case "year":
                stats.setAccounts(countAccountsThisYear());
                stats.setInstructors(countInstructorsThisYear());
                stats.setStudents(countStudentsThisYear());
                stats.setCourses(countCoursesThisYear());
                stats.setCategories(countCategoriesThisYear());
                break;
            default:
                stats.setAccounts(accountRepository.count());
                stats.setInstructors(instructorRepository.count());
                stats.setStudents(studentRepository.count());
                stats.setCourses(courseRepository.count());
                stats.setCategories(categoryRepository.count());
                stats.setRoles(roleRepository.count());
                break;
        }
        return stats;
    }
    // Helper methods to count based on time filters
    // Helper method to get data for each model by date range
    private List<Long> getDataForModel(String model) {
        LocalDate startDate = getStartDateForModel(model);
        LocalDate today = LocalDate.now();

        // Fetch data for the specific model within the time range
        switch (model.toLowerCase()) {
            case "account":
                return Collections.singletonList(accountRepository.countByCreatedAtBetween(startDate.atStartOfDay(), today.atTime(LocalTime.MAX)));
            case "instructor":
                return Collections.singletonList(instructorRepository.countByCreatedAtBetween(startDate.atStartOfDay(), today.atTime(LocalTime.MAX)));
            case "student":
                return Collections.singletonList(studentRepository.countByCreatedAtBetween(startDate.atStartOfDay(), today.atTime(LocalTime.MAX)));
            case "course":
                return Collections.singletonList(courseRepository.countByCreatedAtBetween(startDate.atStartOfDay(), today.atTime(LocalTime.MAX)));
            case "category":
                return Collections.singletonList(categoryRepository.countByCreatedAtBetween(startDate.atStartOfDay(), today.atTime(LocalTime.MAX)));
            case "role":
                return Collections.singletonList(roleRepository.count());
            default:
                return Collections.emptyList();
        }}

    // Helper method to get the start date for a specific model
    private LocalDate getStartDateForModel(String model) {
        switch (model) {
            case "account":
                return accountRepository.getFirstCreatedDate();
            case "instructor":
                return instructorRepository.getFirstCreatedDate();
            case "student":
                return studentRepository.getFirstCreatedDate();
            case "course":
                return courseRepository.getFirstCreatedDate();
            case "category":
                return categoryRepository.getFirstCreatedDate();
            default:
                return LocalDate.now();
        }
    }
    private long countAccountsToday() {
        LocalDate today = LocalDate.now();
        LocalDateTime startLDT = today.atStartOfDay();
        LocalDateTime endLDT = today.atTime(LocalTime.MAX);
        return accountRepository.countByCreatedAtBetween(startLDT, endLDT);
    }

    private long countInstructorsToday() {
        LocalDate today = LocalDate.now();
        LocalDateTime startLDT = today.atStartOfDay();
        LocalDateTime endLDT = today.atTime(LocalTime.MAX);
        return instructorRepository.countByCreatedAtBetween(startLDT, endLDT);
    }

    private long countStudentsToday() {
        LocalDate today = LocalDate.now();
        LocalDateTime startLDT = today.atStartOfDay();
        LocalDateTime endLDT = today.atTime(LocalTime.MAX);
        return studentRepository.countByCreatedAtBetween(startLDT, endLDT);
    }

    private long countCoursesToday() {
        LocalDate today = LocalDate.now();
        LocalDateTime startLDT = today.atStartOfDay();
        LocalDateTime endLDT = today.atTime(LocalTime.MAX);
        return courseRepository.countByCreatedAtBetween(startLDT, endLDT);
    }

    private long countCategoriesToday() {
        LocalDate today = LocalDate.now();
        LocalDateTime startLDT = today.atStartOfDay();
        LocalDateTime endLDT = today.atTime(LocalTime.MAX);
        return categoryRepository.countByCreatedAtBetween(startLDT, endLDT);
    }

    private long countRolesToday() {
        return roleRepository.count();
    }

    private long countAccountsThisWeek() {
        LocalDate today = LocalDate.now();
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        LocalDate startOfWeek = today.with(weekFields.dayOfWeek(), 1); // Monday
        LocalDateTime startLDT = startOfWeek.atStartOfDay();
        LocalDateTime endLDT = today.atTime(LocalTime.MAX);
        return accountRepository.countByCreatedAtBetween(startLDT, endLDT);
    }

    private long countInstructorsThisWeek() {
        LocalDate today = LocalDate.now();
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        LocalDate startOfWeek = today.with(weekFields.dayOfWeek(), 1); // Monday
        LocalDateTime startLDT = startOfWeek.atStartOfDay();
        LocalDateTime endLDT = today.atTime(LocalTime.MAX);
        return instructorRepository.countByCreatedAtBetween(startLDT, endLDT);
    }

    private long countStudentsThisWeek() {
        LocalDate today = LocalDate.now();
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        LocalDate startOfWeek = today.with(weekFields.dayOfWeek(), 1); // Monday
        LocalDateTime startLDT = startOfWeek.atStartOfDay();
        LocalDateTime endLDT = today.atTime(LocalTime.MAX);
        return studentRepository.countByCreatedAtBetween(startLDT, endLDT);
    }

    private long countCoursesThisWeek() {
        LocalDate today = LocalDate.now();
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        LocalDate startOfWeek = today.with(weekFields.dayOfWeek(), 1); // Monday
        LocalDateTime startLDT = startOfWeek.atStartOfDay();
        LocalDateTime endLDT = today.atTime(LocalTime.MAX);
        return courseRepository.countByCreatedAtBetween(startLDT, endLDT);
    }

    private long countCategoriesThisWeek() {
        LocalDate today = LocalDate.now();
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        LocalDate startOfWeek = today.with(weekFields.dayOfWeek(), 1); // Monday
        LocalDateTime startLDT = startOfWeek.atStartOfDay();
        LocalDateTime endLDT = today.atTime(LocalTime.MAX);
        return categoryRepository.countByCreatedAtBetween(startLDT, endLDT);
    }

    private long countRolesThisWeek() {
        return roleRepository.count();
    }

    private long countAccountsThisMonth() {
        LocalDate today = LocalDate.now();
        YearMonth currentMonth = YearMonth.from(today);
        LocalDateTime startLDT = currentMonth.atDay(1).atStartOfDay();
        LocalDateTime endLDT = today.atTime(LocalTime.MAX);
        return accountRepository.countByCreatedAtBetween(startLDT, endLDT);
    }

    private long countInstructorsThisMonth() {
        LocalDate today = LocalDate.now();
        YearMonth currentMonth = YearMonth.from(today);
        LocalDateTime startLDT = currentMonth.atDay(1).atStartOfDay();
        LocalDateTime endLDT = today.atTime(LocalTime.MAX);
        return instructorRepository.countByCreatedAtBetween(startLDT, endLDT);
    }

    private long countStudentsThisMonth() {
        LocalDate today = LocalDate.now();
        YearMonth currentMonth = YearMonth.from(today);
        LocalDateTime startLDT = currentMonth.atDay(1).atStartOfDay();
        LocalDateTime endLDT = today.atTime(LocalTime.MAX);
        return studentRepository.countByCreatedAtBetween(startLDT, endLDT);
    }

    private long countCoursesThisMonth() {
        LocalDate today = LocalDate.now();
        YearMonth currentMonth = YearMonth.from(today);
        LocalDateTime startLDT = currentMonth.atDay(1).atStartOfDay();
        LocalDateTime endLDT = today.atTime(LocalTime.MAX);
        return courseRepository.countByCreatedAtBetween(startLDT, endLDT);
    }

    private long countCategoriesThisMonth() {
        LocalDate today = LocalDate.now();
        YearMonth currentMonth = YearMonth.from(today);
        LocalDateTime startLDT = currentMonth.atDay(1).atStartOfDay();
        LocalDateTime endLDT = today.atTime(LocalTime.MAX);
        return categoryRepository.countByCreatedAtBetween(startLDT, endLDT);
    }

    private long countRolesThisMonth() {
        return roleRepository.count();
    }

    private long countAccountsThisYear() {
        LocalDate today = LocalDate.now();
        Year currentYear = Year.from(today);
        LocalDateTime startLDT = currentYear.atDay(1).atStartOfDay();
        LocalDateTime endLDT = today.atTime(LocalTime.MAX);
        return accountRepository.countByCreatedAtBetween(startLDT, endLDT);
    }

    private long countInstructorsThisYear() {
        LocalDate today = LocalDate.now();
        Year currentYear = Year.from(today);
        LocalDateTime startLDT = currentYear.atDay(1).atStartOfDay();
        LocalDateTime endLDT = today.atTime(LocalTime.MAX);
        return instructorRepository.countByCreatedAtBetween(startLDT, endLDT);
    }

    private long countStudentsThisYear() {
        LocalDate today = LocalDate.now();
        Year currentYear = Year.from(today);
        LocalDateTime startLDT = currentYear.atDay(1).atStartOfDay();
        LocalDateTime endLDT = today.atTime(LocalTime.MAX);
        return studentRepository.countByCreatedAtBetween(startLDT, endLDT);
    }

    private long countCoursesThisYear() {
        LocalDate today = LocalDate.now();
        Year currentYear = Year.from(today);
        LocalDateTime startLDT = currentYear.atDay(1).atStartOfDay();
        LocalDateTime endLDT = today.atTime(LocalTime.MAX);
        return courseRepository.countByCreatedAtBetween(startLDT, endLDT);
    }

    private long countCategoriesThisYear() {
        LocalDate today = LocalDate.now();
        Year currentYear = Year.from(today);
        LocalDateTime startLDT = currentYear.atDay(1).atStartOfDay();
        LocalDateTime endLDT = today.atTime(LocalTime.MAX);
        return categoryRepository.countByCreatedAtBetween(startLDT, endLDT);
    }


    // **Trend methods**
    public TrendsDTO getTrends(String filter) {
        TrendsDTO trends = new TrendsDTO();

        if ("allTime".equalsIgnoreCase(filter)) {
            trends.setDates(Collections.singletonList("All Time"));
            trends.setAccounts(Collections.singletonList(accountRepository.count()));
            trends.setInstructors(Collections.singletonList(instructorRepository.count()));
            trends.setStudents(Collections.singletonList(studentRepository.count()));
            trends.setCourses(Collections.singletonList(courseRepository.count()));
            trends.setCategories(Collections.singletonList(categoryRepository.count()));
            trends.setRoles(Collections.singletonList(roleRepository.count()));
        } else {
            trends = handleTrendsTimeFilters(trends, filter);
        }

        return trends;
    }

    private TrendsDTO handleTrendsTimeFilters(TrendsDTO trends, String filter) {
        switch (filter.toLowerCase()) {
            case "today":
                trends.setDates(Collections.singletonList(LocalDate.now().toString()));
                trends.setAccounts(Collections.singletonList(countAccountsToday()));
                trends.setInstructors(Collections.singletonList(countInstructorsToday()));
                trends.setStudents(Collections.singletonList(countStudentsToday()));
                trends.setCourses(Collections.singletonList(countCoursesToday()));
                trends.setCategories(Collections.singletonList(countCategoriesToday()));
                trends.setRoles(Collections.singletonList(countRolesToday()));
                break;
            case "week":
                trends.setDates(getLast7Days());
                trends.setAccounts(getCountsLast7Days("account"));
                trends.setInstructors(getCountsLast7Days("instructor"));
                trends.setStudents(getCountsLast7Days("student"));
                trends.setCourses(getCountsLast7Days("course"));
                trends.setCategories(getCountsLast7Days("category"));
                trends.setRoles(getCountsLast7Days("role"));
                break;
            case "month":
                trends.setDates(getLast30Days());
                trends.setAccounts(getCountsLast30Days("account"));
                trends.setInstructors(getCountsLast30Days("instructor"));
                trends.setStudents(getCountsLast30Days("student"));
                trends.setCourses(getCountsLast30Days("course"));
                trends.setCategories(getCountsLast30Days("category"));
                trends.setRoles(getCountsLast30Days("role"));
                break;
            case "year":
                trends.setDates(getLast12Months());
                trends.setAccounts(getCountsLast12Months("account"));
                trends.setInstructors(getCountsLast12Months("instructor"));
                trends.setStudents(getCountsLast12Months("student"));
                trends.setCourses(getCountsLast12Months("course"));
                trends.setCategories(getCountsLast12Months("category"));
                trends.setRoles(getCountsLast12Months("role"));
                break;
            default:
                trends.setDates(Collections.singletonList("All Time"));
                trends.setAccounts(Collections.singletonList(accountRepository.count()));
                trends.setInstructors(Collections.singletonList(instructorRepository.count()));
                trends.setStudents(Collections.singletonList(studentRepository.count()));
                trends.setCourses(Collections.singletonList(courseRepository.count()));
                trends.setCategories(Collections.singletonList(categoryRepository.count()));
                trends.setRoles(Collections.singletonList(roleRepository.count()));
                break;
        }
        return trends;
    }

    // Helper methods for trends counting (getLast7Days, getCountsLast7Days, etc.)
    private List<String> getLast7Days() {
        LocalDate today = LocalDate.now();
        List<String> days = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            days.add(date.toString());
        }
        return days;
    }

    private List<Long> getCountsLast7Days(String type) {
        LocalDate today = LocalDate.now();
        List<Long> counts = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            counts.add(getCountByDate(type, date));
        }
        return counts;
    }

    private List<String> getLast30Days() {
        LocalDate today = LocalDate.now();
        List<String> days = new ArrayList<>();
        for (int i = 29; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            days.add(date.toString());
        }
        return days;
    }

    private List<Long> getCountsLast30Days(String type) {
        LocalDate today = LocalDate.now();
        List<Long> counts = new ArrayList<>();
        for (int i = 29; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            counts.add(getCountByDate(type, date));
        }
        return counts;
    }

    private List<String> getLast12Months() {
        YearMonth currentMonth = YearMonth.now();
        List<String> months = new ArrayList<>();
        for (int i = 11; i >= 0; i--) {
            YearMonth month = currentMonth.minusMonths(i);
            months.add(month.toString());
        }
        return months;
    }

    private List<Long> getCountsLast12Months(String type) {
        YearMonth currentMonth = YearMonth.now();
        List<Long> counts = new ArrayList<>();
        for (int i = 11; i >= 0; i--) {
            YearMonth month = currentMonth.minusMonths(i);
            counts.add(getCountByMonth(type, month));
        }
        return counts;
    }

    private long getCountByDate(String type, LocalDate date) {
        LocalDateTime startLDT = date.atStartOfDay();
        LocalDateTime endLDT = date.atTime(LocalTime.MAX);
        return switch (type.toLowerCase()) {
            case "account" -> accountRepository.countByCreatedAtBetween(startLDT, endLDT);
            case "instructor" -> instructorRepository.countByCreatedAtBetween(startLDT, endLDT);
            case "student" -> studentRepository.countByCreatedAtBetween(startLDT, endLDT);
            case "course" -> courseRepository.countByCreatedAtBetween(startLDT, endLDT);
            case "category" -> categoryRepository.countByCreatedAtBetween(startLDT, endLDT);
            case "role" -> roleRepository.count();
            default -> 0;
        };
    }

    private long getCountByMonth(String type, YearMonth month) {
        LocalDateTime startLDT = month.atDay(1).atStartOfDay();
        LocalDateTime endLDT = month.atEndOfMonth().atTime(LocalTime.MAX);
        return switch (type.toLowerCase()) {
            case "account" -> accountRepository.countByCreatedAtBetween(startLDT, endLDT);
            case "instructor" -> instructorRepository.countByCreatedAtBetween(startLDT, endLDT);
            case "student" -> studentRepository.countByCreatedAtBetween(startLDT, endLDT);
            case "course" -> courseRepository.countByCreatedAtBetween(startLDT, endLDT);
            case "category" -> categoryRepository.countByCreatedAtBetween(startLDT, endLDT);
            case "role" -> roleRepository.count();
            default -> 0;
        };
    }
}

