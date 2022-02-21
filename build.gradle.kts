plugins {
    val kotlinVersion = "1.6.10"
    kotlin("jvm") version kotlinVersion apply false
    kotlin("multiplatform") version kotlinVersion apply false
}

allprojects {
    group = "org.hildan.sudoku"

    repositories {
        mavenCentral()
    }
}
