package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Lesson
import com.example.data.model.QuizQuestion
import com.example.ui.components.DemoDataBadge
import com.example.ui.components.GlassCard
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.MarketGreen
import com.example.ui.theme.MarketRed
import com.example.ui.theme.PurpleDeep
import com.example.ui.theme.PurplePrimary
import com.example.ui.theme.PurpleSoftLavender
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun LearnScreen(
    lessons: List<Lesson>,
    completedLessonIds: Set<String>,
    selectedLesson: Lesson?,
    onSelectLesson: (String) -> Unit,
    onCloseLesson: () -> Unit,
    onMarkCompleted: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (selectedLesson != null) {
        LessonDetailView(
            lesson = selectedLesson,
            isCompleted = completedLessonIds.contains(selectedLesson.id),
            onBack = onCloseLesson,
            onMarkCompleted = { onMarkCompleted(selectedLesson.id) },
            modifier = modifier
        )
        return
    }

    var selectedCategoryFilter by remember { mutableStateOf<String?>(null) }

    val categories = remember(lessons) {
        listOf("All") + lessons.map { it.category }.distinct()
    }

    val filteredLessons = remember(lessons, selectedCategoryFilter) {
        if (selectedCategoryFilter == null || selectedCategoryFilter == "All") {
            lessons
        } else {
            lessons.filter { it.category == selectedCategoryFilter }
        }
    }

    val progressFraction = if (lessons.isNotEmpty()) {
        completedLessonIds.size.toFloat() / lessons.size.toFloat()
    } else 0f

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFAF9FD)),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // Screen Header
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp)) {
                Text(
                    text = "Finance Academy",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary
                )
                Text(
                    text = "Bite-sized financial market lessons & interactive quizzes",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        }

        // Progress Overview Card
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)) {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shapeRadius = 20.dp,
                    backgroundColor = Color.White
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "LEARNING PROGRESS",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PurpleDeep,
                                    letterSpacing = 0.5.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${completedLessonIds.size} of ${lessons.size} Lessons Completed",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(PurpleSoftLavender)
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "${(progressFraction * 100).toInt()}%",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black,
                                    color = PurpleDeep
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        LinearProgressIndicator(
                            progress = { progressFraction },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = PurplePrimary,
                            trackColor = Color(0xFFE9E5F5)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "💡 Financial literacy is the foundation of long-term wealth compounding.",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        }

        // Category Filter Pills
        item {
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { cat ->
                    val isSelected = (selectedCategoryFilter == null && cat == "All") || selectedCategoryFilter == cat
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) PurplePrimary else Color.White)
                            .border(1.dp, if (isSelected) PurplePrimary else BorderSubtle, RoundedCornerShape(12.dp))
                            .clickable { selectedCategoryFilter = if (cat == "All") null else cat }
                            .padding(horizontal = 14.dp, vertical = 7.dp)
                    ) {
                        Text(
                            text = cat,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else TextPrimary
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Lessons List
        items(filteredLessons) { lesson ->
            val isDone = completedLessonIds.contains(lesson.id)
            Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 5.dp)) {
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("lesson_card_${lesson.id}"),
                    shapeRadius = 18.dp,
                    backgroundColor = Color.White,
                    onClick = { onSelectLesson(lesson.id) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(if (isDone) Color(0xFFE6F7F0) else PurpleSoftLavender),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isDone) Icons.Default.Check else Icons.Default.School,
                                contentDescription = null,
                                tint = if (isDone) MarketGreen else PurplePrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = lesson.category,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PurpleDeep
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "• ${lesson.readTimeMinutes} min read",
                                    fontSize = 10.sp,
                                    color = TextTertiary
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = lesson.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = lesson.summary,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary,
                                maxLines = 2
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LessonDetailView(
    lesson: Lesson,
    isCompleted: Boolean,
    onBack: () -> Unit,
    onMarkCompleted: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val userAnswers = remember { mutableStateMapOf<Int, Int>() }
    var quizSubmitted by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFAF9FD)),
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {
        // Back bar
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(PurpleSoftLavender)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${lesson.category} • ${lesson.readTimeMinutes} min",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = PurpleDeep
                    )
                }
            }
        }

        // Lesson Title & Summary
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text(
                    text = lesson.title,
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = lesson.summary,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Sections
        items(lesson.sections) { sec ->
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shapeRadius = 18.dp,
                    backgroundColor = Color.White
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = sec.heading,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = sec.text,
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextPrimary,
                            lineHeight = 23.sp
                        )

                        if (sec.example != null) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(PurpleSoftLavender.copy(alpha = 0.6f))
                                    .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
                                    .padding(12.dp)
                            ) {
                                Row(verticalAlignment = Alignment.Top) {
                                    Icon(
                                        imageVector = Icons.Default.Lightbulb,
                                        contentDescription = null,
                                        tint = PurpleDeep,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Example: ${sec.example}",
                                        fontSize = 12.sp,
                                        color = TextPrimary,
                                        lineHeight = 18.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Interactive Quiz Section
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = PurplePrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Knowledge Check Quiz",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                lesson.quiz.forEachIndexed { qIndex, question ->
                    val selectedOption = userAnswers[qIndex]

                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        shapeRadius = 18.dp,
                        backgroundColor = Color.White
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Question ${qIndex + 1} of ${lesson.quiz.size}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = PurpleDeep
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = question.question,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            question.options.forEachIndexed { optIndex, optText ->
                                val isOptSelected = selectedOption == optIndex
                                val isCorrectOpt = question.correctIndex == optIndex

                                val optionBorder = when {
                                    quizSubmitted && isOptSelected && isCorrectOpt -> MarketGreen
                                    quizSubmitted && isOptSelected && !isCorrectOpt -> MarketRed
                                    isOptSelected -> PurplePrimary
                                    else -> BorderSubtle
                                }

                                val optionBg = when {
                                    quizSubmitted && isOptSelected && isCorrectOpt -> Color(0xFFE6F7F0)
                                    quizSubmitted && isOptSelected && !isCorrectOpt -> Color(0xFFFEECEB)
                                    isOptSelected -> PurpleSoftLavender
                                    else -> Color.White
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(optionBg)
                                        .border(1.dp, optionBorder, RoundedCornerShape(12.dp))
                                        .clickable(enabled = !quizSubmitted) {
                                            userAnswers[qIndex] = optIndex
                                        }
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = isOptSelected,
                                        onClick = { if (!quizSubmitted) userAnswers[qIndex] = optIndex },
                                        colors = RadioButtonDefaults.colors(selectedColor = PurplePrimary)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = optText,
                                        fontSize = 13.sp,
                                        color = TextPrimary,
                                        fontWeight = if (isOptSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }

                            // Explanation feedback
                            if (quizSubmitted) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0xFFF6F4FA))
                                        .padding(10.dp)
                                ) {
                                    Text(
                                        text = "Explanation: ${question.explanation}",
                                        fontSize = 12.sp,
                                        color = TextSecondary,
                                        lineHeight = 17.sp
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Submit Quiz / Complete button
                if (!quizSubmitted) {
                    val allAnswered = userAnswers.size == lesson.quiz.size
                    Button(
                        onClick = {
                            quizSubmitted = true
                            onMarkCompleted()
                        },
                        enabled = allAnswered,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("submit_quiz_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary)
                    ) {
                        Text("Check Quiz Answers & Complete Lesson", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFE6F7F0))
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MarketGreen)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Lesson Completed! Progress saved.",
                            fontWeight = FontWeight.Bold,
                            color = MarketGreen,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}
