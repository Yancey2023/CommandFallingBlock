import os
import requests
import xml.etree.ElementTree as ET

versions = [name for name in os.listdir("./versions") if os.path.isdir(os.path.join("./versions", name))]
loader_versions = requests.get("https://meta.fabricmc.net/v2/versions/loader").json()
fabric_api_versions = [node.text for node in ET.fromstring(
    requests.get("https://maven.fabricmc.net/net/fabricmc/fabric-api/fabric-api/maven-metadata.xml").content).findall(
    ".//versions/version")]

best_loader_version = loader_versions[0]
content = ""
with open("./gradle.properties", "r", encoding="utf-8") as f:
    for line in f:
        if line.startswith("essential.defaults.loom.fabric-loader"):
            content += f"essential.defaults.loom.fabric-loader={best_loader_version['maven']}\n"
        else:
            content += line
with open("./gradle.properties", "w", encoding="utf-8") as f:
    f.write(content)

matching_versions = {}
for versions in versions:
    game_version = versions.split("-")[0]
    if game_version.startswith("1.16"):
        game_version = "1.16"
    best_fabric_api_version = None
    for fabric_api_version in fabric_api_versions:
        if fabric_api_version.endswith(game_version):
            best_fabric_api_version = fabric_api_version
    print(f"{versions} -> {best_fabric_api_version}")
    matching_versions[game_version] = best_fabric_api_version

content = ""
with open("./build.gradle.kts", "r", encoding="utf-8") as f:
    for line in f:
        flag = True
        try:
            current_fabric_api_version: str = line.strip().split(" -> ")[1][1:-1]
            for game_version, fabric_api_version in matching_versions.items():
                if current_fabric_api_version.endswith(game_version):
                    content += line.replace(current_fabric_api_version, fabric_api_version)
                    flag = False
        except IndexError:
            pass
        if flag:
            content += line
with open("./build.gradle.kts", "w", encoding="utf-8") as f:
    f.write(content)
