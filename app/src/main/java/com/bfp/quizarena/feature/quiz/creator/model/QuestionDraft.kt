package com.bfp.quizarena.feature.quiz.creator.model

import com.bfp.quizarena.feature.quiz.model.QuestionType
import java.util.UUID

data class QuestionDraft (
    val type: QuestionType,
    val id: UUID = UUID.randomUUID(),
    val text: String = "",
    val options: List<AnswerDraft> = emptyList(),
)