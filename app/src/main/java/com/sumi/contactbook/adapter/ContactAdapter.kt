package com.sumi.contactbook.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sumi.contactbook.R
import com.sumi.contactbook.databinding.LayoutItemContactsBinding
import com.sumi.contactbook.model.contact.ContactModel

class ContactAdapter(
    val contactList: MutableList<ContactModel>
) :
    RecyclerView.Adapter<ContactAdapter.ViewHolder>() {

    inner class ViewHolder(binding: LayoutItemContactsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var contactsBinding: LayoutItemContactsBinding? = binding
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: LayoutItemContactsBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.layout_item_contacts,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val listItem = contactList.get(position)
        holder.contactsBinding?.item = listItem

        holder.contactsBinding?.tvNameFirst?.text = listItem.name?.first().toString()
    }

    override fun getItemCount(): Int {
        return contactList.size
    }
}