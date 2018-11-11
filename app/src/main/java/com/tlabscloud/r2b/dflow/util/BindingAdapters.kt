package com.tlabscloud.r2b.dflow.util

import android.text.SpannableStringBuilder
import android.widget.ImageView
import android.widget.TextView
import androidx.core.text.bold
import androidx.databinding.BindingAdapter
import com.squareup.picasso.Picasso
import com.tlabscloud.r2b.dflow.R
import com.tlabscloud.r2b.dflow.model.Vehicle
import com.tlabscloud.r2b.dflow.model.VehicleTariff


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

@BindingAdapter("vehicleTariffShortText")
fun bindVehicleTariffShortText(textView: TextView, vehicleTariff: VehicleTariff) {
    val resources = textView.context.resources
    val tariffPerMinute = resources.getString(R.string.tariff_per_minute)
    val builder = SpannableStringBuilder()
        .bold { append("${vehicleTariff.pricePerMinute} ${vehicleTariff.currency} ") }
        .append(tariffPerMinute)
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