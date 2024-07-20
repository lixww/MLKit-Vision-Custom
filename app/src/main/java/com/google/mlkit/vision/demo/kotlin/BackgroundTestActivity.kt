package com.google.mlkit.vision.demo.kotlin

import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.mlkit.vision.demo.R
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.android.asCoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors
import kotlin.coroutines.coroutineContext

class BackgroundTestActivity : AppCompatActivity() {

    private val scope = CoroutineScope(Job() + Dispatchers.IO)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_test_background)

        // use for viewing extension functions
        // executor service -> coroutine dispatcher
        Executors.newSingleThreadExecutor().asCoroutineDispatcher()
        val myThread = HandlerThread("myHandler")
        myThread.start()
        val myHandler = Handler(myThread.looper)
        // handler -> coroutine dispatcher
        myHandler.asCoroutineDispatcher()
        myThread.quit()

//        runBlocking {
//
//        }

        val job1 = scope.async(start = CoroutineStart.LAZY) {
            log("job1 async")
        }
        val job2 = scope.launch {
            log("job2 launch")
            job1.await()

        }

    }


    // use for viewing decompiled java
    suspend fun test() {
        log("test1")
        withContext(Dispatchers.IO) {
            log("test2-1 in IO coroutine")
            Thread.sleep(2000L) //manually delay
            log("test2-2 in IO coroutine")
        }
        log("test3")
    }

    // use for viewing decompiled java
    suspend fun fakeSuspendTest() {
        log("test1 for fakeSuspend")
    }

    private fun log(message: String) {
        Log.d("abc.kt", message)
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}