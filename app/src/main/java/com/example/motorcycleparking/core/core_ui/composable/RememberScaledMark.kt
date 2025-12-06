package com.example.motorcycleparking.core.core_ui.composable

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory

/**
 * Creates a cached, scaled BitmapDescriptor for Google Maps markers.
 * Result is remembered and only recomputed when resId or sizePx changes.
 *
 * @param resId Drawable resource to decode and scale
 * @param sizePx Target size in pixels for square bitmap dimensions
 * @return Scaled BitmapDescriptor suitable for Google Maps Marker icons
 */
@Composable
fun rememberScaledMarker(
    @DrawableRes resId: Int,
    sizePx: Int = 96 // size in *pixels*
): BitmapDescriptor {
    val context = LocalContext.current

    return remember(resId, sizePx) {
        val original = BitmapFactory.decodeResource(context.resources, resId)

        val scaled = Bitmap.createScaledBitmap(
            original,
            sizePx,
            sizePx,
            true
        )

        BitmapDescriptorFactory.fromBitmap(scaled)
    }
}