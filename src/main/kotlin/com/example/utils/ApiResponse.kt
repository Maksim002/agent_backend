package com.example.utils

import com.fasterxml.jackson.annotation.JsonInclude

data class ApiResponse<T>(
    val status: String,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    val data: T? = null,

    @field:JsonInclude(JsonInclude.Include.NON_NULL)
    val message: String? = null
)