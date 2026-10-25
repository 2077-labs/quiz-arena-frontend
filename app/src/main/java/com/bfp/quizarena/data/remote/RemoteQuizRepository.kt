package com.bfp.quizarena.data.remote

import io.ktor.http.ContentType
import com.bfp.quizarena.data.remote.dto.CreateQuizRequest
import com.bfp.quizarena.data.repository.QuizRepository
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.contentType

class RemoteQuizRepository(
    private val httpClient: HttpClient
):  QuizRepository
{

    override suspend fun createQuiz(
        createQuizRequest: CreateQuizRequest
    ): Int
    {
        val response = httpClient.post("${ApiConfig.BASE_URL}/api/quizzes") {
            contentType(ContentType.Application.Json)
            setBody(createQuizRequest)
        }
        return response.status.value
    }
}