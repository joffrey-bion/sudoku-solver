plugins {
    kotlin("jvm") version "1.6.10"
    application
}

group = "org.hildan.sudoku"

application {
    mainClass.set("org.hildan.sudoku.SudokuKt")
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.8.2")
}

tasks.test {
    useJUnitPlatform()
    systemProperty("junit.jupiter.execution.parallel.enabled", true)
}
