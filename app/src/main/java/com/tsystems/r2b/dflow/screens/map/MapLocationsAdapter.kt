package com.tsystems.r2b.dflow.screens.map

import android.content.Context
import android.graphics.PointF
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.*
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
            //.fit()
            //.error(R.drawable.failure)
            .into(view)
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