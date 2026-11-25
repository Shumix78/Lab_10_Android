package com.ip_24_1.lab_10_hrynchyshyn_mykhailo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter(
    private val taskList: MutableList<Task>,
    private val onItemClick: (Int) -> Unit,
    private val onDeleteClick: (Int) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTaskTitle: TextView = itemView.findViewById(R.id.tvTaskTitle)
        val tvTaskAddress: TextView = itemView.findViewById(R.id.tvTaskAddress)
        val btnDeleteTask: Button = itemView.findViewById(R.id.btnDeleteTask)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = taskList[position]
        holder.tvTaskTitle.text = task.title

        if (task.address.isNotEmpty()) {
            holder.tvTaskAddress.visibility = View.VISIBLE
            holder.tvTaskAddress.text = task.address
        } else {
            holder.tvTaskAddress.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            val currentPos = holder.bindingAdapterPosition
            if (currentPos != RecyclerView.NO_POSITION) {
                onItemClick(currentPos)
            }
        }

        holder.btnDeleteTask.setOnClickListener {
            val currentPos = holder.bindingAdapterPosition
            if (currentPos != RecyclerView.NO_POSITION) {
                onDeleteClick(currentPos)
            }
        }
    }

    override fun getItemCount(): Int = taskList.size
}