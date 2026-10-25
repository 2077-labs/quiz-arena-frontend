package com.bfp.quizarena.feature.quiz.creator.model

import java.util.UUID

data class AnswerDraft(
    val id: UUID = UUID.randomUUID(),
    val text: String = "",
    val isCorrect: Boolean = false,
    )