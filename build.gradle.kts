repositories {
    mavenCentral()
}

plugins {
    id("com.github.johnrengelman.shadow") version "6.1.0"
    application
}

dependencies {
    implementation(group="org.processing", name="core", version="3.3.7")
}

group = "com.whynogui.clpf"
version = "0.1"

application {
    mainClassName = "com.whynogui.clpf.CLPinguFighter"
}
