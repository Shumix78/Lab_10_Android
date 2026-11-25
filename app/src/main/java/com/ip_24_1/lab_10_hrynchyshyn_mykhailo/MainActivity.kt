package com.ip_24_1.lab_10_hrynchyshyn_mykhailo

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var taskPreferences: TaskPreferences
    private lateinit var taskList: MutableList<Task>
    private lateinit var adapter: TaskAdapter
    private lateinit var tvApiStatus: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        taskPreferences = TaskPreferences(this)
        taskList = taskPreferences.getTasks().toMutableList()

        val etTaskInput: EditText = findViewById(R.id.etTaskInput)
        val btnAddTask: Button = findViewById(R.id.btnAddTask)
        val btnLoadApi: Button = findViewById(R.id.btnLoadApi)
        tvApiStatus = findViewById(R.id.tvApiStatus)
        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewTasks)

        val btnOpenMap: Button = findViewById(R.id.btnOpenMap)
        btnOpenMap.setOnClickListener {
            val intent = android.content.Intent(this, MapsActivity::class.java)
            mapLauncher.launch(intent)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = TaskAdapter(
            taskList = taskList,
            onItemClick = { position ->
                showEditTaskDialog(position)
            },
            onDeleteClick = { position ->
                taskPreferences.deleteTask(position)
                taskList.removeAt(position)
                adapter.notifyItemRemoved(position)
            }
        )
        recyclerView.adapter = adapter

        btnAddTask.setOnClickListener {
            val title = etTaskInput.text.toString().trim()
            if (title.isNotEmpty()) {
                val newTask = Task(title)
                taskPreferences.addTask(newTask)
                taskList.add(newTask)
                adapter.notifyItemInserted(taskList.size - 1)
                etTaskInput.text.clear()
            }
        }

        btnLoadApi.setOnClickListener {
            loadCurrencyRates()
        }
    }

    private fun loadCurrencyRates() {
        tvApiStatus.visibility = View.VISIBLE
        tvApiStatus.text = "Завантаження даних..."

        val apiService = ApiService.create()
        apiService.getExchangeRates().enqueue(object : Callback<List<CurrencyRate>> {
            override fun onResponse(
                call: Call<List<CurrencyRate>>,
                response: Response<List<CurrencyRate>>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val rates = response.body()!!.take(10)
                    tvApiStatus.text = "Завантажено курсів валют: ${rates.size}"

                    for (currency in rates) {
                        val task = Task("${currency.code} (${currency.name}): ${currency.rate} грн")
                        taskPreferences.addTask(task)
                        taskList.add(task)
                    }
                    adapter.notifyDataSetChanged()
                } else {
                    val errorMsg = "Помилка сервера: код ${response.code()}"
                    tvApiStatus.text = errorMsg
                    Toast.makeText(this@MainActivity, errorMsg, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<CurrencyRate>>, t: Throwable) {
                val errorMsg = "Помилка мережі: ${t.localizedMessage ?: "немає з'єднання"}"
                tvApiStatus.text = errorMsg
                Toast.makeText(this@MainActivity, errorMsg, Toast.LENGTH_LONG).show()
            }
        })
    }
    private val mapLauncher = registerForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            taskList.clear()
            taskList.addAll(taskPreferences.getTasks())
            adapter.notifyDataSetChanged()
        }
    }
    private fun showEditTaskDialog(position: Int) {
        val task = taskList[position]
        val input = EditText(this).apply {
            setText(task.title)
            setSelection(text.length)
        }

        val infoMessage = if (task.address.isNotEmpty()) {
            "Локація: ${task.address}"
        } else {
            "Локація не вказана"
        }

        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Перегляд та редагування задачі")
            .setMessage(infoMessage)
            .setView(input)
            .setPositiveButton("Зберегти") { _, _ ->
                val updatedTitle = input.text.toString().trim()
                if (updatedTitle.isNotEmpty()) {
                    taskPreferences.updateTask(position, updatedTitle)
                    taskList[position] = task.copy(title = updatedTitle)
                    adapter.notifyItemChanged(position)
                }
            }
            .setNegativeButton("Скасувати", null)
            .show()
    }
}