package com.example.avpzmobile

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class CustomAdapterProjects(context: Context, private val projects: ArrayList<Project>) : BaseAdapter() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getCount(): Int = projects.size

    override fun getItem(position: Int): Any = projects[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val holder: ViewHolder

        if (convertView == null) {
            view = inflater.inflate(R.layout.list_item_project, parent, false)
            holder = ViewHolder(view)
            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ViewHolder
        }

        val project = projects[position]
        holder.projectNameTextView.text = project.title
        holder.projectDescriptionTextView.text = project.description

        return view
    }

    private class ViewHolder(view: View) {
        val projectNameTextView: TextView = view.findViewById(R.id.project_name)
        val projectDescriptionTextView: TextView = view.findViewById(R.id.project_description)
    }
}