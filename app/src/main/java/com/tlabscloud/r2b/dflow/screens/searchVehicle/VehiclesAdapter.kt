package com.tlabscloud.r2b.dflow.screens.searchVehicle

import android.content.Context
import android.graphics.PointF
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.*
import com.tlabscloud.r2b.dflow.databinding.SearchVehicleListItemBinding
import com.tlabscloud.r2b.dflow.model.Vehicle


class VehiclesListAdapter(
    private val clickCallback: (Vehicle) -> Unit,
    private val bookCallback: (Vehicle, ImageView) -> Unit
) :
    ListAdapter<Vehicle, VehiclesListAdapter.ViewHolder>(
        MapLocationDiffCallback()
    ) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val location = getItem(position)
        holder.apply {
            bind(
                createOnClickListener(location, clickCallback),
                bookCallback,
                location
            )
            itemView.tag = location
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            SearchVehicleListItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    private fun createOnClickListener(location: Vehicle, callback: (Vehicle) -> Unit): View.OnClickListener {
        return View.OnClickListener {
            callback(location)
        }
    }

    fun getItemByPosition(position: Int) = getItem(position)


    class ViewHolder(
        private val binding: SearchVehicleListItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            clickListener: View.OnClickListener,
            bookListener: (Vehicle, ImageView) -> Unit,
            item: Vehicle
        ) {
            binding.apply {
                focusOnVehicleListener = clickListener
                bookVehicleListener = View.OnClickListener {
                    bookListener(item, binding.vehicleItemImage)
                }
                vehicle = item
                executePendingBindings()
            }
        }
    }
}

class MapLocationDiffCallback : DiffUtil.ItemCallback<Vehicle>() {

    override fun areItemsTheSame(
        oldItem: Vehicle,
        newItem: Vehicle
    ): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(
        oldItem: Vehicle,
        newItem: Vehicle
    ): Boolean {
        return oldItem == newItem
    }
}

class LinearLayoutManagerWithSmoothScroller(context: Context) :
    LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false) {

    override fun smoothScrollToPosition(
        recyclerView: RecyclerView, state: RecyclerView.State?,
        position: Int
    ) {
        val smoothScroller = TopSnappedSmoothScroller(recyclerView.context)
        smoothScroller.targetPosition = position
        startSmoothScroll(smoothScroller)
    }

    private inner class TopSnappedSmoothScroller internal constructor(context: Context) :
        LinearSmoothScroller(context) {

        override fun computeScrollVectorForPosition(targetPosition: Int): PointF? {
            return this@LinearLayoutManagerWithSmoothScroller
                .computeScrollVectorForPosition(targetPosition)
        }

        override fun getVerticalSnapPreference(): Int {
            return SNAP_TO_START
        }
    }
}