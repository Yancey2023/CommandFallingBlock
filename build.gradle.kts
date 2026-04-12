plugins {
    id("gg.essential.multi-version")
    id("gg.essential.defaults") apply false
}

val mcVersion = platform.mcVersion
extra["essential.defaults.loom.mappings"] = if (mcVersion >= 26_01_00) "" else "official"
apply(plugin = "gg.essential.defaults")

version = "${project.name}-${rootProject.version}"
base.archivesName.set("commandfallingblock")
java.withSourcesJar()

val fabricApiVersion = when (mcVersion) {
    11605 -> "0.42.0+1.16"
    11802 -> "0.77.0+1.18.2"
    12000 -> "0.83.0+1.20"
    12001 -> "0.92.7+1.20.1"
    12002 -> "0.91.6+1.20.2"
    12003 -> "0.91.1+1.20.3"
    12004 -> "0.97.3+1.20.4"
    12005 -> "0.97.8+1.20.5"
    12006 -> "0.100.8+1.20.6"
    12100 -> "0.102.0+1.21"
    12101 -> "0.116.10+1.21.1"
    12102 -> "0.106.1+1.21.2"
    12103 -> "0.114.1+1.21.3"
    12104 -> "0.119.4+1.21.4"
    12105 -> "0.128.2+1.21.5"
    12106 -> "0.128.2+1.21.6"
    12107 -> "0.129.0+1.21.7"
    12108 -> "0.136.1+1.21.8"
    12109 -> "0.134.1+1.21.9"
    12110 -> "0.138.4+1.21.10"
    12111 -> "0.141.3+1.21.11"
    26_01_00 -> "0.145.1+26.1"
    26_01_01 -> "0.145.4+26.1.1"
    else -> throw UnsupportedOperationException()
}

dependencies {
    implementation("net.fabricmc.fabric-api:fabric-api:${fabricApiVersion}")
}
