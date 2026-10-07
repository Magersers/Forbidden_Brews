# Forbidden Brews 0.7.4 — Random Teleport hotfix

Three loader builds with identical gameplay changes. Publication resumed at the owner's request on 7 October 2026; current platform status is recorded in [publishing/README.md](../../publishing/README.md).

Random Teleport searches neighbouring columns throughout each prepared chunk, including solid tree canopies, and samples destinations inside the world border. It tries up to 24 locations with a bounded search, retries unsuccessful chunk requests, and holds a temporary chunk ticket until each request finishes. Chunk readiness comes from the FULL-generation result rather than a separate visible-cache check.

A drink remains in the player's inventory while searching. A successful teleport consumes exactly one potion and returns an empty bottle; an unsuccessful or cancelled search keeps the original potion. Dropping or transferring the pending bottle outside the player's inventory cancels that drink, and repeated use cannot start overlapping searches. Creative mode retains its potion. Splash potions share the improved destination search; thrown splash bottles are still consumed when thrown.

All Brute, Shapeshifter, Gravity and interface changes from 0.7.3 are retained.

| Minecraft / loader | JAR |
|---|---|
| Forge 1.20.1 | [Forge](forbidden-brews-forge-1.20.1-0.7.4.jar) |
| NeoForge 1.21.1 | [NeoForge](forbidden-brews-neoforge-1.21.1-0.7.4.jar) |
| Fabric 1.21.1 | [Fabric](forbidden-brews-fabric-1.21.1-0.7.4.jar) |

Replace the matching JAR on both client and server. Install only one loader variant. Requirements remain Java 17 for Forge and Java 21 for NeoForge/Fabric; Fabric also requires Fabric API.

Validation evidence is recorded in [the test report](TESTING.md). Checksums are in [manifest.json](manifest.json).
