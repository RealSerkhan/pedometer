package com.example.pedometer

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Looper
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.EventChannel
import android.os.Handler

class SensorStreamHandler() : EventChannel.StreamHandler {

    var stepCount :Int=0
    var MagnitidePrevious :Double= 0.0


    private var sensorEventListener: SensorEventListener? = null
    private var sensorManager: SensorManager? = null
    private var sensor: Sensor? = null
    private lateinit var context: Context
    private lateinit var sensorName: String
    private lateinit var flutterPluginBinding: FlutterPlugin.FlutterPluginBinding

    constructor(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding, sensorType: Int) : this() {
        this.context = flutterPluginBinding.applicationContext
        this.sensorName = if (sensorType == Sensor.TYPE_STEP_COUNTER) "StepCount" else "StepDetection"
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensor = sensorManager!!.getDefaultSensor(sensorType)
        this.flutterPluginBinding = flutterPluginBinding
    }

    override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
        if(sensor==null){
            sensor = sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            sensorEventListener = accelemeterSensorEventListener(events!!);
            sensorManager!!.registerListener(sensorEventListener,
                sensor, SensorManager.SENSOR_DELAY_FASTEST);
        }
        if (sensor == null) {
            events!!.error("1", "$sensorName not available",
                    "$sensorName is not available on this device")
        } else {
            sensorEventListener = sensorEventListener(events!!);
            sensorManager!!.registerListener(sensorEventListener,
                    sensor, SensorManager.SENSOR_DELAY_FASTEST);
        }
    }

    override fun onCancel(arguments: Any?) {
        sensorManager!!.unregisterListener(sensorEventListener);
    }
    fun accelemeterSensorEventListener(events: EventChannel.EventSink): SensorEventListener? {
        return object : SensorEventListener {

            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}

            override fun onSensorChanged(event: SensorEvent) {
                var prevStepCount: Int =stepCount
                val x_acceleration: Float = event.values[0]
                val y_acceleration: Float = event.values[1]
                val z_acceleration: Float = event.values[2]
                var Magnitude: Double= Math.sqrt((x_acceleration*x_acceleration+y_acceleration*y_acceleration+z_acceleration*z_acceleration).toDouble());
                var MagnitudeDelta:Double=Magnitude-MagnitidePrevious
                MagnitidePrevious=Magnitude;
                if(MagnitudeDelta>6){
                    stepCount++
                }
//                if(stepCount>prevStepCount){
                    events.success(stepCount)
//                }
            }
        }
    }


}