package tech.zemn.mobile.player

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import tech.zemn.mobile.Constants
import timber.log.Timber

class ZemnBroadcastReceiver: BroadcastReceiver() {

    companion object {
        const val AUDIO_CONTROL = "audio_control"
        const val ZEMN_PLAYER_PAUSE_PLAY = Constants.PACKAGE_NAME + ".ACTION_PAUSE"
        const val ZEMN_PLAYER_NEXT = Constants.PACKAGE_NAME + ".ACTION_NEXT"
        const val ZEMN_PLAYER_PREVIOUS = Constants.PACKAGE_NAME + ".ACTION_PREVIOUS"
        const val ZEMN_PLAYER_CANCEL = Constants.PACKAGE_NAME + ".ACTION_CANCEL"
        const val PAUSE_PLAY_ACTION_REQUEST_CODE = 1001
        const val NEXT_ACTION_REQUEST_CODE = 1002
        const val PREVIOUS_ACTION_REQUEST_CODE = 1003
        const val CANCEL_ACTION_REQUEST_CODE = 1004
    }

    private var callback: Callback? = null

    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.extras?.getString(AUDIO_CONTROL) ?: return
        when(action){
            ZEMN_PLAYER_NEXT -> callback?.onBroadcastNext()
            ZEMN_PLAYER_PAUSE_PLAY -> callback?.onBroadcastPausePlay()
            ZEMN_PLAYER_PREVIOUS -> callback?.onBroadcastPrevious()
            else -> {
                Timber.d("no action matched -> $action")
            }
        }
    }

    fun startListening(callback: Callback) {
        this.callback = callback
    }

    fun stopListening() {
        this.callback = null
    }

    interface Callback {
        fun onBroadcastPausePlay()
        fun onBroadcastNext()
        fun onBroadcastPrevious()
    }
}