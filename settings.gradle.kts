pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.fabricmc.net")
        maven("https://maven.architectury.dev/")
        maven("https://maven.minecraftforge.net")
        maven("https://repo.essential.gg/repository/maven-public")
    }
    // We also recommend specifying your desired version here if you're using more than one of the plugins,
    // so you do not have to change the version in multilpe places when updating.
    plugins {
        val egtVersion = "0.6.2" // should be whatever is displayed in above badge
        id("gg.essential.multi-version.root") version egtVersion
        id("gg.essential.multi-version.api-validation") version egtVersion
    }
}

listOf(
    "1.16.5-fabric",
    "1.18.2-fabric",
    "1.20.1-fabric",
    "1.20.2-fabric",
    "1.20.3-fabric",
    "1.20.4-fabric",
    "1.20.5-fabric",
    "1.20.6-fabric",
    "1.21-fabric",
    "1.21.1-fabric",
).forEach { version ->
    include(":$version")
    project(":$version").apply {
        // This is where the `build` folder and per-version overwrites will reside
        projectDir = file("versions/$version")
        // All sub-projects get configured by the same `build.gradle.kts.kts` file, the string is relative to projectDir
        // You could use separate build files for each project, but usually that would just be duplicating lots of code
        buildFileName = "../../build.gradle.kts"
    }
}

// We use the `build.gradle.kts` file for all the sub-projects (cause that's where most the interesting stuff lives),
// so we need to use a different build file for the original root project.
rootProject.buildFileName = "root.gradle.kts"