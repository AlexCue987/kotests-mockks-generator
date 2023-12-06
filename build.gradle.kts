import org.gradle.jvm.tasks.Jar
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    val kotlinVersion by extra("1.6.10")
    repositories {
        mavenLocal()
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    }
}

val kotlinVersion by extra("1.6.10")
val kotestVersion by extra("4.3.0")

plugins {
    val kotlinVersion by extra("1.7.10")
    id("com.github.johnrengelman.shadow") version "5.2.0"
    id("pl.allegro.tech.build.axion-release") version "1.13.6"
    id("maven-publish") // this it messed up - we should specify the version
    kotlin("jvm") version "1.7.10"
    id("com.tgt.trans.dmo.kotlin-conventions") version "0.2.4"
}


apply(plugin = "kotlin")

scmVersion {
    tag.prefix = ""
    useHighestVersion = true
}

group="com.tgt.trans.dmo.common"
project.version = scmVersion.version

defaultTasks("clean", "build")

repositories {
    maven { url = uri("https://binrepo.target.com/artifactory/tgt-repo") }
    maven { url = uri("https://binrepo.target.com/artifactory/platform") }
    maven { url = uri("https://binrepo.target.com/artifactory/maven-central-repo") }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = "11"
    kotlinOptions.allWarningsAsErrors = false
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")

    testImplementation("io.mockk:mockk:1.12.0")

    testImplementation("io.kotest:kotest-runner-junit5-jvm:$kotestVersion")
    implementation("io.kotest:kotest-assertions-core-jvm:$kotestVersion")
    implementation("io.kotest:kotest-property-jvm:$kotestVersion")
    testImplementation("io.kotest:kotest-runner-console:4.1.3.2")
}

sourceSets {
    test {
        withConvention(KotlinSourceSet::class) {
            kotlin.srcDir(file("src/test/kotlin/unit"))
            resources.srcDir("src/test/resources")
        }
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "11"
	    javaParameters = true
        allWarningsAsErrors = false
    }
}

tasks.named("publish") {
    dependsOn(":build")
}

fun getPublishUri() : String {
    val version = project.version as String
    return when(version.endsWith("-SNAPSHOT", false)) {
        true -> "https://binrepo.target.com/artifactory/transportation-dmo-stg"
        false -> "https://binrepo.target.com/artifactory/transportation-dmo-prod"
    }
}

fun getProp(propName: String) : String {
    return when(project.hasProperty(propName)) {
        true -> project.property(propName) as String
        false -> ""
    }
}

publishing {
    repositories {
        maven {
            url = uri(getPublishUri())
            credentials {
                username = getProp("artifactoryUser")
                password = getProp("artifactoryPass")
            }
        }

    }
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}

configurations{
    all {
        exclude(group =  "org.jetbrains.kotlin", module = "kotlin-stdlib-jdk7")
    }
}
