plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.fic.notesapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.fic.notesapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    // Room Database Dependencies
    implementation(libs.room.runtime)
    implementation(libs.swiperefreshlayout)  // Versión de Room
    annotationProcessor(libs.room.compiler) // Para Java
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.recyclerview)
    implementation(libs.cardview)
    implementation(libs.retrofit)
    implementation(libs.gson)
    implementation(libs.converter.gson)



}