plugins {
    java
    alias(libs.plugins.shadow)
}

group = "me.ddggdd135"
version = providers.gradleProperty("version").orElse("1.0.0").get()

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://jitpack.io")
    maven("https://oss.sonatype.org/content/groups/public/")
}

dependencies {
    compileOnly(libs.paper.api)
    compileOnly(libs.slimefun4)
    compileOnly(libs.infinity.expansion)
    compileOnly(libs.gugu.slimefun.lib)
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    // 本机与 CI JDK 版本可能不一致，不用 toolchain，直接锁字节码版本
    options.release = 21
}

tasks {
    processResources {
        filesMatching("plugin.yml") {
            expand("version" to project.version.toString())
        }
    }

    shadowJar {
        archiveBaseName.set("InfinityExpansionAutomation")
        archiveVersion.set(project.version.toString())
        archiveClassifier.set("")

        exclude("META-INF/*")
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        mergeServiceFiles()
    }

    build {
        dependsOn(shadowJar)
    }
}

defaultTasks("clean", "build")
