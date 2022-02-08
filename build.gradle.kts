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
    testImplementation("junit:junit:4.13.2")
}
