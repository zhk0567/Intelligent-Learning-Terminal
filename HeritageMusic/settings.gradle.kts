pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "HeritageMusic"
include(
    ":app",
    ":core:resources",
    ":core:ui",
    ":core:data",
    ":core:network",
    ":feature:music-hall",
    ":feature:stories",
    ":feature:community",
    ":feature:mall",
    ":feature:profile",
    ":feature:player"
)
