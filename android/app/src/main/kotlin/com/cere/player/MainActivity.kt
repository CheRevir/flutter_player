package com.cere.player

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Bundle
import android.util.Log
import io.flutter.embedding.android.FlutterActivity
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel

class MainActivity : FlutterActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("TAG", "MainActivity -> onCreate: ")

        MethodChannel(this.flutterEngine?.dartExecutor, Constants.CHANNEL).setMethodCallHandler(object : MethodChannel.MethodCallHandler {
            override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
                Log.e("TAG", "MainActivity -> onMethodCall: $call")
                Log.e("TAG", "MainActivity -> onMethodCall: $result")
                when (call.method) {
                    "getBatteryLevel" -> {
                        result.success(getBatteryLevel())
                    }
                    else -> result.notImplemented()
                }
            }
        })

        EventChannel(this.flutterEngine?.dartExecutor, Constants.CHANNEL_BATTERY).setStreamHandler(object : EventChannel.StreamHandler {
            var broadcastReceiver: BroadcastReceiver? = null

            override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
                Log.e("TAG", "MainActivity -> onListen: $arguments")
                Log.e("TAG", "MainActivity -> onListen: $events")
                broadcastReceiver = object : BroadcastReceiver() {
                    override fun onReceive(context: Context?, intent: Intent?) {
                        val status = intent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
                        if (status != BatteryManager.BATTERY_STATUS_UNKNOWN) {
                            events?.error("errorCode", "errorMessage", "errorDetails")
                        } else {
                            val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1;
                            events?.success(level)
                        }
                    }
                }
                registerReceiver(broadcastReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            }

            override fun onCancel(arguments: Any?) {
                Log.e("TAG", "MainActivity -> onCancel: $arguments")
                unregisterReceiver(broadcastReceiver)
            }
        })
    }

    private fun getBatteryLevel(): Int {
        val batteryManager: BatteryManager = this.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        return batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
    }
}
