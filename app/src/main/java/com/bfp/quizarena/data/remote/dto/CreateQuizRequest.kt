package com.bfp.quizarena.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CreateQuizRequest(
    val title: String,
    val description: String,
    val questions: List<CreateQuestionRequest>
)
@Serializable
data class CreateQuestionRequest(
    val text: String,
    val options: List<CreateAnswerRequest>
)
@Serializable
data class CreateAnswerRequest(
    val text: String,
    val correct: Boolean
)


