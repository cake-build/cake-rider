import io.gitlab.arturbosch.detekt.Detekt
import org.gradle.internal.os.OperatingSystem
import org.jetbrains.changelog.markdownToHTML
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

fun properties(key: String) = project.findProperty(key).toString()

buildscript {
    repositories {
        maven { setUrl("https://cache-redirector.jetbrains.com/www.myget.org/F/rd-snapshots/maven") }
        mavenCentral()
    }
    dependencies {
        // https://www.myget.org/feed/rd-snapshots/package/maven/com.jetbrains.rd/rd-gen
        // version 0.202.x should match pluginSinceBuild ?
        classpath("com.jetbrains.rd", "rd-gen", "0.202.131")
    }
}

plugins {
    // Java support
    id("java")
    // Kotlin support
    // do NOT update to >=1.4.0 while keeping rd-gen below 0.203.134 !! https://youtrack.jetbrains.com/issue/RIDER-57625
    // do NOT update kotlin - kotlin version must match platform version, see https://plugins.jetbrains.com/docs/intellij/kotlin.html#kotlin-standard-library
    id("org.jetbrains.kotlin.jvm") version "1.3.70"
    // gradle-intellij-plugin - read more: https://github.com/JetBrains/gradle-intellij-plugin
    id("org.jetbrains.intellij") version "1.1.6"
    // gradle-changelog-plugin - read more: https://github.com/JetBrains/gradle-changelog-plugin
    id("org.jetbrains.changelog") version "1.3.0"
    // detekt linter - read more: https://detekt.github.io/detekt/gradle.html
    id("io.gitlab.arturbosch.detekt") version "1.18.1"
    // ktlint linter - read more: https://github.com/JLLeitschuh/ktlint-gradle
    id("org.jlleitschuh.gradle.ktlint") version "10.0.0"
    // grammarkit to generate parser & lexer (i.e. the bnf and the flex file...)
    id("org.jetbrains.grammarkit") version "2021.1.3"
}

apply {
    plugin("com.jetbrains.rdgen")
}

val jvmVersion = "1.8"
val kotlinVersion = "1.3" // should match org.jetbrains.kotlin.jvm (major.minor)

group = properties("pluginGroup")
version = properties("pluginVersion")

// Configure project's dependencies
repositories {
    mavenCentral()
    jcenter()
}
dependencies {
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.18.1")

    testImplementation("org.junit.jupiter:junit-jupiter:5.7.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.2")
    testRuntimeOnly("org.junit.vintage:junit-vintage-engine:5.7.2")
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

    reports {
        html.enabled = false
        xml.enabled = false
        txt.enabled = false
    }
}

// configure grammarkit
grammarKit {
    // version of IntelliJ patched JFlex (see bintray link below), Default is 1.7.0-1
    // jflexRelease = "1.7.0-1"

    // tag or short commit hash of Grammar-Kit to use (see link below). Default is 2020.3.1
    // use 2020.1 to have java-compatibility to rider 2020.1
    grammarKitRelease = "2020.1"
}

// configure rdgen
configure<com.jetbrains.rd.generator.gradle.RdgenParams> {
    val csOutput = File(rootDir, "../dotnet/cake-rider/Protocol")
    val ktOutput = File(rootDir, "src/main/gen/net/cakebuild/protocol")

    mkdir(csOutput.absolutePath)
    mkdir(ktOutput.absolutePath)

    verbose = true
    hashFolder = "build/rdgen"
    logger.info("Configuring rdgen params")
    classpath({
        val ideaDependency = intellij.getIdeaDependency(project)
        logger.info("Calculating classpath for rdgen, intellij.ideaDependency is $ideaDependency")
        val sdkPath = ideaDependency.classes
        val rdLibDirectory = File(sdkPath, "lib/rd").canonicalFile
        val riderModelJar = "$rdLibDirectory/rider-model.jar"
        logger.info("rider-model.jar detected at $riderModelJar")

        riderModelJar
    })
    sources(File(rootDir, "protocol/src/main/kotlin/model"))
    packages = "model"
    clearOutput = true

    generator {
        language = "kotlin"
        transform = "asis"
        root = "com.jetbrains.rider.model.nova.ide.IdeRoot"
        namespace = "com.jetbrains.rider.model"
        directory = "$ktOutput"
    }

    generator {
        language = "csharp"
        transform = "reversed"
        root = "com.jetbrains.rider.model.nova.ide.IdeRoot"
        namespace = "JetBrains.Rider.Model"
        directory = "$csOutput"
    }
}

// add generated code to compilation
sourceSets["main"].java.srcDirs("src/main/gen")

tasks {
    // generate the lexer (uses grammarkit)
    register<org.jetbrains.grammarkit.tasks.GenerateLexer>("gen-lexer") {
        dependsOn("gen-parser")
        this.source = "src/main/kotlin/net/cakebuild/language/psi/Cake.flex"
        this.targetDir = "src/main/gen/net/cakebuild/language/psi"
        this.targetClass = "CakeLexer"
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
                    "/t:Clean",
                    File(dotnetDir, "$pluginName.sln").absolutePath,
                    "/p:Configuration=$dotNetConfiguration",
                    "/p:SdkVersion=$platformVersion",
                    "/p:HostFullIdentifier="
                )
                workingDir = File(rootDir, "../dotnet")
            }
        }
    }

    // generate the parser (uses grammarkit)
    register<org.jetbrains.grammarkit.tasks.GenerateParser>("gen-parser") {
        this.source = "src/main/kotlin/net/cakebuild/language/psi/Cake.bnf"
        this.targetRoot = "src/main/gen"
        this.pathToParser = "/net/cakebuild/language/psi/CakeParser.java"
        this.pathToPsiRoot = "/net/cakebuild/language/psi/parse"
    }

    register("buildDotNet") {
        // dependsOn("rdgen")

        val pluginName = properties("pluginName")
        val dotNetConfiguration = properties("dotNetConfiguration")

        // define input & output, so gradle
        // can determine whether building is needed
        outputs.dir(File(rootDir, "../dotnet/$pluginName/bin/$dotNetConfiguration"))
            .withPropertyName("outputDir")
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
            val platformVersion = properties("platformVersion")
            val dotnetDir = File(rootDir, "../dotnet")

            exec {
                executable = "dotnet"
                args = listOf(
                    "msbuild",
                    "/t:Restore;Build",
                    File(dotnetDir, "$pluginName.sln").absolutePath,
                    "/p:Configuration=$dotNetConfiguration",
                    "/p:SdkVersion=$platformVersion",
                    "/p:HostFullIdentifier="
                )
                workingDir = dotnetDir
            }
        }
    }

    // add the dotnet component to the sandbox that will be used to create the final plugin-zip
    prepareSandbox {
        dependsOn.add("buildDotNet")
        val pluginName = intellij.pluginName.get()

        // copy projectTemplates
        logger.info("Adding projectTemplates to sandbox")
        val projectTemplates = File(rootDir, "../projectTemplates")
        into(
            "$pluginName/projectTemplates"
        ) {
            from(projectTemplates)
        }

        // copy dotnet component
        logger.info("Adding .NET component to sandbox")
        val dotNetConfiguration = properties("dotNetConfiguration")
        val dotNetOutput = File(rootDir, "../dotnet/$pluginName/bin/$dotNetConfiguration")
        into(
            "$pluginName/dotnet"
        ) {
            from(dotNetOutput) {
                include("$pluginName.*")
            }
        }
    }

    withType<JavaCompile> {
        sourceCompatibility = jvmVersion
        targetCompatibility = jvmVersion
    }
    withType<KotlinCompile> {
        dependsOn("gen-lexer", "gen-parser" /*, "rdgen"*/)
        kotlinOptions {
            jvmTarget = jvmVersion
            languageVersion = kotlinVersion
            apiVersion = kotlinVersion
            freeCompilerArgs = listOf("-Xjvm-default=compatibility")
        }
    }
    withType<Detekt> {
        jvmTarget = jvmVersion
    }
    // workaround for https://youtrack.jetbrains.com/issue/IDEA-210683
    getByName<JavaExec>("buildSearchableOptions") {
        jvmArgs(
            "--illegal-access=deny",
            "--add-opens=java.base/java.lang=ALL-UNNAMED",
            "--add-opens=java.base/java.util=ALL-UNNAMED",
            "--add-opens=java.desktop/java.awt=ALL-UNNAMED",
            "--add-opens=java.desktop/java.awt.event=ALL-UNNAMED",
            "--add-opens=java.desktop/javax.swing=ALL-UNNAMED",
            "--add-opens=java.desktop/javax.swing.plaf.basic=ALL-UNNAMED",
            "--add-opens=java.desktop/sun.awt=ALL-UNNAMED",
            "--add-opens=java.desktop/sun.font=ALL-UNNAMED",
            "--add-opens=java.desktop/sun.swing=ALL-UNNAMED"
        )

        if (OperatingSystem.current() == OperatingSystem.MAC_OS) {
            jvmArgs("--add-opens=java.desktop/com.apple.eawt.event=ALL-UNNAMED")
        }
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
        verifierVersion.set("1.256") // starting from 1.260 java 11 is needed.
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
