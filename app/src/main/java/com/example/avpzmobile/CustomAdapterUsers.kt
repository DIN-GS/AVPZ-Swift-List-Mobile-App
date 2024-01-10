package com.example.avpzmobile

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class CustomAdapterUsers(context: Context, private val users: ArrayList<User>) : BaseAdapter() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getCount(): Int = users.size

    override fun getItem(position: Int): Any = users[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val holder: ViewHolder

        if (convertView == null) {
            view = inflater.inflate(R.layout.list_item_users, parent, false) // Changed layout
            holder = ViewHolder(view)
            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ViewHolder
        }

        val user = users[position]
        holder.fullNameTextView.text = user.fullName
        holder.emailTextView.text = user.email

        return view
    }

    private class ViewHolder(view: View) {
        val fullNameTextView: TextView = view.findViewById(R.id.user_full_name) // Changed IDs
        val emailTextView: TextView = view.findViewById(R.id.user_email)
    }
}