package com.example.studentmanagement.Adapter

import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.studentmanagement.Activity.ProfileActivity
import com.example.studentmanagement.DB.CertificateDAL
import com.example.studentmanagement.Domain.Certificate
import com.example.studentmanagement.R

class CertificateListAdapter(
    private var certiList: ArrayList<Certificate>,
    activity: ProfileActivity
) : RecyclerView.Adapter<CertificateListAdapter.CertiViewHolder>(){

    var activity = activity

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CertiViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.medal_viewholder, parent, false)
        return CertiViewHolder(view)
    }

    override fun getItemCount(): Int {
        return certiList.size
    }

    override fun onBindViewHolder(holder: CertiViewHolder, position: Int) {
        holder.name.text = certiList[position].certiName
        holder.itemView.setOnClickListener(View.OnClickListener {

        })
    }

    inner class CertiViewHolder(itemView: View)
        : RecyclerView.ViewHolder(itemView) {
        val name = itemView.findViewById<TextView>(R.id.textView_name)

        init {
            itemView.setOnLongClickListener(View.OnLongClickListener {
                popupMenus(itemView)
                false
            })
        }

        private fun popupMenus(v:View) {
            val position = certiList[adapterPosition]
            val popupMenus = PopupMenu(activity,v)
            popupMenus.inflate(R.menu.certificate_menu)
            popupMenus.setOnMenuItemClickListener {
                when(it.itemId){
                    R.id.edit -> {
                        Toast.makeText(activity, "Edit click", Toast.LENGTH_SHORT).show()
                        val v = LayoutInflater.from(activity).inflate(R.layout.add_certi_dialog,null)
                        val name = v.findViewById<EditText>(R.id.edt_certiName)
                        val desc = v.findViewById<EditText>(R.id.edt_certiDesc)

                        name.setText(position.certiName)
                        desc.setText(position.certiContent)

                        AlertDialog.Builder(activity)
                            .setView(v)
                            .setPositiveButton("OK"){
                                    dialog,_->
                                position.certiName = name.text.toString()
                                position.certiContent = desc.text.toString()
                                notifyDataSetChanged()

                                CertificateDAL().UpdateCerti(position)
                                notifyItemChanged(adapterPosition)

                                dialog.dismiss()
                            }
                            .setNegativeButton("CANCEL"){
                                    dialog,_->
                                dialog.dismiss()

                            }
                            .create()
                            .show()

                        true
                    }
                    R.id.del -> {
                        /**set delete*/
                        AlertDialog.Builder(activity)
                            .setTitle("Delete")
                            .setIcon(R.drawable.warning_icon)
                            .setMessage("Are you sure delete this certificate")
                            .setPositiveButton("Yes"){
                                    dialog, _ ->

                                // Delete from database
                                CertificateDAL().DeleteCerti(certiList[adapterPosition])

                                certiList.removeAt(adapterPosition)
                                notifyDataSetChanged()

                                Toast.makeText(activity,"Deleted certificate",Toast.LENGTH_SHORT).show()
                                dialog.dismiss()
                            }
                            .setNegativeButton("No"){
                                    dialog,_->
                                dialog.dismiss()
                            }
                            .create()
                            .show()

                        true
                    }
                    else-> true
                }
            }
            popupMenus.show()
            val popup = PopupMenu::class.java.getDeclaredField("mPopup")
            popup.isAccessible = true
            val menu = popup.get(popupMenus)
            menu.javaClass.getDeclaredMethod("setForceShowIcon",Boolean::class.java)
                .invoke(menu,true)
        }
    }
}