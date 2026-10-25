package com.bfp.quizarena.di

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module
import com.bfp.quizarena.feature.quiz.creator.CreateQuizViewModel
import org.koin.core.module.dsl.viewModel
import com.bfp.quizarena.data.repository.QuizRepository
import com.bfp.quizarena.data.repository.FakeQuizRepository

val appModule = module {
    single<HttpClient> {
        HttpClient(CIO) {
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                    }
                )
            }
        }
    }
    single<QuizRepository> {
        FakeQuizRepository()
    }
    viewModel { CreateQuizViewModel(get()) }
}