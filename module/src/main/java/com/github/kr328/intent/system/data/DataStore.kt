package com.github.kr328.intent.system.data

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import com.github.kr328.intent.compat.*
import com.github.kr328.intent.shared.Constants.*
import com.github.kr328.intent.shared.TLog
import com.github.kr328.intent.system.model.Module
import com.github.kr328.intent.system.model.Target
import com.github.kr328.intent.util.*
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class DataStore(private val userId: Int) {
    private val modulesFile = File(DATA_PATH).resolve(userId.toString()).resolve(MODULES_DIR)
    private val targetsFile = File(DATA_PATH).resolve(userId.toString()).resolve(TARGETS_DIR)

    var modules: MutableMap<String, Module> = mutableMapOf()
        private set
    var targets: Map<String, Target> = emptyMap()
        private set

    fun load() {
        try {
            val modules = modulesFile.loadJsonFiles(Module)
            val targets = targetsFile.loadJsonFiles(Target)

            this.modules = modules.map { it.packageName to it }.toMap().toMutableMap()
            this.targets = targets.map { it.packageName to it }.toMap()
        } catch (e: Exception) {
            TLog.w("Configuration load: ${e.message}", e)

            TLog.w("Cleaning configuration")
        }
    }

    fun update(): Boolean {
        val modules =
            SystemService.packages.getPackagesByPermission(MODULE_PERMISSION, PACKAGE_FLAGS, userId)
                .asSequence()
                .filter { it.isValid() }
                .map { it.packageName to Module(it) }
                .toMap()

        if (this.modules == modules) {
            return false
        }

        this.modules = modules.toMutableMap()

        this.collectTargets()

        this.store()

        return true
    }

    fun updatePackage(packageName: String): Set<String> {
        val pkg = SystemService.packages.getPackageInfo(packageName, PACKAGE_FLAGS, userId)

        if (!pkg.isValid()) {
            return removePackage(packageName)
        }

        val existed = modules[packageName]

        if (existed != null) {
            val module = Module(pkg)

            if (module == existed) {
                return emptySet()
            }

            val changed = modules[packageName]!!.targets + module.targets

            modules[packageName] = module

            this.collectTargets()

            this.store()

            return changed.toSet()
        }

        val module = Module(pkg)

        modules[packageName] = module

        this.collectTargets()

        this.store()

        return module.targets.toSet()
    }

    fun removePackage(packageName: String): Set<String> {
        return modules.remove(packageName)?.targets?.toSet()?.apply {
            collectTargets()

            store()
        } ?: emptySet()
    }

    private fun collectTargets() {
        this.targets = modules.values
            .flatMap { it.targets.map { t -> t to it.packageName } }
            .groupBy(Pair<String, String>::first, Pair<String, String>::second)
            .mapValues { Target(it.key, it.value.toSet()) }
    }

    private fun store() {
        modules.values.storeJsonFiles({ it.packageName }, modulesFile)
        targets.values.storeJsonFiles({ it.packageName }, targetsFile)
    }

    private fun PackageInfo.isValid(): Boolean {
        return requestedPermissions?.contains(MODULE_PERMISSION) == true
                && appMetaData.containsKey(MODULE_METADATA_INTERCEPTOR)
                && appMetaData.containsKey(MODULE_METADATA_TARGET)
                && SystemService.packages.isPermissionGranted(uid, MODULE_PERMISSION)
    }

    companion object {
        const val DATA_PATH = "/data/misc/intent"

        private const val CRASHED_FILE = "crashed.log"
        private const val MODULES_DIR = "modules"
        private const val TARGETS_DIR = "targets"
        private const val PACKAGE_FLAGS =
            PackageManager.GET_PERMISSIONS or PackageManager.GET_META_DATA

        fun openCrashedLog(): OutputStream {
            return FileOutputStream(File(DATA_PATH, CRASHED_FILE))
        }
    }
}