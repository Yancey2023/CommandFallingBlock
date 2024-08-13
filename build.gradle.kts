plugins {
    // If you're using Kotlin, it needs to be applied before the multi-version plugin
    // kotlin("jvm")
    // Apply the multi-version plugin, this does all the configuration necessary for the preprocessor to
    // work. In particular it also applies `com.replaymod.preprocess`.
    // In addition it primarily also provides a `platform` extension which you can use in this build script
    // to get the version and mod loader of the current project.
    id("gg.essential.multi-version")
    // If you do not care too much about the details, you can just apply essential-gradle-toolkits' defaults for
    // Minecraft, fabric-loader, forge, mappings, etc. versions.
    // You can also overwrite some of these if need be. See the `gg.essential.defaults.loom` README section.
    // Otherwise you'll need to configure those as usual for (architectury) loom.
    id("gg.essential.defaults")
}

dependencies {
    // If you are depending on a multi-version library following the same scheme as the Essential libraries (that is
    // e.g. `elementa-1.8.9-forge`), you can `toString` `platform` directly to get the respective artifact id.
    // modImplementation("gg.essential:elementa-$platform:428")
}

tasks.processResources {
    // Expansions are already set up for `version` (or `file.jarVersion`) and `mcVersionStr`.
    // You do not need to set those up manually.
}

loom {
    // If you need to use a tweaker on legacy (1.12.2 and below) forge:
    // if (platform.isLegacyForge) {
    //     launchConfigs.named("client") {
    //         arg("--tweakClass", "gg.essential.loader.stage0.EssentialSetupTweaker")
    //         // And maybe a core mod?
    //         //  property("fml.coreMods.load", "com.example.asm.CoreMod")
    //     }
    // }
    // Mixin on forge? (for legacy forge you will still need to register a tweaker to set up mixin)
    // if (platform.isForge) {
    //     forge {
    //         mixinConfig("commandfallingblock.mixins.json")
    //         // And maybe an access transformer?
    //         // Though try to avoid these, cause they are not automatically translated to Fabric's access widener
    //         accessTransformer(project.parent.file("src/main/resources/commandfallingblock_at.cfg"))
    //     }
    // }
}