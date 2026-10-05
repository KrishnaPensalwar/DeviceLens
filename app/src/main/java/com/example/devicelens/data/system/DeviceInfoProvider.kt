package com.example.devicelens.data.system

import android.app.ActivityManager
import android.content.Context
import android.content.res.Configuration
import android.graphics.Point
import android.hardware.Sensor
import android.hardware.SensorManager
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.media.MediaRecorder
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.provider.Settings
import android.util.Size
import android.view.WindowManager
import com.example.devicelens.domain.model.BatteryStatus
import com.example.devicelens.domain.model.CameraInfo
import com.example.devicelens.domain.model.DeviceBatterySnapshot
import com.example.devicelens.domain.model.DeviceInfo
import com.example.devicelens.domain.model.DeviceSensor
import com.example.devicelens.domain.model.DeviceType
import com.example.devicelens.domain.model.DisplayInfo
import com.example.devicelens.domain.model.MemorySnapshot
import com.example.devicelens.domain.model.StorageSnapshot
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlin.math.sqrt

class DeviceInfoProvider @Inject constructor(
    @ApplicationContext private val context: Context,
    private val batteryManagerProvider: BatteryManagerProvider
) {

    fun getDeviceInfo(): DeviceInfo {
        val manufacturer = Build.MANUFACTURER.replaceFirstChar { it.uppercase() }
        val model = Build.MODEL.ifBlank { "Not available" }
        return DeviceInfo(
            manufacturer = manufacturer.ifBlank { "Not available" },
            model = model,
            brand = Build.BRAND.replaceFirstChar { it.uppercase() }.ifBlank { manufacturer },
            board = Build.BOARD.ifBlank { "Not available" },
            hardware = Build.HARDWARE.ifBlank { "Not available" },
            deviceName = getDeviceName().ifBlank { model },
            deviceType = getDeviceType(),
            androidVersion = Build.VERSION.RELEASE.ifBlank { "Not available" },
            sdkInt = Build.VERSION.SDK_INT,
            securityPatch = Build.VERSION.SECURITY_PATCH.takeIf { it.isNotBlank() },
            buildId = Build.DISPLAY.ifBlank { Build.ID }.ifBlank { "Not available" },
            memory = getMemory(),
            storage = getStorage(),
            display = getDisplay(),
            densityDpi = context.resources.displayMetrics.densityDpi,
            camera = getCamera(),
            battery = getBatterySnapshot(),
            sensors = getSensors()
        )
    }

    private fun getDeviceName(): String {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N_MR1) return ""
        val fromSettings = Settings.Global.getString(
            context.contentResolver,
            Settings.Global.DEVICE_NAME
        )
        return fromSettings.orEmpty().trim()
    }

    private fun getDeviceType(): DeviceType {
        val screenLayout = context.resources.configuration.screenLayout and
            Configuration.SCREENLAYOUT_SIZE_MASK
        return if (screenLayout >= Configuration.SCREENLAYOUT_SIZE_LARGE) {
            DeviceType.TABLET
        } else {
            DeviceType.PHONE
        }
    }

    private fun getMemory(): MemorySnapshot {
        val activityManager =
            context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)

        val total = memoryInfo.totalMem.coerceAtLeast(0L)
        val available = memoryInfo.availMem.coerceIn(0L, total)
        val used = (total - available).coerceAtLeast(0L)
        val percent = if (total > 0) ((used * 100) / total).toInt() else 0

        return MemorySnapshot(
            totalBytes = total,
            usedBytes = used,
            availableBytes = available,
            usagePercent = percent.coerceIn(0, 100)
        )
    }

    private fun getStorage(): StorageSnapshot {
        val stat = StatFs(Environment.getDataDirectory().path)
        val total = stat.totalBytes.coerceAtLeast(0L)
        val available = stat.availableBytes.coerceIn(0L, total)
        val used = (total - available).coerceAtLeast(0L)
        val percent = if (total > 0) ((used * 100) / total).toInt() else 0

        return StorageSnapshot(
            totalBytes = total,
            usedBytes = used,
            availableBytes = available,
            usagePercent = percent.coerceIn(0, 100)
        )
    }

    private fun getDisplay(): DisplayInfo {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val width: Int
        val height: Int
        val refreshRate: Float

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val bounds = windowManager.maximumWindowMetrics.bounds
            width = bounds.width()
            height = bounds.height()

            val displayManager =
                context.getSystemService(Context.DISPLAY_SERVICE) as android.hardware.display.DisplayManager

            refreshRate = displayManager
                .getDisplay(android.view.Display.DEFAULT_DISPLAY)
                ?.refreshRate
                ?: 60f
        } else {
            @Suppress("DEPRECATION")
            val display = windowManager.defaultDisplay
            val size = Point()
            @Suppress("DEPRECATION")
            display.getRealSize(size)
            width = size.x
            height = size.y
            @Suppress("DEPRECATION")
            refreshRate = display.refreshRate
        }

        return DisplayInfo(
            screenSizeInches = getScreenSizeInches(),
            resolutionWidth = width,
            resolutionHeight = height,
            refreshRateHz = refreshRate.toInt().coerceAtLeast(1)
        )
    }

    private fun getScreenSizeInches(): Double? {
        val metrics = context.resources.displayMetrics
        val widthInches = metrics.widthPixels / metrics.xdpi.toDouble()
        val heightInches = metrics.heightPixels / metrics.ydpi.toDouble()
        if (!widthInches.isFinite() || !heightInches.isFinite()) return null
        val diagonal = sqrt(widthInches * widthInches + heightInches * heightInches)
        return if (diagonal in 1.0..20.0) diagonal else null
    }

    private fun getBatterySnapshot(): DeviceBatterySnapshot {
        val battery = batteryManagerProvider.getBatteryInfo()
        val percent = battery.level.takeIf { it in 0..100 }
        val capacity = battery.capacity?.takeIf { it in 800..20_000 }
        return DeviceBatterySnapshot(
            percent = percent,
            isCharging = battery.isCharging || battery.batteryStatus == BatteryStatus.FULL,
            status = battery.batteryStatus,
            temperatureC = battery.temperature,
            capacityMah = capacity
        )
    }

    private fun getCamera(): CameraInfo {
        val manager = context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager
            ?: return CameraInfo(null, null, null)

        var rearMp = 0f
        var frontMp = 0f
        var bestVideo: Size? = null

        val ids = runCatching { manager.cameraIdList }.getOrDefault(emptyArray())
        for (id in ids) {
            val characteristics = runCatching { manager.getCameraCharacteristics(id) }.getOrNull()
                ?: continue
            val pixels = characteristics.get(CameraCharacteristics.SENSOR_INFO_PIXEL_ARRAY_SIZE)
            val megapixels = pixels?.let { it.width.toFloat() * it.height / 1_000_000f } ?: 0f
            when (characteristics.get(CameraCharacteristics.LENS_FACING)) {
                CameraCharacteristics.LENS_FACING_BACK -> if (megapixels > rearMp) rearMp = megapixels
                CameraCharacteristics.LENS_FACING_FRONT -> if (megapixels > frontMp) frontMp = megapixels
            }
            val sizes = characteristics
                .get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                ?.getOutputSizes(MediaRecorder::class.java)
                .orEmpty()
            val largest = sizes.maxByOrNull { it.width.toLong() * it.height } ?: continue
            val current = bestVideo
            if (current == null || largest.width.toLong() * largest.height > current.width.toLong() * current.height) {
                bestVideo = largest
            }
        }

        return CameraInfo(
            rear = rearMp.takeIf { it > 0f }?.let(::formatMegapixels),
            front = frontMp.takeIf { it > 0f }?.let(::formatMegapixels),
            video = bestVideo?.let(::formatVideo)
        )
    }

    private fun formatMegapixels(megapixels: Float): String {
        val rounded = if (megapixels >= 10f) {
            "%.0f".format(megapixels)
        } else {
            "%.1f".format(megapixels)
        }
        return "$rounded MP"
    }

    private fun formatVideo(size: Size): String {
        val shortSide = minOf(size.width, size.height)
        val label = when {
            shortSide >= 2160 -> "4K"
            shortSide >= 1440 -> "1440p"
            shortSide >= 1080 -> "1080p"
            shortSide >= 720 -> "720p"
            else -> return "${size.width} × ${size.height}"
        }
        return "$label (${size.width} × ${size.height})"
    }

    private fun getSensors(): List<DeviceSensor> {
        val sensorManager =
            context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        return listOf(
            DeviceSensor("Accelerometer", sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null),
            DeviceSensor("Gyroscope", sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null),
            DeviceSensor("Proximity", sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY) != null),
            DeviceSensor("Light sensor", sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) != null),
            DeviceSensor("Magnetometer", sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null)
        )
    }
}
