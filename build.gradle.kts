import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.2.10"
    id("fabric-loom") version "1.9-SNAPSHOT"
    id("maven-publish")
}

version = project.property("mod_version") as String
group = project.property("maven_group") as String

base {
    archivesName.set(project.property("archives_base_name") as String)
}

val targetJavaVersion = 21
java {
    toolchain.languageVersion = JavaLanguageVersion.of(targetJavaVersion)
    withSourcesJar()
}

sourceSets {
    main {
        resources {
            srcDirs("src/client/resources")
        }
        kotlin {
            srcDirs("src/client/kotlin")
        }
    }
}

fabricApi {
    configureDataGeneration {
        client = true
    }
}

repositories {
    mavenCentral()
    maven(url = "https://dl.cloudsmith.io/public/geckolib3/geckolib/maven/")
    maven("https://maven.impactdev.net/repository/development/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")

    maven {
        name = "TerraformersMC"
        url = uri("https://maven.terraformersmc.com/")
    }
    maven {
        name = "Ladysnake Libs"
        url = uri("https://maven.ladysnake.org/releases")
    }

    maven {
        name = "Modrinth"
        url = uri("https://api.modrinth.com/maven")
    }
    maven {
        name = "CurseMaven"
        url = uri("https://www.cursemaven.com")
        content {
            includeGroup("curse.maven")
        }
    }
}

dependencies {
    minecraft("com.mojang:minecraft:${project.property("minecraft_version")}")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:${project.property("loader_version")}")
    modImplementation("net.fabricmc:fabric-language-kotlin:${project.property("kotlin_loader_version")}")

    modImplementation("net.fabricmc.fabric-api:fabric-api:${project.property("fabric_version")}")

    modImplementation("com.cobblemon:fabric:1.7.1+1.21.1")
    modCompileOnly("curse.maven:cobblemon-mega-showdown-1189523:7728134")
    modLocalRuntime("curse.maven:cobblemon-mega-showdown-1189523:7728134")
}

tasks.processResources {
    inputs.property("version", project.version)
    inputs.property("minecraft_version", project.property("minecraft_version"))
    inputs.property("loader_version", project.property("loader_version"))
    filteringCharset = "UTF-8"

    filesMatching("fabric.mod.json") {
        expand(
            "version" to project.version,
            "minecraft_version" to project.property("minecraft_version")!!,
            "loader_version" to project.property("loader_version")!!,
            "kotlin_loader_version" to project.property("kotlin_loader_version")!!
        )
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(targetJavaVersion)
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions.jvmTarget.set(JvmTarget.fromTarget(targetJavaVersion.toString()))
}

tasks.jar {
    from("LICENSE") {
        rename { "${it}_${project.base.archivesName}" }
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = project.property("archives_base_name") as String
            from(components["java"])
        }
    }
    repositories {
    }
}
