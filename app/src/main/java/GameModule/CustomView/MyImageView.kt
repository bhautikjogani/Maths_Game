package GameModule.CustomView

import android.content.Context
import android.util.AttributeSet

class MyImageView (
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : androidx.appcompat.widget.AppCompatImageView(context, attrs, defStyle) {

    var stringSrc: String? = ""
        set(value) {
//            Log.d("_JB_", "MyImageView:   value  --->   $value")
            field = value
            if (value?.isNotEmpty() == true) {
                this.setImageResource(
                    context.resources.getIdentifier(
                        value,
                        "drawable",
                        context.packageName
                    )
                )
            }
        }

}