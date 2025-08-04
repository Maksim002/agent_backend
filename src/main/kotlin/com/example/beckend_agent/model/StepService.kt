package com.example.beckend_agent.model

import org.springframework.stereotype.Service

@Service
class StepService {

    private val steps = listOf(
        Step(1, "Первый этап: подготовка"),
        Step(2, "Второй этап: сбор данных"),
        Step(3, "Третий этап: анализ"),
        Step(4, "Четвертый этап: отчет")
    )

    fun getAllSteps(): List<Step> = steps

    fun getStepById(id: Int): Step? = steps.find { it.id == id }
}
