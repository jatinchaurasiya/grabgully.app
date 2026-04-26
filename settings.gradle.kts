pluginManagement {
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
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // CueLink SDK
        maven { url = uri("https://jitpack.io") }
        // Vico charts
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "GrabGully"
include(":app")
