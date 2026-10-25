plugins {
    alias(libs.plugins.android.application)
    id ("com.google.gms.google-services")
}

android {
    namespace = "com.example.rioalert"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.rioalert"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.activity.ktx)
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.material)

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:34.18.0"))
    implementation("com.google.firebase:firebase-database")
    implementation("com.google.firebase:firebase-auth:23.0.0")

    // Lottie Animation
    implementation("com.airbnb.android:lottie:6.4.0")


    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.ext.junit)

    implementation("org.osmdroid:osmdroid-android:6.1.18")
}