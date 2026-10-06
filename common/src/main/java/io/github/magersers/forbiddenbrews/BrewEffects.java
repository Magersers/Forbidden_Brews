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
    public static final class WildTeleport extends MobEffect {
        public WildTeleport() { super(MobEffectCategory.BENEFICIAL,0x9D6FFF); }
        @Override public boolean isInstantenous() { return true; }
        @Override public void applyInstantenousEffect(Entity source, Entity owner, LivingEntity victim, int amplifier, double strength) {
            if(victim instanceof ServerPlayer player) RandomTeleport.teleport(player);
        }
    }
    public static final class OreDouble extends MobEffect {
        public OreDouble() { super(MobEffectCategory.BENEFICIAL,0x68DFDB); }
    }
    public static final class HotPick extends MobEffect {
        public HotPick() { super(MobEffectCategory.BENEFICIAL,0xFF8A38); }
    }
    public static final class Inversion extends MobEffect {
        public Inversion() { super(MobEffectCategory.HARMFUL,0xDD70E8); }
    }
    public static final class Truce extends MobEffect {
        public Truce() {super(MobEffectCategory.BENEFICIAL,0x86EAB3);}
    }
    public static final class OreSight extends MobEffect {
        public OreSight() {super(MobEffectCategory.BENEFICIAL,0x53B9FF);}
    }
    public static final class Hunter extends MobEffect {
        public Hunter() {super(MobEffectCategory.BENEFICIAL,0xFFBC64);}
    }
    public static final class Bat extends MobEffect {
        public Bat() {super(MobEffectCategory.BENEFICIAL,0x8462F3);}
    }
    public static final class Juggernaut extends MobEffect {
        public Juggernaut() {super(MobEffectCategory.BENEFICIAL,0xFA643A);}
    }
    public static final class Shapeshifter extends MobEffect {
        public Shapeshifter() {super(MobEffectCategory.BENEFICIAL,0xB68AFF);}
    }
    public static final class Gravity extends MobEffect {
        public Gravity() {super(MobEffectCategory.BENEFICIAL,0x7CABFF);}
    }
    public static final class Swarm extends TickingBrewEffect {
        public Swarm() {super(MobEffectCategory.HARMFUL,0xEC4263);}
    }
    public static final class Creeper extends MobEffect {
        public Creeper() { super(MobEffectCategory.HARMFUL,0x75ED58); }
        @Override public boolean isInstantenous() { return true; }
        @Override public void applyInstantenousEffect(Entity source, Entity owner, LivingEntity victim, int amplifier, double strength) {
            CreeperBlast.drink(victim);
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
