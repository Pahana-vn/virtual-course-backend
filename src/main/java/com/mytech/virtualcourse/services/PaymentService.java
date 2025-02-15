package com.mytech.virtualcourse.services;

import com.mytech.virtualcourse.configs.VnPayConfig;
import com.mytech.virtualcourse.entities.Course;
import com.mytech.virtualcourse.entities.LearningProgress;
import com.mytech.virtualcourse.entities.Payment;
import com.mytech.virtualcourse.entities.Student;
import com.mytech.virtualcourse.enums.PaymentMethod;
import com.mytech.virtualcourse.enums.PaymentStatus;
import com.mytech.virtualcourse.repositories.CourseRepository;
import com.mytech.virtualcourse.repositories.LearningProgressRepository;
import com.mytech.virtualcourse.repositories.PaymentRepository;
import com.mytech.virtualcourse.repositories.StudentRepository;
import com.mytech.virtualcourse.configs.VnPayConfig;
import com.mytech.virtualcourse.security.JwtUtil;
import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private LearningProgressRepository learningProgressRepository;

    @Autowired
    private APIContext apiContext;

    @Autowired
    private VnPayConfig vnPayConfig;

    @Autowired
    private StudentService studentService;

    @Autowired
    private JwtUtil jwtUtil;

    // -------------------- PAYPAL -------------------------
    public String initiatePaypalPayment(Long courseId, HttpServletRequest request) throws Exception {
        Long studentId = getStudentIdFromRequest(request); // Lấy studentId từ JWT
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        BigDecimal amount = course.getBasePrice();
        if (amount == null) {
            throw new RuntimeException("Course price not found");
        }

        Payment dbPayment = new Payment();
        dbPayment.setAmount(amount);
        dbPayment.setPaymentDate(Timestamp.from(Instant.now()));
        dbPayment.setPaymentMethod(PaymentMethod.PAYPAL);
        dbPayment.setStatus(PaymentStatus.Pending);
        dbPayment.setStudent(student);

        List<Course> singleCourseList = new ArrayList<>();
        singleCourseList.add(course);
        dbPayment.setCourses(singleCourseList);

        dbPayment = paymentRepository.save(dbPayment);

        String cancelUrl = "http://localhost:3000/cancel";
        String successUrl = "http://localhost:3000/success";

        com.paypal.api.payments.Payment createdPayment = createPayPalPayment(
                amount,
                "USD",
                "paypal",
                "sale",
                "Payment for course: " + course.getTitleCourse(),
                cancelUrl,
                successUrl
        );

        dbPayment.setPaypalPaymentId(createdPayment.getId());
        paymentRepository.save(dbPayment);

        for (Links link : createdPayment.getLinks()) {
            if ("approval_url".equalsIgnoreCase(link.getRel())) {
                return link.getHref();
            }
        }
        throw new RuntimeException("No approval URL returned by PayPal");
    }


    public String initiatePaypalPaymentForMultipleCourses(List<Long> courseIds, HttpServletRequest request) throws Exception {
        Long studentId = getStudentIdFromRequest(request); // Lấy studentId từ JWT
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        List<Course> courses = courseRepository.findAllById(courseIds);
        if (courses.isEmpty()) {
            throw new RuntimeException("No courses found for given IDs");
        }

        BigDecimal totalAmount = courses.stream()
                .map(course -> {
                    BigDecimal price = course.getBasePrice();
                    if (price == null) {
                        throw new RuntimeException("Course price not found for courseId: " + course.getId());
                    }
                    return price;
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Payment dbPayment = new Payment();
        dbPayment.setAmount(totalAmount);
        dbPayment.setPaymentDate(Timestamp.from(Instant.now()));
        dbPayment.setPaymentMethod(PaymentMethod.PAYPAL);
        dbPayment.setStatus(PaymentStatus.Pending);
        dbPayment.setStudent(student);
        dbPayment.setCourses(new ArrayList<>(courses));

        dbPayment = paymentRepository.save(dbPayment);

        String cancelUrl = "http://localhost:3000/cancel";
        String successUrl = "http://localhost:3000/success";

        com.paypal.api.payments.Payment createdPayment = createPayPalPayment(
                totalAmount,
                "USD",
                "paypal",
                "sale",
                "Payment for multiple courses",
                cancelUrl,
                successUrl
        );

        dbPayment.setPaypalPaymentId(createdPayment.getId());
        paymentRepository.save(dbPayment);

        for (Links link : createdPayment.getLinks()) {
            if ("approval_url".equalsIgnoreCase(link.getRel())) {
                return link.getHref();
            }
        }

        throw new RuntimeException("No approval URL returned by PayPal");
    }

    private Long getStudentIdFromRequest(HttpServletRequest request) {
        String jwt = parseJwt(request);
        if (jwt != null && jwtUtil.validateJwtToken(jwt)) {
            return jwtUtil.getStudentIdFromJwtToken(jwt);  // Lấy studentId từ JWT
        } else {
            throw new RuntimeException("Student not authenticated");
        }
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);  // Lấy JWT token từ header
        }
        return null;
    }

    public Payment completePaypalPayment(String paymentId, String payerId) throws PayPalRESTException {
        com.paypal.api.payments.Payment payment = new com.paypal.api.payments.Payment();
        payment.setId(paymentId);

        PaymentExecution execution = new PaymentExecution();
        execution.setPayerId(payerId);

        com.paypal.api.payments.Payment executedPayment = payment.execute(apiContext, execution);
        System.out.println("PayPal executed payment state: " + executedPayment.getState());

        Payment dbPayment = paymentRepository.findByPaypalPaymentId(paymentId)
                .orElseThrow(() -> new RuntimeException("No matching Payment found for paypalPaymentId"));

        Student student = dbPayment.getStudent(); // ✅ Lấy student từ Payment

        String state = executedPayment.getState();
        if ("approved".equalsIgnoreCase(state) || "completed".equalsIgnoreCase(state)) {
            dbPayment.setStatus(PaymentStatus.Completed);
            paymentRepository.save(dbPayment);

            if (student.getCourses() == null) {
                student.setCourses(new ArrayList<>());
            }

            List<Course> purchasedCourses = dbPayment.getCourses();
            for (Course c : purchasedCourses) {
                if (!student.getCourses().contains(c)) {
                    student.getCourses().add(c);
                }
            }
            studentRepository.save(student);

            // ✅ Gọi enrollStudentToCourse sau khi cập nhật student
            for (Course c : purchasedCourses) {
                studentService.enrollStudentToCourse(student.getId(), c.getId());
            }
        } else {
            dbPayment.setStatus(PaymentStatus.Failed);
            paymentRepository.save(dbPayment);
        }

        return dbPayment;
    }


    private com.paypal.api.payments.Payment createPayPalPayment(
            BigDecimal total, String currency, String method, String intent,
            String description, String cancelUrl, String successUrl) throws PayPalRESTException {

        Amount amount = new Amount();
        amount.setCurrency(currency);
        amount.setTotal(String.format("%.2f", total));

        Transaction transaction = new Transaction();
        transaction.setDescription(description);
        transaction.setAmount(amount);

        Payer payer = new Payer();
        payer.setPaymentMethod(method);

        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl(cancelUrl);
        redirectUrls.setReturnUrl(successUrl);

        com.paypal.api.payments.Payment payment = new com.paypal.api.payments.Payment();
        payment.setIntent(intent);
        payment.setPayer(payer);
        payment.setTransactions(Collections.singletonList(transaction));
        payment.setRedirectUrls(redirectUrls);

        return payment.create(apiContext);
    }

    // -------------------- VNPAY -------------------------
    public String initiateVnPayPayment(Long courseId, HttpServletRequest request) throws Exception {
        Long studentId = getStudentIdFromRequest(request); // Lấy studentId từ JWT
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        BigDecimal amount = course.getBasePrice();
        if (amount == null) {
            throw new RuntimeException("Course price not found");
        }

        Payment dbPayment = new Payment();
        dbPayment.setAmount(amount);
        dbPayment.setPaymentDate(Timestamp.from(Instant.now()));
        dbPayment.setPaymentMethod(PaymentMethod.VNPAY);
        dbPayment.setStatus(PaymentStatus.Pending);
        dbPayment.setStudent(student);

        List<Course> singleCourseList = new ArrayList<>();
        singleCourseList.add(course); // course là managed entity
        dbPayment.setCourses(singleCourseList);

        dbPayment = paymentRepository.save(dbPayment);

        String paymentUrl = createVnpayPaymentUrl(dbPayment);
        return paymentUrl;
    }


    public String initiateVnPayPaymentForMultipleCourses(List<Long> courseIds, HttpServletRequest request) throws Exception {
        Long studentId = getStudentIdFromRequest(request); // Lấy studentId từ JWT
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        List<Course> courses = courseRepository.findAllById(courseIds);
        if (courses.isEmpty()) {
            throw new RuntimeException("No courses found for given IDs");
        }

        BigDecimal totalAmount = courses.stream()
                .map(course -> {
                    BigDecimal price = course.getBasePrice();
                    if (price == null) {
                        throw new RuntimeException("Course price not found for courseId: " + course.getId());
                    }
                    return price;
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Payment dbPayment = new Payment();
        dbPayment.setAmount(totalAmount);
        dbPayment.setPaymentDate(Timestamp.from(Instant.now()));
        dbPayment.setPaymentMethod(PaymentMethod.VNPAY);
        dbPayment.setStatus(PaymentStatus.Pending);
        dbPayment.setStudent(student);
        dbPayment.setCourses(new ArrayList<>(courses));

        dbPayment = paymentRepository.save(dbPayment);

        String paymentUrl = createVnpayPaymentUrl(dbPayment);
        return paymentUrl;
    }

    private String createVnpayPaymentUrl(Payment payment) throws UnsupportedEncodingException {
        String vnp_TmnCode = vnPayConfig.getTmnCode();
        String vnp_HashSecret = vnPayConfig.getHashSecret();
        String vnp_PayUrl = vnPayConfig.getBaseUrl();
        String vnp_ReturnUrl = vnPayConfig.getReturnUrl();
        String vnp_Version = vnPayConfig.getVersion();
        String vnp_Command = vnPayConfig.getCommand();
        String vnp_Locale = vnPayConfig.getLocale();
        String vnp_CurrCode = vnPayConfig.getCurrCode();
        String vnp_OrderType = "other";

        long amountVND = payment.getAmount().longValue();
        String vnp_Amount = String.valueOf(amountVND * 100);
        String vnp_TxnRef = String.valueOf(payment.getId());
        String vnp_OrderInfo = URLEncoder.encode("Thanh toan don hang", StandardCharsets.US_ASCII.toString());

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        String vnp_IpAddr = "116.102.86.199";

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", vnp_Amount);
        vnp_Params.put("vnp_CurrCode", vnp_CurrCode);
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", vnp_OrderInfo);
        vnp_Params.put("vnp_OrderType", vnp_OrderType);
        vnp_Params.put("vnp_ReturnUrl", vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);
        vnp_Params.put("vnp_Locale", vnp_Locale);
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        for (String fieldName : fieldNames) {
            String fieldValue = vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                if (hashData.length() > 0) {
                    hashData.append('&');
                }
                hashData.append(fieldName).append('=').append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));

                if (query.length() > 0) {
                    query.append('&');
                }
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
            }
        }

        String vnp_SecureHash = hmacSHA512(vnp_HashSecret, hashData.toString());
        query.append("&vnp_SecureHash=").append(vnp_SecureHash);

        return vnp_PayUrl + "?" + query.toString();
    }

    private String hmacSHA512(String key, String data) {
        try {
            Mac sha512_HMAC = Mac.getInstance("HmacSHA512");
            SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            sha512_HMAC.init(secret_key);
            byte[] hash = sha512_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(2 * hash.length);
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error while generating HMAC-SHA512", e);
        }
    }

    public boolean verifyVnpayResponse(Map<String, String> params) {
        String vnp_SecureHash = params.get("vnp_SecureHash");
        if (vnp_SecureHash == null) return false;

        Map<String, String> filtered = new HashMap<>(params);
        filtered.remove("vnp_SecureHash");
        filtered.remove("vnp_SecureHashType");

        List<String> fieldNames = new ArrayList<>(filtered.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        for (String fieldName : fieldNames) {
            String fieldValue = filtered.get(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty()) {
                if (hashData.length() > 0) {
                    hashData.append('&');
                }
                hashData.append(fieldName).append('=').append(fieldValue);
            }
        }

        String calculatedHash = hmacSHA512(vnPayConfig.getHashSecret(), hashData.toString());
        return calculatedHash.equals(vnp_SecureHash);
    }

    public void handleVnpayReturn(Map<String, String> params) {
        System.out.println("VNPAY RETURN PARAMS: " + params);

        String txnRef = params.get("vnp_TxnRef");
        Long paymentId = Long.valueOf(txnRef);

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found with ID: " + paymentId));

        boolean valid = verifyVnpayResponse(params);
        System.out.println("Verification Valid: " + valid);

        if (!valid) {
            payment.setStatus(PaymentStatus.Failed);
            paymentRepository.save(payment);
            return;
        }

        String transactionStatus = params.get("vnp_TransactionStatus");
        System.out.println("Transaction Status: " + transactionStatus);

        if ("00".equals(transactionStatus)) {
            payment.setStatus(PaymentStatus.Completed);
            paymentRepository.save(payment);

            Student student = payment.getStudent();
            if (student.getCourses() == null) {
                student.setCourses(new ArrayList<>());
            }

            List<Course> purchasedCourses = payment.getCourses();
            for (Course c : purchasedCourses) {
                if (!student.getCourses().contains(c)) {
                    student.getCourses().add(c);
                }
            }
            studentRepository.save(student);

            // Gọi enrollStudentToCourse
            for (Course c : purchasedCourses) {
                studentService.enrollStudentToCourse(student.getId(), c.getId());
            }
        } else {
            payment.setStatus(PaymentStatus.Failed);
            paymentRepository.save(payment);
        }
    }
}
