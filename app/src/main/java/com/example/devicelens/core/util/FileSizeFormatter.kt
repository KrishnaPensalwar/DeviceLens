package com.example.devicelens.core.util

object FileSizeFormatter {

    fun format(bytes: Long): String {
        val gb = bytes / 1_073_741_824.0
        return if (gb >= 1) {
            "%.1f GB".format(gb)
        } else {
            val mb = bytes / 1_048_576.0
            "%.0f MB".format(mb)
        }
    }
}
