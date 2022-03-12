package com.example.olseraminiproject.view.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.olseraminiproject.R
import com.example.olseraminiproject.data.dataclass.CompanyDataClass
import com.example.olseraminiproject.databinding.ListCompanyItemBinding
import com.example.olseraminiproject.view.MainActivity

class InactiveStatusAdapter(private val context: Context?, private val activity: Activity): RecyclerView.Adapter<InactiveStatusAdapter.VH>() {

    private var dataSource: ArrayList<CompanyDataClass> = ArrayList<CompanyDataClass>()

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(dataSource.get(position), activity)
    }

    override fun getItemCount(): Int {
        if (dataSource == null) return 0
        else return dataSource.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view =
            ListCompanyItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(view)
    }

    class VH(view: ListCompanyItemBinding) : RecyclerView.ViewHolder(view.root) {
        private var parent: Activity? = null

        val parentView = view.parentView
        val title = view.titleCompany
        val desc = view.statusBookingCompany
        val statusLyt = view.statusCompanyLyt
        val status = view.statusCompany
        val btnMore = view.btnMore


        fun bind(data: CompanyDataClass, activity: Activity) {

            if (data.isMod){
                //genap
                parentView?.setBackgroundColor(ContextCompat.getColor(activity, R.color.grey))
            }else{
                //ganjil
                parentView?.setBackgroundColor(ContextCompat.getColor(activity, R.color.white))
            }

            btnMore.setOnClickListener { v ->
                val parent = activity as MainActivity
                parent.editCompany(data.id)
            }

            if (!data.status) {
                statusLyt.visibility = View.VISIBLE
                desc.visibility = View.GONE
            }

            status.text = "${data.txtStatus}"
            title.text = "${data.name}"
        }
    }

    fun swap(listData: ArrayList<CompanyDataClass>) {
        dataSource = listData
        notifyDataSetChanged()
    }

    fun addList(items: ArrayList<CompanyDataClass>) {
        dataSource?.addAll(items)
        notifyDataSetChanged()
    }

    fun clear() {
        dataSource?.clear()
        notifyDataSetChanged()
    }
}