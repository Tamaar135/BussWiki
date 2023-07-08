val sourceDirectory = project.file("asciidoc")
val htmlBuildOutput = project.file("${project.buildDir}/output/html")

repositories {
    mavenCentral()
    mavenLocal()
}

plugins {
    id("org.asciidoctor.jvm.convert") version "3.1.0"
}

buildscript {
    repositories {
        gradlePluginPortal()
        mavenLocal()
    }
}

tasks {
    "asciidoctor"(org.asciidoctor.gradle.jvm.AsciidoctorTask::class) {
        attributes = mapOf("stylesheet" to "custom.css")
        sourceDir(sourceDirectory)
        setOutputDir(htmlBuildOutput)
    }
}

val includedNonHtmlFileExtensions = setOf("jpg", "png", "pdf")
val copyResourcesTask = tasks.register<Copy>("copyResources") {
    from(sourceDirectory)
    include(*includedNonHtmlFileExtensions.map { "**/*.$it" }.toList().toTypedArray())
    into(htmlBuildOutput)
}

tasks.build {
    dependsOn(copyResourcesTask)
    dependsOn("asciidoctor")
    dependsOn("copyLocalDocs")
}

tasks.register<GradleBuild>("deploy") {
    tasks = listOf("copyLocalDocs")
}

val copyLocalDocs = tasks.register<Copy>("copyLocalDocs") {
    from(htmlBuildOutput)
    into(File(project.projectDir, "docs"))
}
