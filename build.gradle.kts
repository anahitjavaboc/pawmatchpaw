plugins {
    id("com.android.application") version "8.9.1" apply false
    id("com.android.library") version "8.9.1" apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
}

tasks.register<Delete>("clean") {
    delete(layout.buildDirectory)
}