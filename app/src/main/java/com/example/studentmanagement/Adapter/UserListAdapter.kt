package com.example.studentmanagement.Adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.studentmanagement.Activity.ProfileActivity
import com.example.studentmanagement.Activity.UserManagementActivity
import com.example.studentmanagement.Domain.User
import com.example.studentmanagement.R
import com.squareup.picasso.Picasso

class UserListAdapter(
    private val userList: ArrayList<User>,
    activity: UserManagementActivity
) : RecyclerView.Adapter<UserListAdapter.UserViewHolder>(){


    var activity = activity


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.user_viewholder, parent, false)
        return UserViewHolder(view)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        if (userList[position].avatarUrl == "") {
            holder.ava.setImageResource(R.drawable.user)
        } else {
            Picasso.get().load(userList[position].avatarUrl).into(holder.ava)
        }
        holder.name.text = userList[position].name
        holder.pos.text = userList[position].position

        holder.itemView.setOnClickListener(View.OnClickListener {
            val intent = Intent(activity, ProfileActivity::class.java)
            intent.putExtra("user", userList[position])
            activity.startActivity(intent)
        })
    }

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val ava = itemView.findViewById<ImageView>(R.id.imageView_ava)
        val name = itemView.findViewById<TextView>(R.id.textView_name)
        val pos = itemView.findViewById<TextView>(R.id.textView_position)
    }
}