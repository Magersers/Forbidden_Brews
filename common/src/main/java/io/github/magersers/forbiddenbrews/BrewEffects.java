package io.github.magersers.forbiddenbrews;

import net.minecraft.world.effect.*;
import net.minecraft.world.entity.*;
import net.minecraft.server.level.ServerPlayer;

public final class BrewEffects {
    public static final class Fortune extends MobEffect {
        public Fortune() { super(MobEffectCategory.BENEFICIAL,0xEEC85A); }
    }
    public static final class Looting extends MobEffect {
        public Looting() { super(MobEffectCategory.BENEFICIAL,0xED4BA6); }
    }
    public static final class Homeward extends MobEffect {
        public Homeward() { super(MobEffectCategory.BENEFICIAL,0xFFB849); }
        @Override public boolean isInstantenous() { return true; }
        @Override public void applyInstantenousEffect(Entity source, Entity owner, LivingEntity victim, int amplifier, double strength) {
            if (victim instanceof ServerPlayer player) VersionApi.home(player);
        }
    }
    public static int lootingBonus(LivingEntity killer) {
        if (killer == null) return 0;
        var effect = VersionApi.looting(killer);
        return effect == null ? 0 : effect.getAmplifier()+1;
    }
    public static int fortuneBonus(LivingEntity miner) {
        var effect=VersionApi.fortune(miner);
        return effect==null?0:effect.getAmplifier()+1;
    }
    private BrewEffects() {}
}
