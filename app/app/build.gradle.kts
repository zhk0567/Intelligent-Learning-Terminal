plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.guyunxinchuan.heritage.music"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.guyunxinchuan.heritage.music"
        minSdk = 21
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        viewBinding = true
        // 未使用 <layout> 表达式，关闭后可少跑 data binding 管线，加快增量构建
        dataBinding = false
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    lint {
        checkReleaseBuilds = false
        disable += "GestureBackNavigation"
        disable += "MissingSuperCall"
        disable += "MissingConstraints"
        disable += "HardcodedText"
        disable += "Autofill"
        disable += "NestedWeights"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.cardview)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.viewpager2)
    implementation(libs.coil)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}