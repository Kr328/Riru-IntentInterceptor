import com.android.build.gradle.api.ApplicationVariant
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.jf.dexlib2.DexFileFactory
import org.jf.dexlib2.Opcodes
import org.jf.dexlib2.rewriter.DexRewriter
import org.jf.dexlib2.rewriter.Rewriter
import org.jf.dexlib2.rewriter.RewriterModule
import org.jf.dexlib2.rewriter.Rewriters
import java.io.File

abstract class PatchDexTask : DefaultTask() {
    abstract val extractedDex: Property<File>
        @InputFile get

    abstract val patchedDex: Property<File>
        @OutputFile get

    abstract val minSdkVersion: Property<Int>
        @Input get

    fun fromApplicationVariant(variant: ApplicationVariant) {
        val extension = project.extensions.getByType(RiruExtension::class.java)

        extractedDex.set(project.extractedApkDir(variant).resolve("classes.dex"))
        patchedDex.set(project.generatedMagiskDir(variant).resolve("system/framework/${extension.dexName.ifEmpty { "classes.dex" }}"))
        minSdkVersion.set(variant.packageApplicationProvider.get().minSdkVersion.get())
    }

    @TaskAction
    fun patch() {
        val input = extractedDex.get()
        val output = patchedDex.get()
        val api = minSdkVersion.get()

        val dex = DexFileFactory.loadDexFile(input, Opcodes.forApi(api))
        val rewrite = DexRewriter(object : RewriterModule() {
            override fun getTypeRewriter(rewriters: Rewriters): Rewriter<String> {
                return Rewriter {
                    if (it.startsWith("L$")) {
                        "L" + it.substring(2)
                    } else {
                        it
                    }
                }
            }
        })
        val rewritten = rewrite.dexFileRewriter.rewrite(dex)

        DexFileFactory.writeDexFile(output.absolutePath, rewritten)
    }
}