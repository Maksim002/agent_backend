package com.example.beckend_agent.controller

import com.example.beckend_agent.model.Step
import com.example.beckend_agent.model.StepService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/steps")
class StepController(private val stepService: StepService) {

    @GetMapping
    fun getSteps(): List<Step> = stepService.getAllSteps()

    @GetMapping("/{id}")
    fun getStep(@PathVariable id: Int): ResponseEntity<Step> {
        val step = stepService.getStepById(id)
        return if (step != null) {
            ResponseEntity.ok(step)
        } else {
            ResponseEntity.notFound().build()
        }
    }
}
