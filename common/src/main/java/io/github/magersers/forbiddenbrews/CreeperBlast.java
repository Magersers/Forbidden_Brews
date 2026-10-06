package io.github.magersers.forbiddenbrews;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.level.Level;

public final class CreeperBlast {
    public static void drink(LivingEntity drinker) {
        var level=drinker.level();if(level.isClientSide || !drinker.isAlive())return;
        // Vanilla excludes the source entity from its blast targets. Other
        // entities get ordinary damage; the drinker gets one small damage hit.
        level.explode(drinker,drinker.getX(),drinker.getY(),drinker.getZ(),3,false,Level.ExplosionInteraction.TNT);
        drinker.hurt(level.damageSources().explosion(drinker,drinker),4);
    }
    public static boolean splash(ThrownPotion projectile) {
        if(!(projectile.getItem().getItem() instanceof BrewItem item) || !item.spec.splash() || !item.spec.family().equals("creeper"))return false;
        if(!projectile.level().isClientSide && !projectile.isRemoved()) {
            projectile.level().explode(projectile,projectile.getX(),projectile.getY(),projectile.getZ(),4,false,Level.ExplosionInteraction.TNT);
            projectile.discard();
        }
        return true;
    }
    private CreeperBlast() {}
}
