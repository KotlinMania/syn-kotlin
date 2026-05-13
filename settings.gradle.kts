pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    plugins { kotlin("multiplatform") version "2.3.21" }
}

plugins { id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0" }

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "syn-kotlin"

includeBuild("../proc-macro2-kotlin") {
    dependencySubstitution {
        substitute(module("io.github.kotlinmania:proc-macro2-kotlin")).using(project(":"))
    }
}

includeBuild("../quote-kotlin") {
    dependencySubstitution {
        substitute(module("io.github.kotlinmania:quote-kotlin")).using(project(":"))
    }
}
