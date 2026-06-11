plugins {
    id("com.android.application")
    id("io.github.mchav.froid")
}

android {
    namespace = "io.github.mchav.froid.rotate"
    compileSdk = 36

    defaultConfig {
        applicationId = "io.github.mchav.froid.rotate"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "0.1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(project(":froid"))
}
