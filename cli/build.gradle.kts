plugins {
    kotlin("jvm")
    application
}

application {
    mainClass.set("org.hildan.sudoku.MainKt")
}

dependencies {
    implementation(project(":solver"))
}
