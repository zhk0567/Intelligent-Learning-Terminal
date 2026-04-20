plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.intangibleheritage.music"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.intangibleheritage.music"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        buildConfigField("boolean", "USE_REMOTE_API", "false")
        buildConfigField("String", "API_BASE_URL", "\"https://example.com/\"")
        buildConfigField("boolean", "ENABLE_NETWORK_LOGGING", "false")
        buildConfigField("boolean", "ENABLE_FAKE_DELAY", "false")
    }

    buildTypes {
        debug {
            buildConfigField("boolean", "ENABLE_NETWORK_LOGGING", "false")
            buildConfigField("boolean", "ENABLE_FAKE_DELAY", "false")
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            buildConfigField("boolean", "ENABLE_NETWORK_LOGGING", "false")
            buildConfigField("boolean", "ENABLE_FAKE_DELAY", "false")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

// IntelliJ / Android Studio 的「Make」仍可能请求旧任务名；AGP 8+ 已移除 unitTestClasses / androidTestClasses，
// 缺任务时 Gradle 立即失败且 Build 窗口几乎无日志，Sync 也会卡住。此处注册别名指向现有任务。
tasks.register("unitTestClasses") {
    group = "build"
    description = "IDE compatibility: compile all unit test variants (alias for removed task)"
    dependsOn("assembleUnitTest")
}

tasks.register("androidTestClasses") {
    group = "build"
    description = "IDE compatibility: compile instrumentation tests (alias for removed task)"
    dependsOn("assembleAndroidTest")
}

dependencies {
    implementation(project(":core:ui"))
    implementation(project(":core:data"))
    implementation(project(":core:network"))
    implementation(project(":core:resources"))
    implementation(project(":feature:music-hall"))
    implementation(project(":feature:stories"))
    implementation(project(":feature:community"))
    implementation(project(":feature:mall"))
    implementation(project(":feature:profile"))
    implementation(project(":feature:player"))

    val composeBom = platform("androidx.compose:compose-bom:2024.10.01")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
    implementation("io.coil-kt:coil:2.7.0")

    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")

    implementation("androidx.navigation:navigation-compose:2.8.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")

    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
