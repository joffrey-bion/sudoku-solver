plugins {
    kotlin("multiplatform")
}

kotlin {
    jvm()
    js {
        browser()
    }
    sourceSets {
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit5"))
                implementation("org.junit.jupiter:junit-jupiter-params:5.8.2")
            }
        }
    }
}

tasks.named<Test>("jvmTest") {
    useJUnitPlatform()
    systemProperty("junit.jupiter.execution.parallel.enabled", true)
}
