package tk.zwander.fabricateoverlay

import android.os.IBinder
import rikka.shizuku.SystemServiceHelper
import kotlin.system.exitProcess

/**
 * Used in Shizuku mode to retrieve an [android.content.om.IOverlayManager] IBinder instance.
 */
class ShizukuService : IShizukuService.Stub() {
    override fun destroy() {
        exitProcess(0)
    }

    override fun getIOM(): IBinder {
        return SystemServiceHelper.getSystemService("overlay")
    }
}