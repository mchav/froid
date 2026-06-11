package io.github.mchav.froid.gradle

import com.android.build.api.variant.AndroidComponentsExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.attributes.Attribute
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.register

/**
 * Registers a per-variant `compileFrege<Variant>` task and feeds its generated
 * Java into the variant's sources. Apply with `apply<FregeAndroidPlugin>()`
 * AFTER an Android application/library plugin.
 */
class FregeAndroidPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val android = project.extensions.findByType(AndroidComponentsExtension::class.java)
            ?: error("Apply an Android application/library plugin before FregeAndroidPlugin.")

        // Android Studio's Kotlin-DSL sync runs `prepareKotlinBuildScriptModel`
        // qualified to each module's project, but Gradle only registers the real
        // task on the build root — so sync fails with "task not found in :module".
        // Register a harmless no-op so the IDE's task lookup resolves; the script
        // model itself is produced by Gradle's model builder, independently.
        if (project.tasks.findByName("prepareKotlinBuildScriptModel") == null) {
            project.tasks.register("prepareKotlinBuildScriptModel")
        }

        // unitTestClasses is sometimes requested by IDEs or specific toolchains;
        // ensure it exists so that tasks like :module:unitTestClasses don't fail.
        if (project.tasks.findByName("unitTestClasses") == null) {
            project.tasks.register("unitTestClasses")
        }

        // The Frege compiler+runtime jar. In this repo it's vendored under libs/;
        // for external consumers it is downloaded once and cached. Adding it to
        // `implementation` puts the runtime (frege.run8.*) on the compile + runtime
        // classpath of whichever module applies the plugin — so the published froid
        // library needs no `api(files(...))`.
        val fregeJar = resolveFregeCompiler(project)
        project.dependencies.add("implementation", project.files(fregeJar))

        val fregeSrc = project.layout.projectDirectory.dir("src/frege")

        // Precompile any hand-written Java in src/main/java (e.g. the Activity base
        // class) BEFORE Frege runs: Frege can't see classes generated within its
        // own -make pass, so native types pointing at them must already exist.
        val javaHelpers = project.layout.projectDirectory.dir("src/main/java")
        val compileHelpers =
            if (javaHelpers.asFile.exists()) {
                project.tasks.register<JavaCompile>("compileFregeJavaHelpers") {
                    source(javaHelpers)
                    include("**/*.java")
                    classpath = project.files(fregeJar, android.sdkComponents.bootClasspath)
                    destinationDirectory.set(project.layout.buildDirectory.dir("frege-java-helpers"))
                    sourceCompatibility = "17"
                    targetCompatibility = "17"
                }
            } else {
                null
            }

        val artifactType = Attribute.of("artifactType", String::class.java)

        android.onVariants { variant ->
            val suffix = variant.name.replaceFirstChar { it.uppercase() }
            // Select the classes-jar artifacts (AGP exposes many variants per
            // dependency: classes, lint, manifest, …). Without this view the raw
            // CompileClasspath configuration is ambiguous to resolve.
            val variantClasspath = project.configurations
                .getByName("${variant.name}CompileClasspath")
                .incoming.artifactView {
                    attributes.attribute(artifactType, "android-classes-jar")
                }.files

            val task = project.tasks.register<FregeCompile>("compileFrege$suffix") {
                group = "frege"
                description = "Compiles Frege sources for the ${variant.name} variant."
                sourceDir.set(fregeSrc)
                fregeCompiler.from(fregeJar)
                compileClasspath.from(android.sdkComponents.bootClasspath)
                compileClasspath.from(variantClasspath)
                // Hand-written Java helpers must be on Frege's -fp (and built first).
                compileHelpers?.let { helpers ->
                    compileClasspath.from(helpers.flatMap { it.destinationDirectory })
                }
                target.set("1.8")
                outputDir.set(
                    project.layout.buildDirectory.dir("generated/frege/${variant.name}/java")
                )
            }

            variant.sources.java?.addGeneratedSourceDirectory(task, FregeCompile::outputDir)
        }
    }

    private companion object {
        // The pinned Frege snapshot (compiler + runtime). Built by
        // tools/build-frege-snapshot.sh; uploaded to a froid GitHub release.
        const val FREGE_VERSION = "3.25.148-067cad05"
        const val FREGE_URL =
            "https://github.com/mchav/froid/releases/download/frege-$FREGE_VERSION/frege-compiler-snapshot.jar"
    }

    /** Vendored jar if present (this repo), else download once into the Gradle cache. */
    private fun resolveFregeCompiler(project: Project): java.io.File {
        val vendored = project.rootProject.file("libs/frege-compiler-snapshot.jar")
        if (vendored.exists()) return vendored
        val cached = java.io.File(project.gradle.gradleUserHomeDir, "caches/froid/frege-$FREGE_VERSION.jar")
        if (!cached.exists()) {
            project.logger.lifecycle("froid: downloading Frege compiler $FREGE_VERSION …")
            cached.parentFile.mkdirs()
            val part = java.io.File(cached.parentFile, "${cached.name}.part")
            java.net.URI(FREGE_URL).toURL().openStream().use { input ->
                part.outputStream().use { out -> input.copyTo(out) }
            }
            check(part.renameTo(cached)) { "froid: could not finalize $cached" }
        }
        return cached
    }
}
