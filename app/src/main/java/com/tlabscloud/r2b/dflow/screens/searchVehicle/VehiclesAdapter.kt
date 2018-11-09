package com.tlabscloud.r2b.dflow.screens.searchVehicle

import android.content.Context
import android.graphics.PointF
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.text.bold
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.*
import com.squareup.picasso.Picasso
import com.tlabscloud.r2b.dflow.R
import com.tlabscloud.r2b.dflow.databinding.SearchVehicleListItemBinding
import com.tlabscloud.r2b.dflow.model.Vehicle
import com.tlabscloud.r2b.dflow.model.VehicleTariff


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

@BindingAdapter("imageFromUrl")
fun bindImageFromUrl(view: ImageView, imageUrl: String?) {
    if (!imageUrl.isNullOrEmpty()) {
        Picasso.with(view.context)
            .load(imageUrl)
            .into(view)
    }
}

@BindingAdapter("vehicleTariffText")
fun bindVehicleTariffText(textView: TextView, vehicleTariff: VehicleTariff) {
    val resources = textView.context.resources
    val tariffPerMinute = resources.getString(R.string.tariff_per_minute)
    val waitTime = resources.getString(R.string.tariff_wait_time)
    val min = resources.getString(R.string.min)
    val builder = SpannableStringBuilder()
        .bold { append("${vehicleTariff.pricePerMinute} ${vehicleTariff.currency} ") }
        .append(tariffPerMinute)

    if(vehicleTariff.freeWaitTime!=0){
        builder.append(", $waitTime ").bold { append("${vehicleTariff.freeWaitTime} $min") }
    }
    textView.text = builder
}

@BindingAdapter("vehicleChargeLevelText")
fun bindVehicleChargeLevelText(textView: TextView, vehicle: Vehicle) {
    val resources = textView.context.resources
    val km = resources.getString(R.string.km)
    val builder = SpannableStringBuilder()
        .bold { append("${vehicle.chargingLevel*100}% ") }
        .append("(~${vehicle.chargingLevel*vehicle.electricRange} $km)")
    textView.text = builder
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