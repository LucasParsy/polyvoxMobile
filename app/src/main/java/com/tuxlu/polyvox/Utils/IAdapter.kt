package com.tuxlu.polyvox.Utils

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


/**
 * Created by tuxlu on 14/11/17.
 */

interface ViewHolderBinder<T: Any> {
    fun bind(holder: Adapter.ViewHolder<T>, item: T)
    fun setClickListener(holder: Adapter.ViewHolder<T>, data: List<T>)
/*    {
        val clickListener = View.OnClickListener {view ->
            view.context;
            data.truc...
        }
        frame.setOnClickListener(clickListener);
    }*/
}

class Adapter<T: Any>(private  val context : Context,
                      private val data: List<T>,
                      private val layout: Int,
                      private val binder : ViewHolderBinder<T>) : RecyclerView.Adapter<Adapter.ViewHolder<T>>() {


    class ViewHolder<T: Any> (public val v: View, private val binder : ViewHolderBinder<T>) : RecyclerView.ViewHolder(v) {

        fun bind(item: T) {
            binder.bind(this, item)
        }

        fun setClickListener(data: List<T>) {
            binder.setClickListener(this, data)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): ViewHolder<T> {
        val v = LayoutInflater.from(parent.context)
                .inflate(layout, parent, false)
        return ViewHolder(v, binder)
    }

    override fun onBindViewHolder(holder: ViewHolder<T>, position: Int) {
        val item = data[position]
        holder.bind(item)
        holder.setClickListener(data)
    }

    override fun getItemCount(): Int = data.size

    companion object {
        private val TAG = "DiscoverAdapter"
    }
}