package com.bfp.quizarena.data.repository

import com.bfp.quizarena.data.remote.dto.CreateQuizRequest

class FakeQuizRepository : QuizRepository {

    private val savedQuizzes = mutableListOf<CreateQuizRequest>()

    override suspend fun createQuiz(
        createQuizRequest: CreateQuizRequest
    ): Int {
        savedQuizzes.add(createQuizRequest)

        return 201
    }
}