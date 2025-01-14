// package com.mytech.virtualcourse.services;

// import com.mytech.virtualcourse.dtos.BankAccountDTO;
// import com.mytech.virtualcourse.entities.BankAccount;
// import com.mytech.virtualcourse.entities.Instructor;
// import com.mytech.virtualcourse.enums.BankName;
// import com.mytech.virtualcourse.exceptions.ResourceNotFoundException;
// import com.mytech.virtualcourse.mappers.BankAccountMapper;
// import com.mytech.virtualcourse.repositories.BankAccountRepository;
// import com.mytech.virtualcourse.repositories.InstructorRepository;
// import io.swagger.v3.oas.annotations.Operation;
// import io.swagger.v3.oas.annotations.responses.ApiResponse;
// import io.swagger.v3.oas.annotations.responses.ApiResponses;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;

// import java.util.List;
// import java.util.stream.Collectors;

// @Service
// public class BankAccountService {
//     private static final Logger logger = LoggerFactory.getLogger(BankAccountService.class);

//     @Autowired
//     private BankAccountRepository bankAccountRepository;

//     @Autowired
//     private InstructorRepository instructorRepository;

//     @Autowired
//     private BankAccountMapper bankAccountMapper;

//     public List<BankAccountDTO> getAllBankAccounts() {
//         return bankAccountRepository.findAll()
//                 .stream()
//                 .map(bankAccountMapper::ToDTO)
//                 .collect(Collectors.toList());
//     }

//     public BankAccountDTO getBankAccountById(Long id) {
//         BankAccount bankAccount = bankAccountRepository.findById(id)
//                 .orElseThrow(() -> new ResourceNotFoundException("Bank account not found with id: " + id));
//         return bankAccountMapper.ToDTO(bankAccount);
//     }
//     @Operation(summary = "Create a new bank account for an instructor")
//     @ApiResponses(value = {
//             @ApiResponse(responseCode = "201", description = "Bank account created successfully"),
//             @ApiResponse(responseCode = "404", description = "Instructor not found"),
//             @ApiResponse(responseCode = "400", description = "Invalid input data")
//     })
//     public BankAccountDTO createBankAccount(BankAccountDTO dto) {
//         logger.info("Creating bank account for instructorId={}", dto.getInstructorId());
//         Instructor instructor = instructorRepository.findById(dto.getInstructorId())
//                 .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with id: " + dto.getInstructorId()));
//         BankAccount bankAccount = bankAccountMapper.ToEntity(dto);
//         bankAccount.setInstructor(instructor);
//         BankAccount savedBankAccount = bankAccountRepository.save(bankAccount);
//         logger.info("Bank account created with id={}", savedBankAccount.getId());
//         return bankAccountMapper.ToDTO(savedBankAccount);
//     }

//     public BankAccountDTO updateBankAccount(Long id, BankAccountDTO dto) {
//         BankAccount existingAccount = bankAccountRepository.findById(id)
//                 .orElseThrow(() -> new ResourceNotFoundException("Bank account not found with id: " + id));

//         existingAccount.setBankName(dto.getBankName());
//         existingAccount.setAccountNumber(dto.getAccountNumber());
//         existingAccount.setAccountHolderName(dto.getAccountHolderName());
//         BankAccount updatedAccount = bankAccountRepository.save(existingAccount);
//         return bankAccountMapper.ToDTO(updatedAccount);
//     }

//     public void deleteBankAccount(Long id) {
//         if (!bankAccountRepository.existsById(id)) {
//             throw new ResourceNotFoundException("Bank account not found with id: " + id);
//         }
//         bankAccountRepository.deleteById(id);
//     }

//     public List<BankAccountDTO> findByBankName(BankName bankName) {
//         // TODO Auto-generated method stub
//         throw new UnsupportedOperationException("Unimplemented method 'findByBankName'");
//     }
// }
