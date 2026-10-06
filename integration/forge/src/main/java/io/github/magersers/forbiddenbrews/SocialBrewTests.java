package io.github.magersers.forbiddenbrews;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.effect.*;
import net.minecraft.world.item.*;
import net.minecraft.world.phys.*;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.Difficulty;
import net.minecraftforge.gametest.*;

@GameTestHolder(ChaosContent.MOD_ID)
@PrefixGameTestTemplate(false)
public final class SocialBrewTests {
    private static final BlockPos POS=new BlockPos(1,1,1);
    private static void splash(GameTestHelper h,String family,LivingEntity target) {
        var projectile=new ThrownPotion(h.getLevel(),target.getX(),target.getY(),target.getZ()) {
            public void hit(Entity entity) {super.onHit(new EntityHitResult(entity));}
        };
        projectile.setItem(ChaosContent.brew(new BrewSpec(family,1,true)));projectile.hit(target);
    }
    @GameTest(template="empty",timeoutTicks=40)
    public static void truceDrinkAiAndPersonalRetaliation(GameTestHelper h) {
        var player=h.makeMockPlayer();var other=h.makeMockPlayer();
        var zombie=h.spawn(EntityType.ZOMBIE,POS);var neighbour=h.spawn(EntityType.ZOMBIE,POS.offset(2,0,0));
        var creeper=h.spawn(EntityType.CREEPER,POS.offset(0,0,2));
        zombie.setTarget(player);creeper.setTarget(player);creeper.setSwellDir(1);
        var bottle=ChaosContent.brew(new BrewSpec("truce",1,false));
        h.assertTrue(bottle.finishUsingItem(h.getLevel(),player).is(Items.GLASS_BOTTLE),"Drink consumes bottle normally");
        h.assertTrue(player.getEffect(ForbiddenBrews.TRUCE.get()).getDuration()==2400,"Two-minute player protection");
        zombie.tick();creeper.tick();
        h.assertTrue(zombie.getTarget()==null && creeper.getTarget()==null && creeper.getSwellDir()==-1,"Real AI tick abandons targets and defuses unlit creeper");
        zombie.setTarget(player);h.assertTrue(zombie.getTarget()==null && !zombie.canAttack(player),"Target acquisition blocked");
        h.assertTrue(zombie.canAttack(other),"Other player is not protected");
        h.assertFalse(player.hurt(player.damageSources().mobAttack(zombie),2),"Unprovoked attack cannot hurt protected player");
        h.assertTrue(zombie.hurt(zombie.damageSources().playerAttack(player),1),"Player can still hit a mob");
        zombie.setTarget(player);h.assertTrue(zombie.getTarget()==player && zombie.canAttack(player),"Struck mob can retaliate");
        h.assertFalse(neighbour.canAttack(player),"Striking one mob does not anger all mobs");
        new ItemStack(Items.MILK_BUCKET).finishUsingItem(h.getLevel(),player);
        h.assertTrue(neighbour.canAttack(player),"Milk restores ordinary targeting");
        player.addEffect(VersionApi.effect(new BrewSpec("truce",1,false)));
        var dragon=EntityType.ENDER_DRAGON.create(h.getLevel());
        h.assertFalse(dragon.canAttack(player),"Dragon target checks respect protected player");
        h.assertTrue(dragon.hurt(dragon.head,dragon.damageSources().playerAttack(player),2),"Multipart dragon accepts real player hit");
        h.assertTrue(dragon.canAttack(player),"Dragon part hit breaks personal truce");h.succeed();
    }
    @GameTest(template="empty",timeoutTicks=40)
    public static void truceSplashBrainRetaliationAndSave(GameTestHelper h) {
        var attacker=h.makeMockPlayer();var cow=h.spawn(EntityType.COW,POS.offset(2,0,0));
        var zombie=h.spawn(EntityType.ZOMBIE,POS);splash(h,"truce",zombie);
        h.assertTrue(zombie.getEffect(ForbiddenBrews.TRUCE.get()).getDuration()==2400 && !zombie.canAttack(attacker),"Splash pacifies mob toward players");
        zombie.setTarget(cow);h.assertTrue(zombie.getTarget()==null,"Pacified mob cannot acquire other victims");
        var piglin=h.spawn(EntityType.PIGLIN,POS.offset(0,0,2));
        piglin.getBrain().setMemory(MemoryModuleType.ATTACK_TARGET,attacker);
        piglin.addEffect(VersionApi.effect(new BrewSpec("truce",1,false)));Truce.tick(piglin);
        h.assertTrue(piglin.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).isEmpty(),"Brain attack memory is cleared");
        h.assertTrue(zombie.hurt(zombie.damageSources().playerAttack(attacker),1),"A real hit breaks mob pacification");
        h.assertTrue(zombie.canAttack(attacker),"Pacified mob retaliates after a hit");
        var tag=new net.minecraft.nbt.CompoundTag();zombie.saveWithoutId(tag);
        var restored=EntityType.ZOMBIE.create(h.getLevel());restored.load(tag);
        h.assertTrue(restored.hasEffect(ForbiddenBrews.TRUCE.get()) && restored.canAttack(attacker),"Retaliation state survives entity save and load");
        zombie.removeEffect(ForbiddenBrews.TRUCE.get());cow.removeEffect(ForbiddenBrews.TRUCE.get());h.assertTrue(zombie.canAttack(cow),"Removing effect restores ordinary attacks");h.succeed();
    }
    @GameTest(template="empty",timeoutTicks=40)
    public static void swarmDrinkSplashMilkAndTickRate(GameTestHelper h) {
        h.assertTrue(ForbiddenBrews.SWARM.get().getCategory()==MobEffectCategory.HARMFUL,"Swarm is a negative effect");
        h.assertTrue(ForbiddenBrews.TRUCE.get().getCategory()==MobEffectCategory.BENEFICIAL,"Truce is beneficial");
        var player=h.makeMockPlayer();var bottle=ChaosContent.brew(new BrewSpec("swarm",1,false));
        h.assertTrue(bottle.finishUsingItem(h.getLevel(),player).is(Items.GLASS_BOTTLE),"Swarm drink returns glass bottle");
        h.assertTrue(player.getEffect(ForbiddenBrews.SWARM.get()).getDuration()==2400,"Two-minute swarm duration");
        var cow=h.spawn(EntityType.COW,POS);splash(h,"swarm",cow);
        h.assertTrue(cow.getEffect(ForbiddenBrews.SWARM.get()).getDuration()==2400,"Direct splash transfers swarm to living host");
        h.assertTrue(ForbiddenBrews.SWARM.get().isDurationEffectTick(80,0) && !ForbiddenBrews.SWARM.get().isDurationEffectTick(79,0),"Spawn attempts occur once per four seconds");
        new ItemStack(Items.MILK_BUCKET).finishUsingItem(h.getLevel(),player);
        h.assertFalse(player.hasEffect(ForbiddenBrews.SWARM.get()),"Milk removes swarm");h.succeed();
    }
    @GameTest(template="empty",batch="swarm",timeoutTicks=200)
    public static void swarmDaylightCapWorldRulesAndTerrain(GameTestHelper h) {
        var level=h.getLevel();var origin=h.absolutePos(POS).offset(1200,0,300);
        var center=new BlockPos(origin.getX(),200,origin.getZ());var chunk=new ChunkPos(center);
        level.getChunkSource().addRegionTicket(TicketType.FORCED,chunk,3,chunk);
        for(int x=-26;x<=26;x++)for(int z=-26;z<=26;z++) {
            level.setBlockAndUpdate(center.offset(x,-1,z),Blocks.STONE.defaultBlockState());
            for(int y=0;y<4;y++)level.setBlockAndUpdate(center.offset(x,y,z),Blocks.AIR.defaultBlockState());
        }
        h.runAfterDelay(30,()-> {
            boolean spawnRule=level.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING);
            var difficulty=level.getDifficulty();long day=level.getDayTime();
            var host=EntityType.COW.create(level);host.moveTo(center.getX()+.5,200,center.getZ()+.5,0,0);host.setNoAi(true);host.setNoGravity(true);
            var area=new AABB(center).inflate(32);
            try {
                level.getServer().setDifficulty(Difficulty.NORMAL,true);level.setDayTime(6000);
                level.getGameRules().getRule(GameRules.RULE_DOMOBSPAWNING).set(true,level.getServer());
                level.getEntitiesOfClass(Mob.class,area,m->m instanceof Enemy).forEach(Entity::discard);
                boolean spawned=false;for(int i=0;i<40;i++)spawned|=Swarm.summon(host);
                var mobs=level.getEntitiesOfClass(Mob.class,area,m->m instanceof Enemy);
                h.assertTrue(spawned && mobs.size()==Swarm.LIMIT,"Daylight spawns reach bounded cap: "+mobs.size());
                h.assertFalse(Swarm.summon(host),"Cap stops additional spawning");
                h.assertTrue(mobs.stream().allMatch(m->m.getType()==EntityType.CREEPER || m.getType()==EntityType.SPIDER || m.getType()==EntityType.HUSK),"Daylight pool is dimension appropriate and does not burn");
                mobs.forEach(Entity::discard);
                level.getGameRules().getRule(GameRules.RULE_DOMOBSPAWNING).set(false,level.getServer());
                h.assertFalse(Swarm.summon(host),"doMobSpawning=false prevents potion spawning");
                level.getGameRules().getRule(GameRules.RULE_DOMOBSPAWNING).set(true,level.getServer());
                level.getServer().setDifficulty(Difficulty.PEACEFUL,true);h.assertFalse(Swarm.summon(host),"Peaceful prevents hostile spawning");
                level.getServer().setDifficulty(Difficulty.NORMAL,true);
                host.setPos(host.getX(),250,host.getZ());h.assertFalse(Swarm.summon(host),"No floating spawns without nearby support");
                host.setPos(host.getX(),200,host.getZ());
                for(int x=-26;x<=26;x++)for(int z=-26;z<=26;z++)level.setBlockAndUpdate(center.offset(x,0,z),Blocks.WATER.defaultBlockState());
                h.assertFalse(Swarm.summon(host),"No spawns in water");h.succeed();
            } finally {
                host.discard();level.getEntitiesOfClass(Mob.class,area,m->m instanceof Enemy).forEach(Entity::discard);
                level.getGameRules().getRule(GameRules.RULE_DOMOBSPAWNING).set(spawnRule,level.getServer());
                level.getServer().setDifficulty(difficulty,true);level.setDayTime(day);
                level.getChunkSource().removeRegionTicket(TicketType.FORCED,chunk,3,chunk);
            }
        });
    }
}
