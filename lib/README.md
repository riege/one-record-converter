This directory is configured as local repository for resolving dependencies.
In build.gradle buildscript it is added via:

    repositories {
        flatDir dirs: "$rootProject.projectDir/lib"
        ..
    }

Note: cargoxml-jaxb*.jar **must** be placed here because due to it's nature it is
not possible to get it from https://jitpack.io 
