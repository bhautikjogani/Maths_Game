package AdsUtiles

import android.view.View
import android.view.ViewGroup
import com.google.android.gms.ads.AdView

class BannerAdModel(var adView: AdView) {
    fun onResume() {
        adView.resume()
        setVisibility(View.VISIBLE)
    }

    fun onPause() {
        adView.pause()
        setVisibility(View.INVISIBLE)
    }

    fun setVisibility(visibility: Int) {
        (adView as ViewGroup).visibility = visibility
    }
}
