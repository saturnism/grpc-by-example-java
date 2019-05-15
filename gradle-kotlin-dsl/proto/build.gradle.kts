import com.google.protobuf.gradle.*

plugins {
    idea
    `java-library`
    id("com.google.protobuf") version "0.8.8"
}

dependencies {
    compile("io.grpc:grpc-netty-shaded:1.20.0")
    compile("io.grpc:grpc-protobuf:1.20.0")
    compile("io.grpc:grpc-stub:1.20.0")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.7.1"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.20.0"
        }
    }
    generateProtoTasks {
        ofSourceSet("main").forEach {
            it.plugins {
                // Apply the "grpc" plugin whose spec is defined above, without options.
                id("grpc")
            }
        }
    }
}

idea {
    module {
        generatedSourceDirs.addAll(listOf(
                file("${protobuf.protobuf.generatedFilesBaseDir}/main/grpc"),
                file("${protobuf.protobuf.generatedFilesBaseDir}/main/java")
        ))
    }
}


