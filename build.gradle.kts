plugins {
    val kotlinVersion = "1.6.10"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion
    id("net.mamoe.mirai-console") version "2.12.2"
}

group = "me.dousha"
version = "0.1.0"

repositories {
    maven("https://maven.aliyun.com/repository/public")
    maven{
        url = uri("https://oss.sonatype.org/content/repositories/snapshots")
    }
    mavenCentral()
}

dependencies {
    implementation("net.md-5:bungeecord-chat:1.19-R0.1-SNAPSHOT")
    implementation("com.google.code.gson:gson:2.9.0")
    implementation("dnsjava:dnsjava:3.5.1")
    implementation("com.google.guava:guava:31.1-jre")
    implementation("com.alibaba:fastjson:2.0.14.graal")
    shadow("com.alibaba:fastjson:2.0.14.graal")
    shadow("net.md-5:bungeecord-chat:1.16-R0.4")
    shadow("com.google.code.gson:gson:2.9.0")
    shadow("dnsjava:dnsjava:3.5.1")
    shadow("com.google.guava:guava:31.1-jre")



    compileOnly("org.projectlombok:lombok:1.18.24")
    annotationProcessor("org.projectlombok:lombok:1.18.24")
}
