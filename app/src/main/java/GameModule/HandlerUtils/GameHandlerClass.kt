package GameModule.HandlerUtils

class GameHandlerClass : PauseHandler() {

    fun postDelayedClass(r: Runnable, delayMillis: Long) {
        if (paused) {
            addMessage(r, delayMillis, null)
        } else {
            super.postDelayed(r, delayMillis)
        }
    }

    fun postClass(r: Runnable) {
        if (paused) {
            addMessage(r, 0, null)
        } else {
            super.post(r)
        }
    }

}