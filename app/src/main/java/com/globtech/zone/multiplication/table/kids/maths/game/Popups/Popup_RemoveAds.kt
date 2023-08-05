package com.globtech.zone.multiplication.table.kids.maths.game.Popups

import GameModule.Base.BaseDialog
import GameModule.GamePreference
import GameModule.PrefKey
import GameModule.PurchaseUtiles.PurchaseData
import GameModule.PurchaseUtiles.PurchaseHelper
import GameModule.PurchaseUtiles.PurchaseListener
import android.app.Activity
import android.util.Log
import android.view.View
import com.globtech.zone.multiplication.table.kids.maths.game.databinding.PopupRemoveAdsBinding

class Popup_RemoveAds(
    activity: Activity,
    val binding: PopupRemoveAdsBinding = PopupRemoveAdsBinding.inflate(activity.layoutInflater)
) : BaseDialog(activity, binding.root), View.OnClickListener, PurchaseListener {

    var purchaseHelper: PurchaseHelper
    var purchaseData = PurchaseData(PurchaseHelper.RemoveAdKey, -1, PurchaseHelper.RemoveAdKey)

    init {
        val list = ArrayList<PurchaseData>()
        list.add(purchaseData)
        purchaseHelper = PurchaseHelper(activity, list, binding.frmParent, this)

        binding.clickHandle = this

        show()

    }

    override fun onClick(view: View) {
        if (isDoubleClick()) return

        when (view) {
            binding.btnClose -> dismiss()
            binding.btnPurchase -> purchaseHelper.startPurchase(purchaseData)
        }

    }

    override fun onSkuDetailsResponse(purchaseDataList: ArrayList<PurchaseData>) {
        if (activity.isFinishing) return
        if (purchaseDataList.isNotEmpty()) {
            val pData = purchaseDataList.first()
            if (pData.skuDetails != null) {
                binding.btnPurchase.text = pData.priceString
                binding.progressbar.visibility = View.GONE
            }
        }
    }

    override fun onPurchasesAlreadyOwned() {
        Log.d(TAG, "onPurchasesAlreadyOwned: ")
        GamePreference.setBoolean(PrefKey.isRemoveAds, true)
        Popup_Conformation(activity = activity)
            .setDialogTitle("Congrats")
            .setDialogMsg("Ads removed successfully")
            .setButtonRight("OK") {
                purchaseHelper.destroyBillingClient()
                dismiss()
            }
    }

    override fun onPurchasesCanceled() {
        Log.d(TAG, "onPurchasesCanceled: ")
        Popup_Conformation(activity = activity)
            .setDialogTitle("Alert")
            .setDialogMsg("unable to purchase")
            .setButtonRight("OK")
    }

    override fun onPurchasesUpdated(purchaseData: PurchaseData, quantity: Int) {
        Log.d(TAG, "onPurchasesUpdated:  purchaseData ($quantity)  -->  $purchaseData")
        Popup_Conformation(activity = activity)
            .setDialogTitle("Congrats")
            .setDialogMsg("congrats! ${purchaseData.reward * quantity} coins purchased successfully")
            .setButtonRight("OK")
    }

}
