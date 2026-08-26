package com.example.devicelens.data.system

import android.app.usage.StorageStatsManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.os.Process
import android.os.StatFs
import android.os.storage.StorageManager
import dagger.hilt.android.qualifiers.ApplicationContext
import com.example.devicelens.domain.model.AppStorageInfo
import com.example.devicelens.domain.model.StorageOverview
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID
import javax.inject.Inject

class StorageStatsProvider @Inject constructor(
    @ApplicationContext private val context: Context
) {

    suspend fun getStorageOverview(
        apps: List<AppStorageInfo>
    ): StorageOverview = withContext(Dispatchers.IO) {

        val snapshot = deviceSnapshot()

        val applicationsBytes = sumKnown(apps) { it.apkBytes }
        val userDataBytes = sumIfComplete(apps) { it.dataBytes }
        val cacheBytes = sumIfComplete(apps) { it.cacheBytes }

        val otherBytes =
            if (
                applicationsBytes != null &&
                userDataBytes != null &&
                cacheBytes != null
            ) {
                (
                        snapshot.usedBytes -
                                applicationsBytes -
                                userDataBytes -
                                cacheBytes
                        ).coerceAtLeast(0L)
            } else {
                null
            }

        StorageOverview(
            totalBytes = snapshot.totalBytes,
            usedBytes = snapshot.usedBytes,
            freeBytes = snapshot.freeBytes,
            usagePercent = snapshot.usagePercent,
            applicationsBytes = applicationsBytes,
            userDataBytes = userDataBytes,
            cacheBytes = cacheBytes,
            otherBytes = otherBytes
        )
    }

    suspend fun getAppStorage(): List<AppStorageInfo> =
        withContext(Dispatchers.IO) {
            getAppStorage(deviceSnapshot().totalBytes)
        }

    private fun getAppStorage(
        deviceTotalBytes: Long
    ): List<AppStorageInfo> {

        val packageManager = context.packageManager

        val applications = packageManager.getInstalledApplications(
            PackageManager.GET_META_DATA
        )

        val storageStatsManager =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.getSystemService(StorageStatsManager::class.java)
            } else {
                null
            }

        val uuid = storageUuid()

        return applications.mapNotNull { appInfo ->

            val packageName = appInfo.packageName
                .takeIf { it.isNotBlank() }
                ?: return@mapNotNull null

            /*
             * Exclude pre-installed system applications.
             *
             * Updated system applications are kept because they
             * can be relevant to the user.
             */
            if (isSystemApp(appInfo)) {
                return@mapNotNull null
            }

            val appName = appInfo
                .loadLabel(packageManager)
                .toString()
                .ifBlank { packageName }

            val apkBytes = apkSize(appInfo)
                .takeIf { it > 0L }

            var appBytes: Long? = null
            var dataBytes: Long? = null
            var cacheBytes: Long? = null

            /*
             * StorageStatsManager is available from Android O.
             */
            if (
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
                storageStatsManager != null &&
                uuid != null
            ) {
                try {

                    val stats = storageStatsManager.queryStatsForPackage(
                        uuid,
                        packageName,
                        Process.myUserHandle()
                    )

                    appBytes = stats.appBytes.takeIf { it >= 0L }
                    dataBytes = stats.dataBytes.takeIf { it >= 0L }
                    cacheBytes = stats.cacheBytes.takeIf { it >= 0L }

                } catch (_: SecurityException) {
                    /*
                     * Storage statistics may not be available
                     * without the required usage access.
                     *
                     * APK size will be used as fallback.
                     */
                } catch (_: Exception) {
                    /*
                     * Package may have been removed or storage
                     * statistics may temporarily be unavailable.
                     */
                }
            }

            /*
             * Calculate total application storage.
             *
             * Prefer complete StorageStats information.
             * Otherwise fall back to APK size.
             */
            val totalBytes = when {
                appBytes != null &&
                        dataBytes != null &&
                        cacheBytes != null -> {

                    (
                            appBytes +
                                    dataBytes +
                                    cacheBytes
                            ).coerceAtLeast(0L)
                }

                apkBytes != null -> {
                    apkBytes
                }

                else -> {
                    0L
                }
            }

            /*
             * IMPORTANT:
             * Do not show applications that have no measurable
             * storage usage.
             */
            if (totalBytes <= 0L) {
                return@mapNotNull null
            }

            val percent = if (
                deviceTotalBytes > 0L &&
                totalBytes > 0L
            ) {
                (totalBytes.toDouble() / deviceTotalBytes) * 100.0
            } else {
                null
            }

            AppStorageInfo(
                packageName = packageName,
                appName = appName,
                totalBytes = totalBytes,
                apkBytes = appBytes ?: apkBytes,
                dataBytes = dataBytes,
                cacheBytes = cacheBytes,
                percentOfDevice = percent
            )
        }
    }

    /**
     * Returns true for pre-installed system applications.
     *
     * UPDATED_SYSTEM_APP is intentionally excluded from this check,
     * meaning an updated system app will still be displayed.
     */
    private fun isSystemApp(
        appInfo: ApplicationInfo
    ): Boolean {

        val isSystemApp =
            (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0

        val isUpdatedSystemApp =
            (appInfo.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0

        return isSystemApp && !isUpdatedSystemApp
    }

    private fun deviceSnapshot(): DeviceSnapshot {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val storageStatsManager =
                context.getSystemService(
                    StorageStatsManager::class.java
                )

            val uuid = storageUuid()

            if (
                storageStatsManager != null &&
                uuid != null
            ) {
                try {

                    val total = storageStatsManager
                        .getTotalBytes(uuid)
                        .coerceAtLeast(0L)

                    val free = storageStatsManager
                        .getFreeBytes(uuid)
                        .coerceIn(0L, total)

                    return toSnapshot(
                        total = total,
                        free = free
                    )

                } catch (_: Exception) {
                    /*
                     * Fall through to StatFs.
                     */
                }
            }
        }

        /*
         * Fallback for older Android versions or when
         * StorageStatsManager is unavailable.
         */
        val stat = StatFs(
            Environment.getDataDirectory().path
        )

        val total = stat.totalBytes
            .coerceAtLeast(0L)

        val free = stat.availableBytes
            .coerceIn(0L, total)

        return toSnapshot(
            total = total,
            free = free
        )
    }

    private fun toSnapshot(
        total: Long,
        free: Long
    ): DeviceSnapshot {

        val used = (total - free)
            .coerceAtLeast(0L)

        val percent =
            if (total > 0L) {
                ((used * 100L) / total).toInt()
            } else {
                0
            }

        return DeviceSnapshot(
            totalBytes = total,
            usedBytes = used,
            freeBytes = free,
            usagePercent = percent.coerceIn(0, 100)
        )
    }

    private fun storageUuid(): UUID? {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return null
        }

        return try {

            val storageManager =
                context.getSystemService(
                    StorageManager::class.java
                )

            storageManager.getUuidForPath(
                Environment.getDataDirectory()
            ) ?: StorageManager.UUID_DEFAULT

        } catch (_: Exception) {

            StorageManager.UUID_DEFAULT
        }
    }

    /**
     * Calculates APK + split APK size.
     */
    private fun apkSize(
        appInfo: ApplicationInfo
    ): Long {

        var size = 0L

        appInfo.sourceDir?.let { path ->
            size += File(path).length()
        }

        appInfo.splitSourceDirs?.forEach { path ->
            size += File(path).length()
        }

        return size
    }

    private fun sumKnown(
        apps: List<AppStorageInfo>,
        selector: (AppStorageInfo) -> Long?
    ): Long? {

        if (apps.isEmpty()) {
            return null
        }

        var any = false
        var sum = 0L

        apps.forEach { app ->

            val value = selector(app)
                ?: return@forEach

            any = true
            sum += value
        }

        return if (any) {
            sum
        } else {
            null
        }
    }

    private fun sumIfComplete(
        apps: List<AppStorageInfo>,
        selector: (AppStorageInfo) -> Long?
    ): Long? {

        if (apps.isEmpty()) {
            return null
        }

        var sum = 0L

        apps.forEach { app ->

            val value = selector(app)
                ?: return null

            sum += value
        }

        return sum
    }

    private data class DeviceSnapshot(
        val totalBytes: Long,
        val usedBytes: Long,
        val freeBytes: Long,
        val usagePercent: Int
    )
}