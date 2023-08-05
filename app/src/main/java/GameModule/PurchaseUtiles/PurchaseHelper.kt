package GameModule.PurchaseUtiles

import GameModule.Utils
import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.multidex.BuildConfig
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.AcknowledgePurchaseResponseListener
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.ConsumeResponseListener
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetails
import com.android.billingclient.api.SkuDetailsParams
import com.globtech.zone.multiplication.table.kids.maths.game.MyGame
import com.globtech.zone.multiplication.table.kids.maths.game.R

class PurchaseHelper(
    var activity: Activity,
    var purchaseDataList: ArrayList<PurchaseData>,
    var parent: ViewGroup,
    var purchaseListener: PurchaseListener?
) {


    companion object {
        const val RemoveAdKey = "removeads"
        const val Pack1 = "pack1"
        const val Pack2 = "pack2"
        const val Pack3 = "pack3"
        const val Pack4 = "pack4"
        const val Pack5 = "pack5"
    }

    private val TAG: String = MyGame.MainTAG + javaClass.simpleName
    private lateinit var purchaseData: PurchaseData
    private var loaderFrame: ViewGroup? = null

    private var billingClient: BillingClient =
        BillingClient.newBuilder(activity).setListener { result, purchaseList ->
            Log.d(TAG, "onPurchasesUpdated:     ---->   ${result.responseCode}")
            if (result.responseCode == BillingClient.BillingResponseCode.OK && purchaseList != null) {
                for (purchase in purchaseList) handlePurchase(purchase)
            }
            /**/
            else if (result.responseCode == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
                activity.runOnUiThread {
                    hideLoaderFrame()
                    purchaseListener?.onPurchasesAlreadyOwned()
                }
            }
            /**/
            else {
                activity.runOnUiThread {
                    purchaseListener?.onPurchasesCanceled()
                    hideLoaderFrame()
                }
            }
        }.enablePendingPurchases().build()


    init {

        Log.d(TAG, "Init:    $purchaseDataList")

        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                Log.d(TAG, "onBillingSetupFinished:    --->   ${billingResult.responseCode}")
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) querySkuDetails()
            }

            override fun onBillingServiceDisconnected() {
                Log.d(TAG, "onBillingServiceDisconnected: ")
            }
        })

    }

    private fun querySkuDetails() {
        Log.d(TAG, "querySkuDetails: purchaseDataList  -->  $purchaseDataList")
        val skuList = ArrayList<String>()
        purchaseDataList.forEach { skuList.add(it.sku) }

        val params =
            SkuDetailsParams.newBuilder().setSkusList(skuList).setType(BillingClient.SkuType.INAPP)

        billingClient.querySkuDetailsAsync(
            params.build()
        ) { billingResult, skuList ->
            Log.d(
                TAG, "querySkuDetails:     --->     ${billingResult.responseCode}    |    $skuList"
            )
            skuList?.forEachIndexed { index, skuDetails ->
                Log.d(TAG, "querySkuDetails:  ($index)   -->   $skuDetails")
            }
            purchaseDataList.forEachIndexed { index, purchaseData ->
                skuList?.forEach { skuData ->
                    if (purchaseData.sku == skuData.sku) {
                        purchaseData.priceString = skuData.price
                        purchaseData.skuDetails = skuData
                        return@forEach
                    }
                }
            }
            queryPurchaseHistory()
        }
    }

    private fun queryPurchaseHistory() {
        Log.d(TAG, "queryPurchaseHistory: ")
        var counter = 0
        billingClient.queryPurchaseHistoryAsync(
            BillingClient.SkuType.INAPP
        ) { billingResult, skuList ->
            Log.d(
                TAG,
                "queryPurchaseHistory:    --->   ${billingResult.responseCode}    ----->    $skuList"
            )
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                var isSendData = true
                skuList?.forEachIndexed { index, it ->

                    Log.d(TAG, "queryPurchaseHistory:  ********>>>>>    $it")

                    if (!it.skus[it.skus.lastIndex].contains(RemoveAdKey)) {
                        counter++
                        isSendData = false
                        billingClient.acknowledgePurchase(
                            AcknowledgePurchaseParams.newBuilder()
                                .setPurchaseToken(
                                    it.skus[it.skus.lastIndex].contains(RemoveAdKey).toString()
                                ).build(), object : AcknowledgePurchaseResponseListener {
                                override fun onAcknowledgePurchaseResponse(billingResult: BillingResult) {
                                    Log.d(TAG, "onAcknowledgePurchaseResponse: ")

                                    billingClient.consumeAsync(
                                        ConsumeParams.newBuilder()
                                            .setPurchaseToken(it.purchaseToken).build(),
                                        object : ConsumeResponseListener {
                                            override fun onConsumeResponse(
                                                billingResult: BillingResult, s: String
                                            ) {
                                                Log.d(TAG, "onConsumeResponse:    --->  $s")
                                                counter--
                                                if (counter == 0) activity.runOnUiThread {
                                                    purchaseListener?.onSkuDetailsResponse(
                                                        purchaseDataList
                                                    )
                                                }
                                            }
                                        })
                                }
                            })
                    }

                }
                if (isSendData) activity.runOnUiThread {
                    purchaseListener?.onSkuDetailsResponse(purchaseDataList)
                }
            }
        }
    }

    private fun consumePurchase(skuToken: String) {
        Log.d(TAG, "consumePurchase: ")
        billingClient.consumeAsync(ConsumeParams.newBuilder().setPurchaseToken(skuToken).build(),
            object : ConsumeResponseListener {
                override fun onConsumeResponse(billingResult: BillingResult, s: String) {
                    Log.d(TAG, "onConsumeResponse:    --->    $s")
                    hideLoaderFrame()
                }
            })
    }

    fun destroyBillingClient() {
        Log.d(TAG, "destroyBillingClient: ")
        billingClient.endConnection()
    }

    fun startPurchase(purchaseData: PurchaseData) {
        Log.d(TAG, "startPurchase: ")
        if (BuildConfig.DEBUG) {
            purchaseListener?.onPurchasesUpdated(purchaseData, 1)
            return
        }
        if (!Utils.isNetworkAvailable(activity)) {
            Toast.makeText(
                activity, activity.resources.getString(R.string.NoConnection), Toast.LENGTH_SHORT
            ).show()
            return
        }
        if (purchaseData.skuDetails == null) {
            Toast.makeText(
                activity, activity.resources.getString(R.string.SomethingWrong), Toast.LENGTH_SHORT
            ).show()
            return
        }
        showLoaderFrame()
        this@PurchaseHelper.purchaseData = purchaseData
        billingClient.launchBillingFlow(
            activity,
            BillingFlowParams.newBuilder().setSkuDetails(purchaseData.skuDetails as SkuDetails)
                .build()
        )
    }

    private fun handlePurchase(purchase: Purchase) {
        Log.d(TAG, "handlePurchase: ")
        if (purchase.purchaseState != Purchase.PurchaseState.PURCHASED) return
        if (purchase.isAcknowledged) return
        billingClient.acknowledgePurchase(
            AcknowledgePurchaseParams.newBuilder().setPurchaseToken(purchase.purchaseToken).build()
        ) {
            if (purchase.skus[purchase.skus.lastIndex].contains(RemoveAdKey)) {
                activity.runOnUiThread {
                    hideLoaderFrame()
                    purchaseListener?.onPurchasesAlreadyOwned()
                }
            }
            /**/
            else {
                consumePurchase(purchase.purchaseToken)
                activity.runOnUiThread {
                    purchaseListener?.onPurchasesUpdated(purchaseData, purchase.quantity)
                }
            }
        }
    }

    //region: Loader Frame
    private fun initLoaderFrame() {
        loaderFrame = FrameLayout(parent.context)
        loaderFrame?.setBackgroundColor(ContextCompat.getColor(activity, R.color.black40))
        loaderFrame?.setOnClickListener { }
        (loaderFrame as ViewGroup).addView(
            ProgressBar(parent.context).apply {
                scaleX = .15.toFloat()
                scaleY = .15.toFloat()
            }, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        loaderFrame?.visibility = View.GONE
        parent.addView(
            loaderFrame, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
    }

    private fun showLoaderFrame() {
        if (loaderFrame == null) initLoaderFrame()
        loaderFrame?.bringToFront()
        loaderFrame?.visibility = View.VISIBLE
    }

    private fun hideLoaderFrame() {
        Log.d(TAG, "hideLoaderFrame:   1")
        Handler(Looper.getMainLooper()).postDelayed({
            Log.d(TAG, "hideLoaderFrame:   2")
            loaderFrame?.visibility = View.GONE
        }, 1000)
    }
    //endregion

}
