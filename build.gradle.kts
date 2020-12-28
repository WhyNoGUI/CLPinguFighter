repositories {
    mavenCentral()
}

plugins {
    id("com.github.johnrengelman.shadow") version "6.1.0"
    application
}

dependencies {
}

group = "com.whynogui.clpf"
version = "0.1"

application {
    mainClassName = "com.whynogui.clpf.CLPinguFighter"
}
