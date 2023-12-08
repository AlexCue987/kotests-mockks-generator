import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    val kotlinVersion by extra("1.9.20")
    repositories {
        mavenLocal()
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    }
}

val kotlinVersion by extra("1.9.20")
val kotestVersion by extra("5.5.5")

plugins {
    val kotlinVersion by extra("1.9.20")
    kotlin("jvm") version "1.9.20"
    `java-library`
}

apply(plugin = "kotlin")

defaultTasks("clean", "build")

repositories {
    mavenCentral()
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

fun getProp(propName: String) : String {
    return when(project.hasProperty(propName)) {
        true -> project.property(propName) as String
        false -> ""
    }
}

configurations{
    all {
        exclude(group =  "org.jetbrains.kotlin", module = "kotlin-stdlib-jdk7")
    }
}
