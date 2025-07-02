plugins {
    // This marks the current project as the root of a multi-version project.
    // Any project using `gg.essential.multi-version` must have a parent with this root plugin applied.
    // Advanced users may use multiple (potentially independent) multi-version trees in different sub-projects.
    // This is currently equivalent to applying `com.replaymod.preprocess-root`.
    id("gg.essential.loom") version "1.9.+" apply false
    id("gg.essential.multi-version.root")
}

group = "yancey.commandfallingblock"
version = project.version

preprocess {
    // Here you first need to create a node per version you support and assign it an integer Minecraft version.
    // The mappings value is currently meaningless.
    val fabric11605 = createNode("1.16.5-fabric", 11605, "yarn")
    val fabric11802 = createNode("1.18.2-fabric", 11802, "yarn")
    val fabric12000 = createNode("1.20-fabric", 12000, "yarn")
    val fabric12001 = createNode("1.20.1-fabric", 12001, "yarn")
    val fabric12002 = createNode("1.20.2-fabric", 12002, "yarn")
    val fabric12003 = createNode("1.20.3-fabric", 12003, "yarn")
    val fabric12004 = createNode("1.20.4-fabric", 12004, "yarn")
    val fabric12005 = createNode("1.20.5-fabric", 12005, "yarn")
    val fabric12006 = createNode("1.20.6-fabric", 12006, "yarn")
    val fabric12100 = createNode("1.21-fabric", 12100, "yarn")
    val fabric12101 = createNode("1.21.1-fabric", 12101, "yarn")
    val fabric12102 = createNode("1.21.2-fabric", 12102, "yarn")
    val fabric12103 = createNode("1.21.3-fabric", 12103, "yarn")
    val fabric12104 = createNode("1.21.4-fabric", 12104, "yarn")
    val fabric12105 = createNode("1.21.5-fabric", 12105, "yarn")
    val fabric12106 = createNode("1.21.6-fabric", 12106, "yarn")
    val fabric12107 = createNode("1.21.7-fabric", 12107, "yarn")

    // And then you need to tell the preprocessor which versions it should directly convert between.
    // This should form a directed graph with no cycles (i.e. a tree), which the preprocessor will then traverse to
    // produce source code for all versions from the main version.
    // Do note that the preprocessor can only convert between two projects when they are either on the same Minecraft
    // version (but use different mappings, e.g. 1.16.2 forge to fabric), or when they are using the same intermediary
    // mappings (but on different Minecraft versions, e.g. 1.12.2 forge to 1.8.9 forge, or 1.16.2 fabric to 1.18 fabric)
    // but not both at the same time, i.e. you cannot go straight from 1.12.2 forge to 1.16.2 fabric, you need to go via
    // an intermediary 1.16.2 forge project which has something in common with both.
    fabric12107.link(fabric12106)
    fabric12106.link(fabric12105)
    fabric12105.link(fabric12104)
    fabric12104.link(fabric12103)
    fabric12103.link(fabric12102)
    fabric12102.link(fabric12101)
    fabric12101.link(fabric12100)
    fabric12100.link(fabric12006)
    fabric12006.link(fabric12005)
    fabric12005.link(fabric12004)
    fabric12004.link(fabric12003)
    fabric12003.link(fabric12002)
    fabric12002.link(fabric12001)
    fabric12001.link(fabric12000)
    fabric12000.link(fabric11802)
    fabric11802.link(fabric11605, file("versions/mapping-1.18.2-1.16.5.txt"))
    // For any link, you can optionally specify a file containing extra mappings which the preprocessor cannot infer by
    // itself, e.g. forge intermediary names do not contain class names, so you may need to supply mappings for those
    // manually.
    // forge11202.link(forge10809, file("versions/1.12.2-1.8.9.txt"))
}