package com.example.taskmanagerforkkrit

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class UserActivity : AppCompatActivity() {

    private lateinit var spinnerSort: Spinner
    private lateinit var spinnerFilter: Spinner
    private lateinit var buttonMarkAsDone1: Button
    private lateinit var buttonMarkAsDone2: Button
    private lateinit var textViewStatus1: TextView
    private lateinit var textViewStatus2: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        spinnerSort = findViewById(R.id.spinnerSort)
        spinnerFilter = findViewById(R.id.spinnerFilter)
        buttonMarkAsDone1 = findViewById(R.id.buttonMarkAsDone1)
        buttonMarkAsDone2 = findViewById(R.id.buttonMarkAsDone2)
        textViewStatus1 = findViewById(R.id.textViewTaskStatus)
        textViewStatus2 = findViewById(R.id.textViewTaskStatus2)

        val sortOptions = arrayOf("Сортировка", "От А до Я", "От Я до А", "По сроку выполнения")
        val filterOptions = arrayOf("Фильтрация", "Все задачи", "Выполненные")

        val sortAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, sortOptions)
        val filterAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, filterOptions)

        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinnerSort.adapter = sortAdapter
        spinnerFilter.adapter = filterAdapter

        buttonMarkAsDone1.setOnClickListener {
            textViewStatus1.text = "Статус: Выполнено"
        }

        buttonMarkAsDone2.setOnClickListener {
            textViewStatus2.text = "Статус: Выполнено"
        }
    }
}

