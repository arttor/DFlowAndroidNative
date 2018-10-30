package com.tsystems.r2b.dflow.screens.map

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.tsystems.r2b.dflow.databinding.ListItemMapBinding
import com.tsystems.r2b.dflow.model.MapLocation


class MapLocationsAdapter : ListAdapter<MapLocation, MapLocationsAdapter.ViewHolder>(MapLocationDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val location = getItem(position)
        holder.apply {
            bind(createOnClickListener(location), location)
            itemView.tag = location
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ListItemMapBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    private fun createOnClickListener(location: MapLocation): View.OnClickListener {
        return View.OnClickListener {
            //val direction = PlantListFragmentDirections.ActionPlantListFragmentToPlantDetailFragment(plantId)
            //it.findNavController().navigate(direction)
        }
    }

    class ViewHolder(
        private val binding: ListItemMapBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(listener: View.OnClickListener, item: MapLocation) {
            binding.apply {
                clickListener = listener
                location = item
                executePendingBindings()
            }
        }
    }
}

class MapLocationDiffCallback : DiffUtil.ItemCallback<MapLocation>() {

    override fun areItemsTheSame(
        oldItem: MapLocation,
        newItem: MapLocation
    ): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(
        oldItem: MapLocation,
        newItem: MapLocation
    ): Boolean {
        return oldItem == newItem
    }
}

@BindingAdapter("isGone")
fun bindIsGone(view: View, isGone: Boolean) {
    view.visibility = if (isGone) {
        View.GONE
    } else {
        View.VISIBLE
    }
}

@BindingAdapter("imageFromUrl")
fun bindImageFromUrl(view: ImageView, imageUrl: String?) {
    if (!imageUrl.isNullOrEmpty()) {
        Picasso.with(view.context)
            .load(imageUrl)
            //.error(R.drawable.failure)
            .into(view)
    }
}