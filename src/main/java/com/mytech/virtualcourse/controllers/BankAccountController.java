// package com.mytech.virtualcourse.controllers;

// import com.mytech.virtualcourse.dtos.BankAccountDTO;
// import com.mytech.virtualcourse.enums.BankName;
// import com.mytech.virtualcourse.services.BankAccountService;

// import jakarta.validation.Valid;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// import java.util.List;

// @RestController
// @RequestMapping("/api/bank-accounts")
// public class BankAccountController {

//     @Autowired
//     private BankAccountService bankAccountService;

//     @GetMapping
//     public ResponseEntity<List<BankAccountDTO>> getAllBankAccounts() {
//         return ResponseEntity.ok(bankAccountService.getAllBankAccounts());
//     }

//     @GetMapping("/{id}")
//     public ResponseEntity<BankAccountDTO> getBankAccountById(@PathVariable Long id) {
//         return ResponseEntity.ok(bankAccountService.getBankAccountById(id));
//     }

//     @PostMapping
//     public ResponseEntity<BankAccountDTO> createBankAccount(@Valid @RequestBody BankAccountDTO dto) {
//         return ResponseEntity.ok(bankAccountService.createBankAccount(dto));
//     }

//     @PutMapping("/{id}")
//     public ResponseEntity<BankAccountDTO> updateBankAccount(@Valid @PathVariable Long id, @RequestBody BankAccountDTO dto) {
//         return ResponseEntity.ok(bankAccountService.updateBankAccount(id, dto));
//     }

//     @DeleteMapping("/{id}")
//     public ResponseEntity<Void> deleteBankAccount(@PathVariable Long id) {
//         bankAccountService.deleteBankAccount(id);
//         return ResponseEntity.ok().build();
//     }
//     @GetMapping
// public List<BankAccountDTO> searchBankAccounts(@RequestParam(required = false) BankName bankName) {
//     if (bankName != null) {
//         return bankAccountService.findByBankName(bankName);
//     }
//     return bankAccountService.getAllBankAccounts();
// }
// }
