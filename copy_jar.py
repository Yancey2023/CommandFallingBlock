import os
import shutil

# 从 gradle.properties 中获取 mod_version
with open("./gradle.properties", "r") as f:
    for line in f:
        if line.startswith("version"):
            mod_version = line.split("=")[1].strip()
game_versions = [name for name in os.listdir("./versions") if os.path.isdir(os.path.join("./versions", name))]

release_dest = "./build/libs/release"
sources_dest = "./build/libs/sources"

if not os.path.exists(release_dest):
    os.makedirs(release_dest)
if not os.path.exists(sources_dest):
    os.makedirs(sources_dest)

for game_version in game_versions:
    shutil.copyfile(
        f"./versions/{game_version}/build/libs/commandfallingblock-{game_version}-{mod_version}.jar",
        f"./build/libs/release/commandfallingblock-{game_version}-{mod_version}.jar"
    )
    shutil.copyfile(
         f"./versions/{game_version}/build/libs/commandfallingblock-{game_version}-{mod_version}-sources.jar",
         f"./build/libs/sources/commandfallingblock-{game_version}-{mod_version}-sources.jar"
    )
