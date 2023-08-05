package GameModule.PurchaseUtiles

interface PurchaseListener {

    fun onSkuDetailsResponse(purchaseDataList: ArrayList<PurchaseData>)

    fun onPurchasesAlreadyOwned()

    fun onPurchasesCanceled()

    fun onPurchasesUpdated(purchaseData: PurchaseData, quantity: Int)

}