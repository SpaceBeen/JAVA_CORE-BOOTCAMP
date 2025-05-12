plugins {
    id("com.gradleup.shadow") version "8.3.2"
    id("application")
    id("com.github.johnrengelman.shadow") version "6.0.0"
}

group = "ru.s21.rogue"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("com.baulsupp.kolja:jcurses:0.9.5.3")
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set("ru.s21.rogue.Main")
}

// Shadow task depends on Jar task, so these will be reflected for Shadow as well
tasks.jar {
    manifest.attributes["Main"] = "ru.s21.rogue.Main"
}
