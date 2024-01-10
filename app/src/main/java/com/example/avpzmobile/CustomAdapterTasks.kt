package com.example.avpzmobile

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class CustomAdapterTasks(context: Context, private val tasks: ArrayList<Task>) : BaseAdapter() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getCount(): Int = tasks.size

    override fun getItem(position: Int): Any = tasks[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val holder: ViewHolder

        if (convertView == null) {
            view = inflater.inflate(R.layout.list_item_tasks, parent, false) // Changed layout
            holder = ViewHolder(view)
            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ViewHolder
        }

        val task = tasks[position]
        holder.taskTitleTextView.text = task.title
        holder.taskDescriptionTextView.text = task.description

        return view
    }

    private class ViewHolder(view: View) {
        val taskTitleTextView: TextView = view.findViewById(R.id.task_title) // Changed IDs
        val taskDescriptionTextView: TextView = view.findViewById(R.id.task_description)
    }
}