package com.example.androiddevelopers.ui.game.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "questions")
data class Question(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "question_text")
    val questionText: String,

    @ColumnInfo(name = "option_a")
    val optionA: String,

    @ColumnInfo(name = "option_b")
    val optionB: String,

    @ColumnInfo(name = "option_c")
    val optionC: String,

    @ColumnInfo(name = "correct_answer_index")
    val correctAnswerIndex: Int
)