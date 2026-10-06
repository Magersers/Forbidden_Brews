package io.github.magersers.forbiddenbrews;

import java.util.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/** Original entities keep their inventory, health, AI, UUID and ownership. */
public final class Morphs {
    public static final int BAT=1,BRUTE=2;
    private static final List<EntityType<? extends LivingEntity>> LEGACY=List.of(EntityType.PIG,EntityType.COW,
        EntityType.SHEEP,EntityType.WOLF,EntityType.FOX,EntityType.RABBIT,EntityType.CHICKEN,EntityType.BEE,
        EntityType.SPIDER,EntityType.ZOMBIE,EntityType.SKELETON,EntityType.CREEPER,EntityType.ENDERMAN,EntityType.SLIME,EntityType.BAT);
    private static final Map<Level,List<EntityType<? extends LivingEntity>>> POOLS=new WeakHashMap<>();
    public static List<EntityType<? extends LivingEntity>> pool(Level world) {
        return POOLS.computeIfAbsent(world,level-> {
            var types=new ArrayList<EntityType<? extends LivingEntity>>();
            for(var type:BuiltInRegistries.ENTITY_TYPE) {
                if(type==EntityType.PLAYER)continue;
                try {
                    if(type.create(level) instanceof Mob) {
                        @SuppressWarnings("unchecked") var mobType=(EntityType<? extends LivingEntity>)type;
                        types.add(mobType);
                    }
                } catch(RuntimeException error) {
                    com.mojang.logging.LogUtils.getLogger().warn("Cannot create morph proxy for {}",BuiltInRegistries.ENTITY_TYPE.getKey(type),error);
                }
            }
            types.sort(Comparator.comparing(t->BuiltInRegistries.ENTITY_TYPE.getKey(t).toString()));
            return List.copyOf(types);
        });
    }
    public static int formOf(EntityType<?> type) {return type==null?0:3+BuiltInRegistries.ENTITY_TYPE.getId(type);}
    public static int legacyForm(int form) {return form>=3 && form<3+LEGACY.size()?formOf(LEGACY.get(form-3)):0;}
    public static int form(LivingEntity entity) {
        var state=(MorphState)entity;
        return state.brews$data()==null || !entity.isAlive()?0:state.brews$form();
    }
    @SuppressWarnings("unchecked") public static EntityType<? extends LivingEntity> type(int form) {
        return form==BAT?EntityType.BAT:form>=3?(EntityType<? extends LivingEntity>)BuiltInRegistries.ENTITY_TYPE.byId(form-3):null;
    }
    public static boolean bat(int form) {return form==BAT || type(form)==EntityType.BAT;}
    public static EntityDimensions dimensions(int form) {
        return form==BRUTE?VersionApi.bruteDimensions():type(form)==null?null:type(form).getDimensions();
    }
    public static boolean fits(LivingEntity entity,int form) {
        var dims=dimensions(form);if(dims==null)return true;
        return entity.level().noCollision(entity,dims.makeBoundingBox(entity.position()).deflate(.001));
    }
    private static int choose(LivingEntity entity,int previous) {
        var choices=pool(entity.level());if(choices.isEmpty())return 0;
        int start=entity.getRandom().nextInt(choices.size());
        for(int i=0;i<choices.size();i++) {
            int candidate=formOf(choices.get((start+i)%choices.size()));
            if(candidate!=previous && fits(entity,candidate))return candidate;
        }
        return fits(entity,previous)?previous:formOf(EntityType.RABBIT);
    }
    public static void tick(LivingEntity entity) {
        var state=(MorphState)entity;var data=state.brews$data();if(data==null)return;
        if(!entity.level().isClientSide) {
            var random=VersionApi.effectInstance(entity,"shapeshifter");
            if(random==null || !entity.isAlive()) {data.randomForm=0;data.previousDuration=-1;}
            else {
                if(data.randomForm<3 || !pool(entity.level()).contains(type(data.randomForm)) || data.reroll
                    || random.getDuration()>data.previousDuration && data.previousDuration>=0)
                    data.randomForm=choose(entity,data.randomForm);
                data.previousDuration=random.getDuration();
            }
            data.reroll=false;
            int desired=0;
            if(entity.isAlive()) {
                // Headroom gates entry, not an already active transformation. Rechecking
                // every tick used to cancel Brute in cramped mines or under falling blocks.
                if(VersionApi.effectInstance(entity,"juggernaut")!=null)desired=state.brews$form()==BRUTE || fits(entity,BRUTE)?BRUTE:0;
                else if(VersionApi.effectInstance(entity,"bat")!=null)desired=BAT;
                else if(random!=null)desired=data.randomForm;
            }
            boolean blocked=entity.isAlive() && VersionApi.effectInstance(entity,"juggernaut")!=null && desired==0;
            if(blocked && !data.blocked && entity instanceof net.minecraft.server.level.ServerPlayer player)
                player.displayClientMessage(net.minecraft.network.chat.Component.translatable("message.forbidden_brews.brute_space"),true);
            data.blocked=blocked;
            if(state.brews$form()!=desired) {
                state.brews$form(desired);
                if(desired>0 && entity instanceof net.minecraft.server.level.ServerPlayer player)
                    player.displayClientMessage(net.minecraft.network.chat.Component.translatable("message.forbidden_brews.form",
                        desired==BRUTE?net.minecraft.network.chat.Component.translatable("entity.forbidden_brews.brute"):type(desired).getDescription()),true);
            }
            if(entity instanceof Player player)flight(player,data,bat(desired));
            VersionApi.bruteAttributes(entity,desired==BRUTE);
        }
        int form=form(entity);
        if(data.lastForm!=form) {data.lastForm=form;entity.refreshDimensions();}
        Gravity.tick(entity);
        if(data.safeLanding) {
            entity.fallDistance=0;
            if(entity.onGround() || entity.isInWater() || entity.isPassenger())data.safeLanding=false;
        }
    }
    private static void flight(Player player,MorphState.Data data,boolean bat) {
        var abilities=player.getAbilities();
        if(bat) {
            if(!data.flightOwned) {
                data.flightOwned=true;data.mayfly=abilities.mayfly;data.flying=abilities.flying;
                data.creative=player.isCreative();data.spectator=player.isSpectator();data.flyingSpeed=abilities.getFlyingSpeed();
                abilities.mayfly=true;abilities.flying=true;player.onUpdateAbilities();
            } else if(!abilities.mayfly) {abilities.mayfly=true;player.onUpdateAbilities();}
            player.fallDistance=0;
        } else if(data.flightOwned) {
            data.flightOwned=false;
            boolean sameMode=data.creative==player.isCreative() && data.spectator==player.isSpectator();
            abilities.mayfly=sameMode?data.mayfly:player.isCreative() || player.isSpectator();
            abilities.flying=abilities.mayfly && (sameMode?data.flying:player.isSpectator());
            abilities.setFlyingSpeed(data.flyingSpeed);player.onUpdateAbilities();data.safeLanding=!player.onGround();
        }
    }
    private Morphs() {}
}
