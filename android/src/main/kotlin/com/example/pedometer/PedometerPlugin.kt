import android.Manifest
import android.app.Activity
import androidx.annotation.NonNull
import android.hardware.Sensor
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import androidx.core.app.ActivityCompat
import com.example.pedometer.SensorStreamHandler

import io.flutter.plugin.common.EventChannel

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import android.content.pm.PackageManager
/** PedometerPlugin */
class PedometerPlugin : FlutterPlugin {
    private lateinit var stepDetectionChannel: EventChannel
    private lateinit var stepCountChannel: EventChannel

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
       val pm: PackageManager = flutterPluginBinding.applicationContext.packageManager
    if (pm.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_COUNTER)) {
   print(message = "is avaliable")
    }
      else{
          print("not avaliable")
    }
    /// Create channels
    stepDetectionChannel = EventChannel(flutterPluginBinding.binaryMessenger, "step_detection")
    stepCountChannel = EventChannel(flutterPluginBinding.binaryMessenger, "step_count")

    /// Create handlers
    val stepDetectionHandler = SensorStreamHandler(flutterPluginBinding, Sensor.TYPE_STEP_DETECTOR)
    val stepCountHandler = SensorStreamHandler(flutterPluginBinding, Sensor.TYPE_STEP_COUNTER)

    /// Set handlers
    stepDetectionChannel.setStreamHandler(stepDetectionHandler)
    stepCountChannel.setStreamHandler(stepCountHandler)
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    stepDetectionChannel.setStreamHandler(null)
    stepCountChannel.setStreamHandler(null)
  }

  override fun onMethodCall(call: MethodCall, result: Result) {
    TODO("Not yet implemented")
  }

}
