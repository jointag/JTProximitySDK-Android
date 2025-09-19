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
        // Library repository
        maven { url = uri("https://artifactory.jointag.com/artifactory/jointag") }
        // Next14CMP repository
        maven { url = uri("https://artifactory.jointag.com/artifactory/next14") }
        // Huawei repository
        maven { url = uri("https://developer.huawei.com/repo/") }
    }
}

rootProject.name = "Empty Example"

include(":app")
