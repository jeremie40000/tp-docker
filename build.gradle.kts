import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin("jvm") version "1.3.50"
    id("com.bmuschko.docker-remote-api") version "6.6.1"
}

// Import task types
import com.bmuschko.gradle.docker.tasks.image.*


//docker {
  //  javaApplication {
    //    baseImage.​set​(​"openjdk"​)
      //  ports.​set​(listOf(​8080​, ​5432​))
        //tag.​set​(​"tpdocker"​)
    //}
//}

group = "dev.gleroy.devops.tp-docker"
version = "1.0.0"

application {
    mainClassName = "dev.gleroy.devops.tp.docker.AppKt"
    val dbUrl = property("database.url")
    val dbUsername = property("database.username")
    val dbPassword = property("database.password")
    applicationDefaultJvmArgs = listOf("-Ddatabase.url=$dbUrl", "-Ddatabase.username=$dbUsername", "-Ddatabase.password=$dbPassword")
}

repositories {
    mavenCentral()
}

dependencies {
    val jUnitVersion = "5.5.2"
    testImplementation("org.junit.jupiter:junit-jupiter-api:$jUnitVersion")
    testRuntime("org.junit.jupiter:junit-jupiter-engine:$jUnitVersion")

    val jacksonVersion = "2.9.10"
    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")

    val ktorVersion = "1.2.4"
    implementation("io.ktor:ktor-auth:$ktorVersion")
    implementation("io.ktor:ktor-jackson:$ktorVersion") {
        exclude("com.fasterxml.jackson.core", "jackson-databind")
        exclude("com.fasterxml.jackson.module", "jackson-module-kotlin")
    }
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    testImplementation("io.ktor:ktor-server-test-host:$ktorVersion") {
        exclude("org.jetbrains.kotlinx")
    }

    implementation(kotlin("stdlib-jdk8"))

    val kotlintestVersion = "3.4.2"
    testImplementation("io.kotlintest:kotlintest-core:$kotlintestVersion")
    testRuntime("io.kotlintest:kotlintest-runner-junit5:$kotlintestVersion")

    runtime("ch.qos.logback:logback-classic:1.2.3")

    runtime("org.postgresql:postgresql:42.2.8")
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = application.mainClassName
        attributes["Implementation-Version"] = project.version
    }

    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<Test> {
    useJUnitPlatform()
    systemProperties(mapOf(
	"database.username" to project.property("database.username"),
	"database.password" to project.property("database.password"),
	"database.url" to project.property("database.url")	
    ))
}

tasks.withType<Wrapper> {
    gradleVersion = "5.6"
}

task<Exec>("createImage") {
	commandLine("sudo docker build -t jeremie3407/tpdocker -f docker/Dockerfile .".split(" "))
}
