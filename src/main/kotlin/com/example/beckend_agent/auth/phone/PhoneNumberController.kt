package com.example.beckend_agent.auth.phone

import com.example.beckend_agent.bd.model.PhoneNumber
import com.example.beckend_agent.bd.repository.PhoneNumberRepository
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/phones")
class PhoneNumberController(
    private val phoneNumberRepository: PhoneNumberRepository
) {

    @PostMapping
    fun savePhoneNumber(@RequestBody phoneNumber: PhoneNumber): PhoneNumber {
        return phoneNumberRepository.save(phoneNumber)
    }

    @GetMapping
    fun getAll(): List<PhoneNumber> = phoneNumberRepository.findAll()
}