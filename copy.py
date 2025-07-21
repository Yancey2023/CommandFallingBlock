import os
import shutil

modVersion = '1.12'
mcVersions = [
    "1.16.5-fabric",
    "1.18.2-fabric",
    "1.20-fabric",
    "1.20.1-fabric",
    "1.20.2-fabric",
    "1.20.3-fabric",
    "1.20.4-fabric",
    "1.20.5-fabric",
    "1.20.6-fabric",
    "1.21-fabric",
    "1.21.1-fabric",
    "1.21.2-fabric",
    "1.21.3-fabric",
    "1.21.4-fabric",
    "1.21.5-fabric",
    "1.21.6-fabric",
    "1.21.7-fabric",
    "1.21.8-fabric"
]

release_dest = "./build/libs/release"
sources_dest = "./build/libs/sources"

if not os.path.exists(release_dest):
    os.makedirs(release_dest)
if not os.path.exists(sources_dest):
    os.makedirs(sources_dest)

for mcVersion in mcVersions:
    shutil.copyfile(
        f"./versions/{mcVersion}/build/libs/commandfallingblock-{mcVersion}-{modVersion}.jar",
        f"./build/libs/release/commandfallingblock-{mcVersion}-{modVersion}.jar"
    )
    shutil.copyfile(
         f"./versions/{mcVersion}/build/libs/commandfallingblock-{mcVersion}-{modVersion}-sources.jar",
         f"./build/libs/sources/commandfallingblock-{mcVersion}-{modVersion}-sources.jar"
    )
