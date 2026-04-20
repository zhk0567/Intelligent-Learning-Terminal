plugins {
    id("com.android.application") version "8.7.2" apply false
    id("com.android.library") version "8.7.2" apply false
    id("org.jetbrains.kotlin.android") version "2.0.21" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.21" apply false
}

/** 与 compileSdk 34 对齐；避免传递依赖拉取要求 API 35 的 androidx.core 1.15+ */
subprojects {
    configurations.configureEach {
        resolutionStrategy.eachDependency {
            if (requested.group == "androidx.core" &&
                (requested.name == "core" || requested.name == "core-ktx")
            ) {
                useVersion("1.13.1")
            }
        }
    }
}
