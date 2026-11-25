package com.ip_24_1.lab_10_hrynchyshyn_mykhailo

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

class TaskPreferences(context: Context) {

    private val sharedPreferences =
        context.getSharedPreferences("tasks_prefs", Context.MODE_PRIVATE)
    private val keyTasks = "tasks_list_v2"

    fun getTasks(): MutableList<Task> {
        val jsonString = sharedPreferences.getString(keyTasks, null) ?: return mutableListOf()
        val jsonArray = JSONArray(jsonString)
        val tasks = mutableListOf<Task>()
        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            tasks.add(
                Task(
                    title = obj.getString("title"),
                    address = obj.optString("address", ""),
                    latitude = if (obj.has("lat") && !obj.isNull("lat")) obj.getDouble("lat") else null,
                    longitude = if (obj.has("lng") && !obj.isNull("lng")) obj.getDouble("lng") else null
                )
            )
        }
        return tasks
    }

    fun addTask(task: Task) {
        val tasks = getTasks()
        tasks.add(task)
        saveTasks(tasks)
    }

    fun deleteTask(position: Int) {
        val tasks = getTasks()
        if (position in tasks.indices) {
            tasks.removeAt(position)
            saveTasks(tasks)
        }
    }

    private fun saveTasks(tasks: List<Task>) {
        val jsonArray = JSONArray()
        for (task in tasks) {
            val obj = JSONObject().apply {
                put("title", task.title)
                put("address", task.address)
                put("lat", task.latitude)
                put("lng", task.longitude)
            }
            jsonArray.put(obj)
        }
        sharedPreferences.edit().putString(keyTasks, jsonArray.toString()).apply()
    }
    fun updateTask(position: Int, newTitle: String) {
        val tasks = getTasks()
        if (position in tasks.indices) {
            val oldTask = tasks[position]
            tasks[position] = oldTask.copy(title = newTitle)
            saveTasks(tasks)
        }
    }
}