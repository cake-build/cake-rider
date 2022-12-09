import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.changelog.markdownToHTML
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

fun properties(key: String) = project.findProperty(key).toString()

plugins {
    // Java support
    id("java")
    // Kotlin support
    // do NOT update kotlin - kotlin version must match platform version, see https://plugins.jetbrains.com/docs/intellij/kotlin.html#kotlin-standard-library
    id("org.jetbrains.kotlin.jvm") version "1.6.21"
    // gradle-intellij-plugin - read more: https://github.com/JetBrains/gradle-intellij-plugin
    id("org.jetbrains.intellij") version "1.10.1"
    id("com.jetbrains.rdgen") version "2022.2.5"
    // gradle-changelog-plugin - read more: https://github.com/JetBrains/gradle-changelog-plugin
    id("org.jetbrains.changelog") version "2.0.0"
    // detekt linter - read more: https://detekt.github.io/detekt/gradle.html
    id("io.gitlab.arturbosch.detekt") version "1.22.0"
    // ktlint linter - read more: https://github.com/JLLeitschuh/ktlint-gradle
    id("org.jlleitschuh.gradle.ktlint") version "11.0.0"
    // grammarkit to generate parser & lexer (i.e. the bnf and the flex file...)
    id("org.jetbrains.grammarkit") version "2021.2.2"
}

val jvmVersion = "11"
val kotlinVersion = "1.6" // should match org.jetbrains.kotlin.jvm (major.minor)

group = properties("pluginGroup")
version = properties("pluginVersion")

// Configure project's dependencies
repositories {
    mavenCentral()
    jcenter()
}
dependencies {
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.22.0")

    testImplementation("org.junit.jupiter:junit-jupiter:5.9.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.1")
    testRuntimeOnly("org.junit.vintage:junit-vintage-engine:5.9.1")
}

// Configure gradle-intellij-plugin plugin.
// Read more: https://github.com/JetBrains/gradle-intellij-plugin
intellij {
    pluginName.set(properties("pluginName"))
    version.set(properties("platformVersion"))
    type.set(properties("platformType"))
    downloadSources.set(properties("platformDownloadSources").toBoolean())
    updateSinceUntilBuild.set(true)

    // Plugin Dependencies. Uses `platformPlugins` property from the gradle.properties file.
    plugins.set(
        properties("platformPlugins")
            .split(',')
            .map(String::trim)
            .filter(String::isNotEmpty)
    )
}

// Configure detekt plugin.
// Read more: https://detekt.github.io/detekt/kotlindsl.html
detekt {
    config = files("./detekt-config.yml")
    buildUponDefaultConfig = true
}

// configure rdgen
configure<com.jetbrains.rd.generator.gradle.RdGenExtension> {
    val modelDir = File(rootDir, "protocol/src/main/kotlin/model")
    val csOutput = File(rootDir, "../dotnet/cake-rider/Protocol")
    val ktOutput = File(rootDir, "src/main/gen/net/cakebuild/protocol")

    verbose = true
    classpath({
        "${tasks.setupDependencies.get().idea.get().classes}/lib/rd/rider-model.jar"
    })
    sources("$modelDir/rider")
    hashFolder = "$buildDir"
    packages = "model.rider"

    generator {
        language = "kotlin"
        transform = "asis"
        root = "com.jetbrains.rider.model.nova.ide.IdeRoot"
        namespace = "net.cakebuild.protocol"
        directory = "$ktOutput"
    }

    generator {
        language = "csharp"
        transform = "reversed"
        root = "com.jetbrains.rider.model.nova.ide.IdeRoot"
        namespace = "net.cakebuild.Protocol"
        directory = "$csOutput"
    }
}

// add generated code to compilation
sourceSets["main"].java.srcDirs("src/main/gen")

tasks {

    // generate the lexer (uses grammarkit)
    generateLexer {
        source.set("src/main/kotlin/net/cakebuild/language/psi/Cake.flex")
        targetDir.set("src/main/gen/net/cakebuild/language/psi")
        targetClass.set("CakeLexer")
        purgeOldFiles.set(true)
    }

    clean {
        doFirst {
            // clean gen folder
            logger.log(LogLevel.INFO, "removing gen folder to cleanup all generated sources.")
            delete("src/main/gen")

            // clean dotnet
            logger.log(LogLevel.INFO, "cleaning dotnet component.")
            val pluginName = properties("pluginName")
            val dotNetConfiguration = properties("dotNetConfiguration")
            val platformVersion = properties("platformVersion")
            val dotnetDir = File(rootDir, "../dotnet")

            exec {
                executable = "dotnet"
                args = listOf(
                    "msbuild",
                    "-restore",
                    "-target:Clean",
                    File(dotnetDir, "$pluginName.sln").absolutePath,
                    "-property:Configuration=$dotNetConfiguration",
                    "-property:SdkVersion=$platformVersion",
                    "-property:HostFullIdentifier="
                )
                workingDir = File(rootDir, "../dotnet")
            }
        }
    }

    // generate the parser (uses grammarkit)
    generateParser {
        source.set("src/main/kotlin/net/cakebuild/language/psi/Cake.bnf")
        targetRoot.set("src/main/gen")
        pathToParser.set("/net/cakebuild/language/psi/CakeParser.java")
        pathToPsiRoot.set("/net/cakebuild/language/psi")
    }

    val buildDotNet = register("buildDotNet") {
        dependsOn(rdgen)

        val pluginName = properties("pluginName")
        val dotNetConfiguration = properties("dotNetConfiguration")
        val platformVersion = properties("platformVersion")
        val dotnetDir = File(rootDir, "../dotnet")
        val dotnetOutDir = File(dotnetDir, "$pluginName/bin/$dotNetConfiguration")

        // define input & output, so gradle
        // can determine whether building is needed
        outputs.files(
            File(dotnetOutDir, "$pluginName.dll"),
            File(dotnetOutDir, "$pluginName.pdb")
        )
            .withPropertyName("outputDir")

        inputs.property("dotNetConfiguration", dotNetConfiguration)
        inputs.property("pluginName", pluginName)
        inputs.property("platformVersion", platformVersion)
        inputs.files(
            fileTree(File(rootDir, "../dotnet/$pluginName")) {
                include("**/*.cs", "**/*.csproj")
                exclude("bin/**", "obj/**")
            }
        )
            .skipWhenEmpty()
            .withPropertyName("sourceFiles")
            .withPathSensitivity(PathSensitivity.RELATIVE)

        // build the dotNet part
        // https://blog.jetbrains.com/dotnet/2019/02/14/writing-plugins-resharper-rider/
        doLast {
            exec {
                executable = "dotnet"
                args = listOf(
                    "msbuild",
                    "-restore",
                    "-target:Build",
                    File(dotnetDir, "$pluginName.sln").absolutePath,
                    "-property:Configuration=$dotNetConfiguration",
                    "-property:SdkVersion=$platformVersion",
                    "-property:HostFullIdentifier="
                )
                workingDir = dotnetDir
            }

            logger.info("outputs: ${outputs.files.joinToString { it.absolutePath }}")
        }
    }

    // add the dotnet component to the sandbox that will be used to create the final plugin-zip
    prepareSandbox {
        dependsOn.add(buildDotNet)

        val pluginName = properties("pluginName")
        val dotNetConfiguration = properties("dotNetConfiguration")
        val platformVersion = properties("platformVersion")

        inputs.property("dotNetConfiguration", dotNetConfiguration)
        inputs.property("pluginName", pluginName)
        inputs.property("platformVersion", platformVersion)

        val projectTemplates = File(rootDir, "../projectTemplates")
        val sandbox = intellij.sandboxDir.get()

        val forcePrepareSandbox = properties("forcePrepareSandbox").toBoolean()
        if (forcePrepareSandbox) {
            outputs.upToDateWhen { false }
        }

        doFirst {
            if (forcePrepareSandbox) {
                logger.warn("prepareSandbox was forced!")
            }
            logger.info("Sandbox is at $sandbox")
            logger.info("Adding projectTemplates from $projectTemplates")
            logger.info(
                "Adding .NET components:\n - ${buildDotNet.get().outputs.files.joinToString(separator = "\n - ")}"
            )
        }

        // copy projectTemplates
        into(
            "$pluginName/projectTemplates"
        ) {
            from(projectTemplates)
        }

        // copy dotnet component
        into(
            "$pluginName/dotnet"
        ) {
            from(buildDotNet.get().outputs)
        }
    }

    withType<JavaCompile> {
        sourceCompatibility = jvmVersion
        targetCompatibility = jvmVersion
    }
    withType<KotlinCompile> {
        dependsOn(generateLexer, generateParser, rdgen)
        kotlinOptions {
            jvmTarget = jvmVersion
            languageVersion = kotlinVersion
            apiVersion = kotlinVersion
            freeCompilerArgs = listOf("-Xjvm-default=compatibility")
        }
    }
    withType<Detekt> {
        jvmTarget = jvmVersion
        excludes.add("**/gen/**")
        reports {
            html.required.set(false)
            xml.required.set(false)
            txt.required.set(false)
        }
    }
    // workaround for https://youtrack.jetbrains.com/issue/IDEA-210683
    getByName<JavaExec>("buildSearchableOptions") {
        jvmArgs(
            // I gave up on tracking individual illegal access violations.
            // This seems to be an integral and unfixable part of IntelliJ.
            "--illegal-access=permit"
        )
    }
    withType<org.jlleitschuh.gradle.ktlint.tasks.KtLintCheckTask> {
        exclude("**/gen/**", "**/*.Generated.kt")
    }

    patchPluginXml {
        version.set(properties("pluginVersion"))
        sinceBuild.set(properties("pluginSinceBuild"))
        untilBuild.set(properties("pluginUntilBuild"))

        // Extract the <!-- Plugin description --> section from README.md and provide for the plugin's manifest
        pluginDescription.set(
            provider {
                File(projectDir, "./plugin_description.md").readText().lines().run {
                    val start = "<!-- Plugin description -->"
                    val end = "<!-- Plugin description end -->"

                    if (!containsAll(listOf(start, end))) {
                        throw GradleException(
                            "Plugin description section not found in plugin_description.md:\n$start ... $end"
                        )
                    }
                    subList(indexOf(start) + 1, indexOf(end))
                }.joinToString("\n").run { markdownToHTML(this) }
            }
        )

        changeNotes.set(
            provider {
                File(projectDir, "./plugin_description.md").readText().lines().run {
                    val start = "<!-- Plugin changeNotes -->"
                    val end = "<!-- Plugin changeNotes end -->"

                    if (!containsAll(listOf(start, end))) {
                        throw GradleException(
                            "Plugin changeNotes section not found in plugin_description.md:\n$start ... $end"
                        )
                    }
                    subList(indexOf(start) + 1, indexOf(end))
                }.joinToString("\n").run { markdownToHTML(this) }
            }
        )
    }

    runPluginVerifier {
        // verifierVersion.set("1.256") // default ist "latest".
        ideVersions.addAll(
            properties("pluginVerifierIdeVersions")
                .split(',')
                .map(String::trim)
                .filter(String::isNotEmpty)
        )
        // reports are in ${project.buildDir}/reports/pluginVerifier - or set verificationReportsDirectory()
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
        channels.set(listOf(properties("marketplaceChannel")))
    }

    test {
        useJUnitPlatform()
    }
}
