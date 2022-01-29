import net.minecraftforge.gradle.common.util.RunConfig
import net.minecraftforge.gradle.userdev.DependencyManagementExtension
import net.minecraftforge.gradle.userdev.UserDevExtension
import java.text.SimpleDateFormat
import java.util.*

buildscript {
    repositories {
        maven("https://maven.minecraftforge.net")
        mavenCentral()
    }
    dependencies {
        classpath(group = "net.minecraftforge.gradle", name = "ForgeGradle", version = "5.1.+") {
            isChanging = true
        }
    }
}
plugins {
    kotlin("jvm") version "1.5.31"
    kotlin("plugin.serialization") version "1.5.31"
}

apply {
    plugin("net.minecraftforge.gradle")
}



version = "1.16.5-1.1.0"
group = "com.teampolymer"

configure<BasePluginExtension> {
    archivesName.set("polymer-scripts")
}
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}
println(
    "Java: ${System.getProperty("java.version")} " +
            "JVM:  ${System.getProperty("java.vm.version")}(${System.getProperty("java.vendor")}) " +
            "Arch:  ${System.getProperty("os.arch")}"
)


configure<UserDevExtension> {
    mappings("official", "1.16.5")

    accessTransformer(file("src/main/resources/META-INF/polymer_at.cfg"))
    runs {
        val runConfig = Action<RunConfig> {
            workingDirectory = project.file("run").canonicalPath
            // "SCAN": For mods scan.
            // "REGISTRIES": For firing of registry events.
            // "REGISTRYDUMP": For getting the contents of all registries.
            property("forge.logging.markers", "REGISTRIES")
            property("forge.logging.console.level", "debug")
            mods {
                create("polymer-scripts") {
                    source(sourceSets["main"])
                }
            }
        }

        create("client", runConfig)
        create("server", runConfig)
        create("data") {
            runConfig(this)
            args(
                "--mod",
                "polymer-scripts",
                "--all",
                "--output",
                file("src/generated/resources/"),
                "--existing",
                file("src/main/resources/")
            )
        }
    }

}

//Include resources generated by data generators.
sourceSets.main {
    resources { srcDir("src/generated/resources") }
}

repositories {
    maven {
        name = "Kotlin for Forge"
        url = uri("https://thedarkcolour.github.io/KotlinForForge/")
    }

}

dependencies {
    // Specify the version of Minecraft to use, If this is any group other then 'net.minecraft' it is assumed
    // that the dep is a ForgeGradle 'patcher' dependency. And it's patches will be applied.
    // The userdev artifact is a special name and will get all sorts of transformations applied to it.
    "minecraft"("net.minecraftforge:forge:1.16.5-36.2.20")
    // Use the latest version of KotlinForForge
    implementation("thedarkcolour:kotlinforforge:1.16.0")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.6.10")

    val fg = project.extensions.getByType<DependencyManagementExtension>()


}

tasks.jar {
    manifest {
        val time = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(Date())
        attributes(
            mapOf(
                "Specification-Title" to "Polymer Scripts",
                "Specification-Vendor" to "WarmthDawn",
                "Specification-Version" to "1", // We are version 1 of ourselves
                "Implementation-Title" to project.name,
                "Implementation-Version" to "${project.version}",
                "Implementation-Vendor" to "WarmthDawn",
                "Implementation-Timestamp" to time
            )
        )
    }

}

tasks.jar {
    finalizedBy("reobfJar")
}

