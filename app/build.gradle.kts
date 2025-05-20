plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.anahit.pawmatch"
    compileSdk = 35
    buildToolsVersion = "35.0.0"

    defaultConfig {
        applicationId = "com.anahit.pawmatch"
        minSdk = 28
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            isDebuggable = true
            buildConfigField("String", "CLOUDINARY_CLOUD_NAME", "\"${project.property("CLOUDINARY_CLOUD_NAME")}\"")
            buildConfigField("String", "CLOUDINARY_API_KEY", "\"${project.property("CLOUDINARY_API_KEY")}\"")
            buildConfigField("String", "CLOUDINARY_API_SECRET", "\"${project.property("CLOUDINARY_API_SECRET")}\"")
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("String", "CLOUDINARY_CLOUD_NAME", "\"${project.property("CLOUDINARY_CLOUD_NAME")}\"")
            buildConfigField("String", "CLOUDINARY_API_KEY", "\"${project.property("CLOUDINARY_API_KEY")}\"")
            buildConfigField("String", "CLOUDINARY_API_SECRET", "\"${project.property("CLOUDINARY_API_SECRET")}\"")
        }
    }
}

dependencies {
    // Firebase BOM for consistent versions
    implementation(platform("com.google.firebase:firebase-bom:33.13.0"))

    // Firebase dependencies
    implementation("com.google.firebase:firebase-auth:23.2.0")
    implementation("com.google.firebase:firebase-database:21.0.0")
    implementation("com.google.firebase:firebase-storage:21.0.0")
    implementation("com.google.firebase:firebase-analytics:22.0.0")

    // Core Android dependencies
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.activity:activity:1.9.3")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    implementation("androidx.core:core-ktx:1.16.0")

    // UI & Graphics libraries
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // CardStackView for swipeable pet matching UI
    implementation("com.github.yuyakaido:cardstackview:2.3.4")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    // Coroutines for async operations
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")

    // CardView and Annotation support
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.annotation:annotation:1.8.0")

    // Cloudinary for image storage
    implementation("com.cloudinary:cloudinary-android:2.5.0")

    // Kotlin standard library
    implementation("org.jetbrains.kotlin:kotlin-stdlib:${project.property("kotlin_version")}")

    // Testing dependencies
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}