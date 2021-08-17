plugins {
    `java-library`
}

group = "org.xjcraft"
version = "1.0.0-SNAPSHOT"

// Java 版本
configure<JavaPluginExtension> {
    val javaVersion = JavaVersion.VERSION_11

    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
}

// 仓库配置
repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

// 源文件编码
tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

// 依赖
dependencies {
    annotationProcessor("org.projectlombok:lombok:1.18.20")
    compileOnly("org.projectlombok:lombok:1.18.20")
    implementation("org.spigotmc:spigot-api:1.17-R0.1-SNAPSHOT")
}
