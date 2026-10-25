package com.bfp.quizarena.feature.quiz.creator

import com.bfp.quizarena.feature.quiz.creator.model.QuizDraft

data class CreateQuizUiState(
    val quiz: QuizDraft = QuizDraft(),
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)