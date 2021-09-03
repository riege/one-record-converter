This directory is configured as local repository for resolving dependencies.
In build.gradle buildscript it is added via:

    repositories {
        flatDir dirs: "$rootProject.projectDir/lib"
        ..
    }

