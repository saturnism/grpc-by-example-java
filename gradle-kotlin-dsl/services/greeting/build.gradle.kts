import com.google.protobuf.gradle.*

plugins {
    idea
    java
    application
    id("com.google.protobuf") version "0.8.8"
}

dependencies {
    compile(project(":proto"))
}

application {
    mainClassName = "com.example.greeting.Application"
}

