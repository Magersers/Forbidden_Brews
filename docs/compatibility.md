# Compatibility

| Target | Tested loader | Java | Additional dependency |
|---|---|---|---|
| Forge / Minecraft 1.20.1 | 47.4.0 | 17 | None |
| NeoForge / Minecraft 1.21.1 | 21.1.252 | 21 | None |
| Fabric / Minecraft 1.21.1 | Loader 0.16.14 | 21 | Fabric API 0.116.17+1.21.1 |

Install one matching JAR on both client and server. Newer loader/API releases must still target the listed Minecraft version. Other Minecraft versions are not supported by these files.

Targets were compared using the public [Modrinth search API](https://docs.modrinth.com/api/operations/searchprojects/) on 6 October 2026. Counts are projects tagged with both a loader and game version, a proxy for available mods rather than active-player statistics. They do not guarantee every individual artifact supports that exact combination.

| Compared version | Forge | NeoForge | Fabric |
|---|---:|---:|---:|
| 1.20.1 | 25,085 | 5,064 | 17,609 |
| 1.21.1 | 5,368 | 21,149 | 19,417 |
| 1.21.4 | 4,305 | 9,369 | 13,602 |
| 1.21.8 | 4,136 | 9,085 | 14,169 |
| 1.21.11 | 4,374 | 8,045 | 17,743 |
| 26.1 | 3,279 | 6,118 | 10,648 |

Forge 1.20.1 and NeoForge/Fabric 1.21.1 have the largest counts in this comparison. The three existing targets retain the shared gameplay implementation. Release 0.7.2 adds complete translations and English presentation without changing gameplay logic.
