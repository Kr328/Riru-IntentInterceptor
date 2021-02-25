import com.android.build.gradle.api.ApplicationVariant
import org.gradle.api.tasks.Copy

abstract class MergeMagiskTask : Copy() {
    fun fromApplicationVariant(v: ApplicationVariant) {
        into(project.generatedMagiskDir(v))

        from(project.extractedApkDir(v)) {
            include("lib/**")

            eachFile {
                it.path = it.path
                    .replace("lib/x86_64", "system_x86/lib64")
                    .replace("lib/x86", "system_x86/lib")
                    .replace("lib/arm64-v8a", "system/lib64")
                    .replace("lib/armeabi-v7a", "system/lib")
            }
        }
    }
}