// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    val gradleVersion = "8.2.0"
    val kotlinVersion = "1.8.10"
    id("com.android.application") version gradleVersion apply false
    id("org.jetbrains.kotlin.android") version kotlinVersion apply false
    id("com.google.dagger.hilt.android") version "2.48" apply false
}