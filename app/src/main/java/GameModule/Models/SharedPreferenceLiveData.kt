package GameModule.Models

import GameModule.GamePreference
import android.content.SharedPreferences
import androidx.lifecycle.LiveData

open class SharedPreferenceLiveData<Any>(
    private val key: String,
    private val defValue: Any
) : LiveData<Any>() {

    init {
        value = this.getValueFromPreferences(key, defValue)
    }

    private val preferenceChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
            if (key == this.key) {
                value = getValueFromPreferences(key, defValue)
            }
        }

    private fun getValueFromPreferences(key: String, defValue: Any): Any {
        return (when (defValue) {
            is String -> GamePreference.getString(key)
            is Int -> GamePreference.getInteger(key)
            is Float -> GamePreference.getFloat(key)
            is Long -> GamePreference.getLong(key)
            is Boolean -> GamePreference.getBoolean(key)
            else -> GamePreference.preferences.all[key]

        }) as Any
    }

    override fun onActive() {
        super.onActive()
        value = getValueFromPreferences(key, defValue)
        GamePreference.preferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener)
    }

    override fun onInactive() {
        GamePreference.preferences.unregisterOnSharedPreferenceChangeListener(
            preferenceChangeListener
        )
        super.onInactive()
    }

}
