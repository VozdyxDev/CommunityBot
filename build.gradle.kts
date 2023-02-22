import com.github.jengelman.gradle.plugins.shadow.tasks.ConfigureShadowRelocation

group = "net.darkdustry"
version = "2.0.0"

// Dependency versions
val mindustryVersion = "v142"

plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
}

dependencies {
    implementation("net.dv8tion:JDA:5.0.0-alpha.21")

    implementation("com.github.Anuken.Arc:arc-core:$mindustryVersion")
    implementation("com.github.Anuken.Mindustry:core:$mindustryVersion")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    sourceCompatibility = "16"
    targetCompatibility = "16"
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "net.darkdustry.bot.Main"
        attributes["Can-Redefine-Classes"] = true
        attributes["Can-Retransform-Classes"] = true
    }

    archiveFileName.set(rootProject.name + ".jar")

    from({
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) }
    })
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

val relocate = tasks.register<ConfigureShadowRelocation>("relocateShadowJar") {
    target = tasks.shadowJar.get()
    prefix = project.property("props.root-package").toString() + ".shadow"
}

tasks.shadowJar {
    archiveFileName.set(rootProject.name + "-shadowJar.jar")
    archiveClassifier.set("plugin")
    dependsOn(relocate)
    minimize()
}