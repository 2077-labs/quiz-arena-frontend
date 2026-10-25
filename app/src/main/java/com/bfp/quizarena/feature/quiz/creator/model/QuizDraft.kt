package com.bfp.quizarena.feature.quiz.creator.model

data class QuizDraft(
    val title: String = "",
    val description: String = "",
    val questions: List<QuestionDraft> = emptyList()
)