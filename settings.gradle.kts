pluginManagement {
    // The froid Frege plugin lives in its own (publishable) build; include it so
    // modules can apply it by id without a version.
    includeBuild("froid-gradle-plugin")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
    plugins {
        id("com.android.application") version "8.13.2"
        id("com.android.library") version "8.13.2"
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "froid"
include(":froid")
include(":counter")
project(":counter").projectDir = file("examples/counter")
include(":geoquiz")
project(":geoquiz").projectDir = file("examples/geoquiz")
include(":rotate")
project(":rotate").projectDir = file("examples/rotate")
