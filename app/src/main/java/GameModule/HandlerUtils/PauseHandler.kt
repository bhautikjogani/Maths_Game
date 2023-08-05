package GameModule.HandlerUtils

import android.os.Handler
import android.os.Looper
import android.os.Message
import java.util.Vector

abstract class PauseHandler internal constructor() :
    Handler(Looper.getMainLooper()) {

    fun SendMessageClass(message: Message?) {
        if (paused) {
            addMessage(null, 0, message)
        } else {
            sendMessage(message!!)
        }
    }

    /*
     * Message Queue Buffer
     */
    private val messageQueueBuffer = Vector<Message?>()
    private val runnableQueueBuffer = ArrayList<Runnable?>()
    private val timeQueueBuffer = ArrayList<Long>()

    /*
     * Flag indicating the pause state
     */
    @JvmField
    var paused = false

    /*
     * Resume the handler
     */
    fun resume() {
        paused = false
        while (messageQueueBuffer.size > 0) {
            val r = runnableQueueBuffer[0]
            runnableQueueBuffer.removeAt(0)
            val time = timeQueueBuffer[0]
            timeQueueBuffer.removeAt(0)
            val msg = messageQueueBuffer.elementAt(0)
            messageQueueBuffer.removeElementAt(0)
            if (msg == null) {
                this.postDelayed(r!!, time)
            } else {
                sendMessage(msg)
            }
        }
    }

    /*
     * Pause the handler
     */
    fun pause() {
        paused = true
    }

    /*
    * Add incoming message to messageQueue
    */
    fun addMessage(r: Runnable?, delay: Long, msg: Message?) {
        if (msg == null) {
            messageQueueBuffer.add(null)
        } else {
            val msgCopy = Message()
            msgCopy.copyFrom(msg)
            messageQueueBuffer.add(msgCopy)
        }
        runnableQueueBuffer.add(r)
        timeQueueBuffer.add(delay)
    }
}