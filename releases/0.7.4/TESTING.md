# 0.7.4 validation

Run on 6 October 2026 using Java 17 for Forge 1.20.1 and Java 21 for NeoForge/Fabric 1.21.1.

The Forge GameTest suite passed **all 43 required tests**, including the previous gameplay checks and seven new Random Teleport regressions:

- Generated, previously unloaded distant terrain can be prepared and searched.
- An underwater random column can be replaced by a safe neighbour in the same chunk; solid leaf canopies are eligible.
- One hundred sampled destinations remain within a small world border.
- Cancellation on player death preserves the exact original potion.
- Every candidate chunk filled with magma causes a bounded failure, preserving the original potion without empty-bottle duplication or consumption statistics.
- Offhand survival drinking returns glass in the offhand slot, leaving the main hand alone; creative retains its potion. These cases invoke Minecraft's native start/complete-use path, including Forge's item-use event.
- Transferring the pending bottle out of the inventory cancels the drink without granting a free teleport or extra items.

The existing successful Random Teleport regression also checks deferred consumption, one returned glass bottle, and cleared velocity/fall distance.

Commands:

```text
gradlew.bat -PgameTests runGameTestServer --no-daemon
gradlew.bat clean build --no-daemon
gradlew.bat -Ptarget=neoforge-1.21.1 clean build --no-daemon
gradlew.bat -Ptarget=fabric-1.21.1 clean build --no-daemon
python tools/package_release.py
```

Production JAR packaging checks reject GameTest/visual fixtures, verify the required gameplay classes and mixins, validate all language packs, and record SHA-256 hashes.

A separate native client/server visual attempt timed out during login, before the drinking scenario began. No client screenshot or client visual pass is claimed. The server GameTests exercise actual Minecraft terrain, inventory and use events. This run does not reproduce the owner's particular save or modpack, and runtime GameTests were run on Forge; NeoForge/Fabric were compiled and packaged.

The Forge 0.7.3 CurseForge file was observed Archived; no 0.7.3 GitHub release/tag was created. Publication of 0.7.4 resumed on 7 October 2026; current platform status is recorded in [publishing/README.md](../../publishing/README.md).
