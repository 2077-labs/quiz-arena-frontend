    package com.bfp.quizarena.feature.quiz.creator

    import androidx.compose.foundation.layout.Arrangement
    import androidx.compose.foundation.layout.Column
    import androidx.compose.foundation.layout.Row
    import androidx.compose.foundation.layout.fillMaxSize
    import androidx.compose.foundation.layout.fillMaxWidth
    import androidx.compose.foundation.layout.padding
    import androidx.compose.foundation.rememberScrollState
    import androidx.compose.foundation.verticalScroll
    import androidx.compose.material3.Button
    import androidx.compose.material3.Checkbox
    import androidx.compose.material3.MaterialTheme
    import androidx.compose.material3.OutlinedTextField
    import androidx.compose.material3.Text
    import androidx.compose.runtime.Composable
    import androidx.compose.runtime.collectAsState
    import androidx.compose.runtime.getValue
    import androidx.compose.ui.Alignment
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.unit.dp
    import com.bfp.quizarena.feature.quiz.model.QuestionType
    import org.koin.compose.viewmodel.koinViewModel

    @Composable
    fun CreateQuizScreen(
        modifier: Modifier = Modifier,
        viewModel: CreateQuizViewModel = koinViewModel()
    ) {
        val uiState by viewModel.uiState.collectAsState()

        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "QuizArena — tworzenie quizu",
                style = MaterialTheme.typography.headlineSmall
            )

            OutlinedTextField(
                value = uiState.quiz.title,
                onValueChange = {
                    viewModel.onAction(CreateQuizAction.TitleChanged(it))
                },
                label = { Text("Tytuł quizu") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = uiState.quiz.description,
                onValueChange = {
                    viewModel.onAction(CreateQuizAction.DescriptionChanged(it))
                },
                label = { Text("Opis quizu") },
                modifier = Modifier.fillMaxWidth()
            )

            Text("Liczba pytań: ${uiState.quiz.questions.size}")

            Button(
                onClick = {
                    viewModel.onAction(
                        CreateQuizAction.AddQuestion(QuestionType.SINGLE_CHOICE)
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Dodaj pytanie")
            }

            uiState.quiz.questions.forEachIndexed { index, question ->
                Text(
                    text = "Pytanie ${index + 1}",
                    style = MaterialTheme.typography.titleMedium
                )

                OutlinedTextField(
                    value = question.text,
                    onValueChange = {
                        viewModel.onAction(
                            CreateQuizAction.QuestionTextChanged(
                                questionId = question.id,
                                text = it
                            )
                        )
                    },
                    label = { Text("Treść pytania") },
                    modifier = Modifier.fillMaxWidth()
                )

                question.options.forEachIndexed { answerIndex, answer ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = answer.isCorrect,
                            onCheckedChange = {
                                viewModel.onAction(
                                    CreateQuizAction.AnswerCorrectnessChanged(
                                        questionId = question.id,
                                        answerId = answer.id,
                                        isCorrect = it
                                    )
                                )
                            }
                        )

                        OutlinedTextField(
                            value = answer.text,
                            onValueChange = {
                                viewModel.onAction(
                                    CreateQuizAction.AnswerTextChanged(
                                        questionId = question.id,
                                        answerId = answer.id,
                                        text = it
                                    )
                                )
                            },
                            label = {
                                Text("Odpowiedź ${answerIndex + 1}")
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Button(
                    onClick = {
                        viewModel.onAction(
                            CreateQuizAction.AddAnswer(question.id)
                        )
                    }
                ) {
                    Text("Dodaj odpowiedź")
                }
            }

            Button(
                onClick = {
                    viewModel.onAction(CreateQuizAction.SaveQuiz)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isSaving
            ) {
                Text(
                    text = if (uiState.isSaving) {
                        "Zapisywanie..."
                    } else {
                        "Zapisz quiz"
                    }
                )
            }

            uiState.errorMessage?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error
                )
            }

            uiState.successMessage?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }