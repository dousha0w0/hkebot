import org.gradle.internal.impldep.junit.runner.Version.id
import org.jetbrains.kotlin.gradle.plugin.statistics.ReportStatisticsToElasticSearch.url

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
    mavenCentral()
}

dependencies {
    implementation("br.com.azalim:mcserverping:1.0.6")
}
