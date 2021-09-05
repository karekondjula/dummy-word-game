package com.rubean.user

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.os.RemoteException
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.rubean.bot.IBotCallback
import com.rubean.bot.IBotService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

interface ServiceManager : LifecycleObserver {
    val botMoveFlow: Flow<String>

    fun nextUserMove(move: String)
}

class ServiceManagerImpl(private val context: Context) : ServiceManager {

    private var botService: IBotService? = null

    override val botMoveFlow: MutableSharedFlow<String> = MutableSharedFlow(replay = 1)

    private val botCallback = object : IBotCallback.Stub() {
        override fun nextBotMove(move: String) {
            botMoveFlow.tryEmit(move)
        }
    }

    private val connection = object : ServiceConnection {

        override fun onServiceConnected(componentName: ComponentName, binder: IBinder) {
            botService = IBotService.Stub.asInterface(binder)

            try {
                botService?.registerCallback(botCallback)
            } catch (e: RemoteException) {
            }
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        Intent(MainActivity.BOT_ACTION).also { intent ->
            intent.setPackage(MainActivity.BOT_PACKAGE)
            context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        context.unbindService(connection)
    }

    override fun nextUserMove(move: String) {
        botService?.nextUserMove(move)
    }
}