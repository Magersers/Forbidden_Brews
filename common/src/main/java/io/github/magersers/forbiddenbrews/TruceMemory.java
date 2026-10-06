package io.github.magersers.forbiddenbrews;

import java.util.Map;
import java.util.UUID;

/** Per-mob, saved retaliation state; never shared between players or servers. */
public interface TruceMemory {
    Map<UUID,Long> brews$provocations();
    long brews$brokenUntil();
    void brews$brokenUntil(long time);
}
