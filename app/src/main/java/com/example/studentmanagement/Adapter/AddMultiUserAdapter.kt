package com.example.studentmanagement.Adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.studentmanagement.Activity.AddMultiUserActivity
import com.example.studentmanagement.Domain.User
import com.example.studentmanagement.R
import com.squareup.picasso.Picasso

class AddMultiUserAdapter (
    private var userList: ArrayList<User>,
    activity: AddMultiUserActivity
) : RecyclerView.Adapter<AddMultiUserAdapter.UserInfoViewHolder>(){

    var activity = activity

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserInfoViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.file_user_viewholder, parent, false)
        return UserInfoViewHolder(view)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: UserInfoViewHolder, position: Int) {
        holder.name.setText(userList[position].name)
        holder.age.setText(userList[position].age)
        holder.phone.setText(userList[position].phone)
        holder.status.setText(userList[position].status)

//        holder.name.addTextChangedListener(object:TextWatcher{
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                holder.name.setText(p0.toString())
//            }
//
//            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                holder.name.setText(p0.toString())
//            }
//
//            override fun afterTextChanged(p0: Editable?) {
//                holder.name.setText(p0.toString())
//            }
//        })
//
//        holder.age.addTextChangedListener(object:TextWatcher{
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                holder.age.setText(p0.toString())
//            }
//
//            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                holder.age.setText(p0.toString())
//            }
//
//            override fun afterTextChanged(p0: Editable?) {
//                holder.age.setText(p0.toString())
//            }
//        })
//
//        holder.phone.addTextChangedListener(object:TextWatcher{
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                holder.phone.setText(p0.toString())
//            }
//
//            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                holder.phone.setText(p0.toString())
//            }
//
//            override fun afterTextChanged(p0: Editable?) {
//                holder.phone.setText(p0.toString())
//            }
//        })
//
//        holder.status.addTextChangedListener(object:TextWatcher{
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                holder.status.setText(p0.toString())
//            }
//
//            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                holder.status.setText(p0.toString())
//            }
//
//            override fun afterTextChanged(p0: Editable?) {
//                holder.status.setText(p0.toString())
//            }
//        })
    }

    class UserInfoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val name = itemView.findViewById<EditText>(R.id.tvName)
        val age = itemView.findViewById<EditText>(R.id.tvAge)
        val phone = itemView.findViewById<EditText>(R.id.tvPhone)
        val status = itemView.findViewById<EditText>(R.id.tvStatus)
    }

}