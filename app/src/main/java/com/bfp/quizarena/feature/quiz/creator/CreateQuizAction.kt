package com.bfp.quizarena.feature.quiz.creator

import com.bfp.quizarena.feature.quiz.model.QuestionType
import java.util.UUID

sealed interface CreateQuizAction
{
    data class TitleChanged(val title: String) : CreateQuizAction
    data class DescriptionChanged(val description: String) : CreateQuizAction
    data class AddQuestion(val questionType: QuestionType) : CreateQuizAction
    data class QuestionTextChanged(val questionId: UUID, val text: String) : CreateQuizAction
    data class AnswerTextChanged(val questionId: UUID, val answerId: UUID, val text: String) : CreateQuizAction
    data class AnswerCorrectnessChanged(val questionId: UUID, val answerId: UUID, val isCorrect: Boolean) : CreateQuizAction
    data class RemoveQuestion(val questionId: UUID) : CreateQuizAction
    data class RemoveAnswer(val questionId: UUID, val answerId: UUID) : CreateQuizAction
    data class AddAnswer(val questionId: UUID) : CreateQuizAction
    object SaveQuiz : CreateQuizAction
}