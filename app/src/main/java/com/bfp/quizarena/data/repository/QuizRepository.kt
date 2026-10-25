package com.bfp.quizarena.data.repository

import com.bfp.quizarena.data.remote.dto.CreateQuizRequest

interface QuizRepository {
    suspend fun createQuiz(createQuizRequest: CreateQuizRequest): Int
}