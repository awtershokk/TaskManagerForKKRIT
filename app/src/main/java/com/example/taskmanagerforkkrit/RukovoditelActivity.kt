package com.example.taskmanagerforkkrit

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class RukovoditelActivity : AppCompatActivity() {

    private lateinit var spinnerAssignTo: Spinner
    private lateinit var editTextTaskName: EditText
    private lateinit var buttonSelectDate: Button
    private lateinit var buttonSelectTime: Button
    private lateinit var buttonCreateTask: Button

    private var selectedDate: String = ""
    private var selectedTime: String = ""

    private val timeList = listOf("08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rukovoditel)

        spinnerAssignTo = findViewById(R.id.spinnerAssignTo)
        editTextTaskName = findViewById(R.id.editTextTaskName)
        buttonSelectDate = findViewById(R.id.buttonSelectDate)
        buttonSelectTime = findViewById(R.id.buttonSelectTime)
        buttonCreateTask = findViewById(R.id.buttonCreateTask)

        loadEmployees()

        buttonSelectDate.setOnClickListener {
            showDatePickerDialog()
        }

        buttonSelectTime.setOnClickListener {
            showTimePickerDialog()
        }

        buttonCreateTask.setOnClickListener {
            val taskName = editTextTaskName.text.toString()
            val assignedTo = spinnerAssignTo.selectedItem.toString()

            if (taskName.isNotEmpty() && assignedTo.isNotEmpty() && selectedDate.isNotEmpty() && selectedTime.isNotEmpty()) {
                writeToDatabase(taskName, assignedTo, selectedDate, selectedTime)
            } else {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, monthOfYear, dayOfMonth ->
                selectedDate = "$dayOfMonth/${monthOfYear + 1}/$year"
                buttonSelectDate.text = selectedDate
            },
            year,
            month,
            day
        )

        datePickerDialog.show()
    }

    private fun showTimePickerDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Выберите время")

        builder.setItems(timeList.toTypedArray()) { _, which ->
            selectedTime = timeList[which]
            buttonSelectTime.text = selectedTime
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun loadEmployees() {
        val database = Firebase.database
        val usersRef = database.getReference("users")

        usersRef.orderByChild("role").equalTo("Сотрудник").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val employeesList = mutableListOf<String>()

                for (userSnapshot in dataSnapshot.children) {
                    val name = userSnapshot.child("name").getValue(String::class.java)
                    if (name != null) {
                        employeesList.add(name)
                    }
                }

                val adapter = ArrayAdapter<String>(this@RukovoditelActivity, android.R.layout.simple_spinner_item, employeesList)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerAssignTo.adapter = adapter
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Обработка ошибок
            }
        })
    }

    private fun writeToDatabase(taskName: String, assignedTo: String, date: String, time: String) {
        val database = Firebase.database
        val tasksRef = database.getReference("tasks")

        val currentUser = FirebaseAuth.getInstance().currentUser
        val createdBy = currentUser?.uid ?: ""

        val task = HashMap<String, Any>()
        task["taskName"] = taskName
        task["assignedTo"] = assignedTo
        task["deadline"] = date
        task["time"] = time
        task["createdBy"] = createdBy
        task["status"] = "created"

        val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        task["creationDate"] = currentDate

        val newTaskRef = tasksRef.push()
        newTaskRef.setValue(task)
            .addOnSuccessListener {
                Toast.makeText(this, "Задача успешно создана", Toast.LENGTH_SHORT).show()
                editTextTaskName.text.clear()
                buttonSelectDate.text = "Выбрать дату"
                buttonSelectTime.text = "Выбрать время"
            }
            .addOnFailureListener {
                Toast.makeText(this, "Ошибка при создании задачи", Toast.LENGTH_SHORT).show()
            }
    }
}




