package GameModule.PurchaseUtiles

data class PurchaseData(
    val sku: String,
    val reward: Long,
    var priceString: String,
    var skuDetails: Any? = null,
    var isSpacialOffer: Boolean = false
) {
    override fun toString(): String {
        return "sku: $sku    |    reward: $reward    |    skuDetails: $skuDetails "
    }
}
