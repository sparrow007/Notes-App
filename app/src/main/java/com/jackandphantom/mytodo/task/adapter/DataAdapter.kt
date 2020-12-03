package com.jackandphantom.mytodo.task.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jackandphantom.mytodo.data.Task
import com.jackandphantom.mytodo.databinding.ListItemBinding
import com.jackandphantom.mytodo.databinding.TaskListBinding
import com.jackandphantom.mytodo.task.TaskViewModel

class DataAdapter (private val viewModel: TaskViewModel) : ListAdapter<Task, DataAdapter.ViewHolder>(TaskDiffCallback()) {


    class ViewHolder private constructor(val binding : ListItemBinding) : RecyclerView.ViewHolder(binding.root) {


        fun bind(viewModel: TaskViewModel , task: Task) {

            binding.viewmodel = viewModel
            binding.task = task
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent : ViewGroup) : ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }

    }

    class TaskDiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.entry_id == newItem.entry_id
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(viewModel, getItem(position))
    }
}

