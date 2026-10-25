package com.bfp.quizarena.feature.quiz.creator

import androidx.lifecycle.ViewModel
import com.bfp.quizarena.feature.quiz.creator.model.AnswerDraft
import com.bfp.quizarena.feature.quiz.creator.model.QuestionDraft
import com.bfp.quizarena.feature.quiz.creator.model.QuizDraft
import com.bfp.quizarena.feature.quiz.model.QuestionType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import com.bfp.quizarena.data.repository.QuizRepository
import com.bfp.quizarena.data.remote.dto.CreateAnswerRequest
import com.bfp.quizarena.data.remote.dto.CreateQuestionRequest
import com.bfp.quizarena.data.remote.dto.CreateQuizRequest
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class CreateQuizViewModel(
    private val quizRepository: QuizRepository
) : ViewModel()
{
    private val _uiState = MutableStateFlow<CreateQuizUiState>(CreateQuizUiState())
    val uiState: StateFlow<CreateQuizUiState> = _uiState.asStateFlow()

    fun onAction(action: CreateQuizAction) {
        when(action)
        {
            is CreateQuizAction.TitleChanged -> {
                _uiState.update { state ->
                    state.copy(
                        quiz = state.quiz.copy(
                            title = action.title
                        ),
                        errorMessage = null
                    )
                }
            }
            is CreateQuizAction.DescriptionChanged -> {
                _uiState.update { state ->
                    state.copy(
                        quiz = state.quiz.copy(
                            description = action.description
                        ),
                        errorMessage = null
                    )
                }
            }
            is CreateQuizAction.AddQuestion -> {
                _uiState.update { state ->
                    val newQuestion = QuestionDraft(
                        type = action.questionType,
                        options = listOf(AnswerDraft(), AnswerDraft())
                    )
                    val newQuestionList = state.quiz.questions + newQuestion
                    state.copy(
                        quiz = state.quiz.copy(
                            questions = newQuestionList
                        ),
                        errorMessage = null
                    )
                }
            }
            is CreateQuizAction.QuestionTextChanged -> {
                _uiState.update { state ->
                   val updatedQuestions = state.quiz.questions.map { question ->
                        if (action.questionId == question.id)
                        {
                            question.copy(text = action.text)
                        } else {
                            question
                        }
                    }
                    state.copy(
                        quiz = state.quiz.copy(
                            questions = updatedQuestions
                        ),
                        errorMessage = null
                    )
                }
            }
            is CreateQuizAction.AnswerTextChanged -> {
                _uiState.update { state ->
                    val updatedQuestions = state.quiz.questions.map { question ->
                        if (action.questionId == question.id)
                        {
                            val updatedOptions = question.options.map { answer ->
                                if (action.answerId == answer.id)
                                {
                                    answer.copy(text = action.text)
                                } else {
                                    answer
                                }
                            }
                            question.copy(options = updatedOptions)
                        } else {
                            question
                        }
                    }
                    state.copy(
                        quiz = state.quiz.copy(
                            questions = updatedQuestions
                        ),
                        errorMessage = null
                    )
                }
            }
            is CreateQuizAction.AnswerCorrectnessChanged -> {
                _uiState.update { state ->
                    val updatedQuestions = state.quiz.questions.map { question ->
                        if (action.questionId == question.id)
                        {
                            val updatedOptions = question.options.map { answer ->
                                if (action.answerId == answer.id)
                                {
                                    answer.copy(isCorrect = action.isCorrect)
                                } else if (
                                    question.type == QuestionType.SINGLE_CHOICE &&
                                    action.isCorrect
                                ) {
                                    answer.copy(isCorrect = false)
                                } else {
                                    answer
                                }
                            }
                            question.copy(options = updatedOptions)
                        } else {
                            question
                        }
                    }
                    state.copy(
                        quiz = state.quiz.copy(
                            questions = updatedQuestions
                        ),
                        errorMessage = null
                    )
                }
            }
            is CreateQuizAction.RemoveQuestion -> {
                _uiState.update { state ->
                    val updatedQuestions = state.quiz.questions.filterNot { question ->
                        question.id == action.questionId
                    }
                    state.copy(
                        quiz = state.quiz.copy(
                            questions = updatedQuestions
                        ),
                        errorMessage = null
                    )
                }
            }
            is CreateQuizAction.RemoveAnswer -> {
                _uiState.update { state ->
                    val updatedQuestions = state.quiz.questions.map { question ->
                        if (question.id == action.questionId)
                        {
                            question.copy(
                                options = question.options.filterNot { answer ->
                                    answer.id == action.answerId
                                }
                            )
                        } else {
                            question
                        }
                    }
                    state.copy(
                        quiz = state.quiz.copy(
                            questions = updatedQuestions
                        ),
                        errorMessage = null
                    )
                }
            }
            is CreateQuizAction.AddAnswer -> {
                _uiState.update { state ->
                    val newAnswer = AnswerDraft()
                    val updatedQuestions = state.quiz.questions.map { question ->
                        if (question.id == action.questionId)
                        {
                            question.copy(
                                options = question.options + newAnswer
                            )
                        } else {
                            question
                        }
                    }
                    state.copy(
                        quiz = state.quiz.copy(
                            questions = updatedQuestions
                        ),
                        errorMessage = null
                    )
                }
            }
            CreateQuizAction.SaveQuiz -> {
                val quiz = _uiState.value.quiz
                val validationError = validateQuiz(quiz)
                if (validationError != null) {
                    _uiState.update { state ->
                        state.copy(errorMessage = validationError)
                    }
                } else {
                    _uiState.update { state ->
                        state.copy(errorMessage = null)
                    }
                    viewModelScope.launch {
                        _uiState.update { state ->
                            state.copy(
                                isSaving = true,
                                errorMessage = null,
                                successMessage = null
                            )
                        }
                        val statusCode = quizRepository.createQuiz(toCreateQuizRequest(quiz))
                        if (statusCode == 201) {
                            _uiState.update { state ->
                                state.copy(
                                    isSaving = false,
                                    errorMessage = null,
                                    successMessage = "Quiz zapisany pomyślnie!"
                                )
                            }
                        } else {
                            _uiState.update { state ->
                                state.copy(
                                    isSaving = false,
                                    errorMessage = "Nie udało się zapisać quizu. Kod HTTP: $statusCode",
                                    successMessage = null
                                )
                            }
                        }

                    }
                }
            }
        }
    }

    private fun toCreateQuizRequest(
        quiz: QuizDraft
    ): CreateQuizRequest {
        return CreateQuizRequest(
            title = quiz.title,
            description = quiz.description,
            questions = quiz.questions.map { question ->
                CreateQuestionRequest(
                    text = question.text,
                    options = question.options.map { answer ->
                        CreateAnswerRequest(
                            text = answer.text,
                            correct = answer.isCorrect
                        )
                    }
                )
            }
        )
    }
    private fun validateQuiz(quiz: QuizDraft): String? {
        if (quiz.title.isBlank()) {
            return "Podaj tytuł quizu."
        }

        if (quiz.description.isBlank()) {
            return "Podaj opis quizu."
        }

        if (quiz.questions.isEmpty()) {
            return "Dodaj przynajmniej jedno pytanie."
        }

        for ((questionIndex, question) in quiz.questions.withIndex()) {
            val questionNumber = questionIndex + 1

            if (question.text.isBlank()) {
                return "Uzupełnij treść pytania $questionNumber."
            }

            if (question.options.size < 2) {
                return "Pytanie $questionNumber musi mieć przynajmniej dwie odpowiedzi."
            }

            for ((answerIndex, answer) in question.options.withIndex()) {
                if (answer.text.isBlank()) {
                    val answerNumber = answerIndex + 1
                    return "Uzupełnij treść odpowiedzi $answerNumber w pytaniu $questionNumber."
                }
            }

            val correctAnswersCount = question.options.count { answer ->
                answer.isCorrect
            }

            when (question.type) {
                QuestionType.SINGLE_CHOICE -> {
                    if (correctAnswersCount != 1) {
                        return "Pytanie $questionNumber musi mieć dokładnie jedną poprawną odpowiedź."
                    }
                }

                QuestionType.MULTIPLE_CHOICE -> {
                    if (correctAnswersCount < 2) {
                        return "Pytanie $questionNumber musi mieć przynajmniej dwie poprawne odpowiedzi."
                    }
                }
            }
        }

        return null
    }
}