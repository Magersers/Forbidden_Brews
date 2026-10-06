package io.github.magersers.forbiddenbrews.visual;

import io.github.magersers.forbiddenbrews.client.*;
import io.github.magersers.forbiddenbrews.*;
import net.minecraft.client.*;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.core.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.InteractionHand;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.nio.file.Path;

@Mixin(Minecraft.class)
public abstract class VisualClientMixin {
    @Unique private boolean brews$connected,brews$world,brews$baseShot,brews$upgradeShot;
    @Unique private int brews$frame,brews$screenTicks,brews$fxFrame,brews$diagnostic,brews$batTicks,brews$bruteTicks,brews$randomTicks,brews$gravityTicks;
    @Unique private long brews$last=-1;
    @Unique private boolean brews$ore,brews$gold,brews$bat,brews$flight,brews$brute,brews$smash,brews$random,brews$up,brews$down,brews$landed,brews$cleared,brews$mining;
    @Unique private int brews$standFrames;
    @Unique private boolean brews$craftingShot;
    @Unique private int brews$stackShots;
    @Unique private int brews$073Shots;
    @Unique private net.minecraft.world.entity.Entity brews$073Observer;
    @Inject(method="tick",at=@At("TAIL"))
    private void brews$connectAndControl(CallbackInfo ci) {
        Minecraft mc=(Minecraft)(Object)this;
        if(!brews$connected && mc.screen instanceof TitleScreen) {
            mc.options.setCameraType(CameraType.FIRST_PERSON);mc.options.keyJump.setDown(false);
            brews$connected=true;ConnectScreen.startConnecting(mc.screen,mc,ServerAddress.parseString("127.0.0.1:25584"),new ServerData("Forbidden Brews visual test","127.0.0.1:25584",false),false);
        }
        if(java.nio.file.Files.exists(Path.of(mc.gameDirectory.getAbsolutePath(),"073-capture.flag"))) {
            if(mc.player!=null) {
                mc.getToasts().clear();((MouseCoordinates)mc.mouseHandler).brews$setX(5);((MouseCoordinates)mc.mouseHandler).brews$setY(5);
                if(mc.player.tickCount>=300 && mc.player.tickCount<325) {
                    if(brews$073Observer==null)brews$073Observer=net.minecraft.world.entity.EntityType.COW.create(mc.level);
                    var observer=brews$073Observer;observer.setPos(mc.player.position().add(0,0,-6));observer.xo=observer.getX();observer.yo=observer.getY();observer.zo=observer.getZ();observer.setYRot(0);observer.yRotO=0;observer.setXRot(0);observer.xRotO=0;
                    mc.setCameraEntity(observer);mc.options.setCameraType(CameraType.FIRST_PERSON);
                } else {
                    mc.setCameraEntity(mc.player);
                    mc.options.setCameraType(mc.player.tickCount>=210 && mc.player.tickCount<285 || mc.player.tickCount>=325 && mc.player.tickCount<340?CameraType.FIRST_PERSON:CameraType.THIRD_PERSON_FRONT);
                }
            }
            return;
        }
        if(mc.screen instanceof ChaosScreen screen) {
            brews$screenTicks++;int x=(screen.width-256)/2,y=(screen.height-256)/2;
            if(brews$screenTicks==5 || brews$screenTicks==50)screen.mouseClicked(x+128,y+136,0);
            if(brews$screenTicks==25)screen.mouseClicked(x+182,y+225,0);
            if(brews$screenTicks==65)screen.mouseClicked(x+128,y+76,0);
            if(brews$screenTicks==35 && !ChaosRecipes.ALL.get(screen.getMenu().selectedRecipe()).result().equals(new BrewSpec("juggernaut",1,false)))throw new IllegalStateException("Brute base choice not synchronized");
            if(brews$screenTicks==95 && !ChaosRecipes.ALL.get(screen.getMenu().selectedRecipe()).result().equals(new BrewSpec("juggernaut",1,true)))throw new IllegalStateException("Brute splash choice not synchronized");
        }
        if(mc.player==null || mc.level==null || mc.screen!=null)return;
        if(java.nio.file.Files.exists(Path.of(mc.gameDirectory.getAbsolutePath(),"sight-morph.flag"))) {
            mc.options.setCameraType(CameraType.THIRD_PERSON_BACK);return;
        }
        int form=Morphs.form(mc.player);
        if(form==Morphs.BAT) {
            if(++brews$batTicks==1) {mc.options.setCameraType(CameraType.THIRD_PERSON_FRONT);mc.options.keyJump.setDown(true);}
            if(brews$batTicks==2)mc.options.keyJump.setDown(false);
            if(brews$batTicks==4)mc.options.keyJump.setDown(true);
            if(brews$batTicks==20)mc.options.keyJump.setDown(false);
        } else if(brews$batTicks>0 && brews$gravityTicks==0)mc.options.keyJump.setDown(false);
        if(form==Morphs.BRUTE) {
            brews$bruteTicks++;var pos=new BlockPos(1,66,-20);
            if(brews$bruteTicks>=45 && mc.level.getBlockState(pos).is(Blocks.STONE)) {
                if(!brews$mining) {mc.gameMode.startDestroyBlock(pos,Direction.SOUTH);brews$mining=true;mc.player.swing(InteractionHand.MAIN_HAND);}
                else mc.gameMode.continueDestroyBlock(pos,Direction.SOUTH);
            }
        }
        if(form>=3)brews$randomTicks++;
        if(VersionApi.effectInstance(mc.player,"gravity")!=null) {
            brews$gravityTicks++;
            if(brews$gravityTicks==1 || brews$gravityTicks==120)mc.options.keyJump.setDown(true);
            if(brews$gravityTicks==12 || brews$gravityTicks==132)mc.options.keyJump.setDown(false);
        } else if(brews$gravityTicks>0)mc.options.keyJump.setDown(false);
    }
    @Unique private void brews$shot(Minecraft mc,String name) throws Exception {
        try(var screenshot=Screenshot.takeScreenshot(mc.getMainRenderTarget())) {screenshot.writeToFile(Path.of(mc.gameDirectory.getAbsolutePath(),name));}
    }
    @Inject(method="runTick",at=@At("TAIL"))
    private void brews$capture(boolean render,CallbackInfo ci) throws Exception {
        Minecraft mc=(Minecraft)(Object)this;if(!render)return;
        if(mc.player==null || mc.level==null) {
            if(++brews$diagnostic%120==0) {System.out.println("VISUAL_SCREEN "+mc.screen);brews$shot(mc,"visual-loading.png");}return;
        }
        if(java.nio.file.Files.exists(Path.of(mc.gameDirectory.getAbsolutePath(),"073-capture.flag"))) {
            int[] shots={45,95,155,180,245,310,335,360,410};
            if(brews$073Shots<shots.length && mc.player.tickCount>=shots[brews$073Shots]) {
                if((brews$073Shots==5 || brews$073Shots==6) && !Gravity.inverted(mc.player) || brews$073Shots>=7 && Gravity.inverted(mc.player))
                    throw new IllegalStateException("Gravity visual orientation is not synchronized");
                brews$shot(mc,"073-native-"+brews$073Shots+".png");System.out.println("073_CLIENT_CAPTURE "+brews$073Shots+" gravity="+Gravity.inverted(mc.player));brews$073Shots++;
            }
            if(mc.player.tickCount>=425) {mc.options.setCameraType(CameraType.FIRST_PERSON);mc.stop();}
            return;
        }
        if(java.nio.file.Files.exists(Path.of(mc.gameDirectory.getAbsolutePath(),"sight-morph.flag"))) {
            int[] shots={65,145,245,365,485,545};
            if(brews$stackShots<shots.length && mc.player.tickCount>=shots[brews$stackShots]) {
                int form=Morphs.form(mc.player);var ores=SightCache.ores();
                if(brews$stackShots>=1 && brews$stackShots<=4 && (ores.size()!=11 || (brews$stackShots<=2?form!=Morphs.BRUTE:form<3)))
                    throw new IllegalStateException("Sight/morph client mismatch: shot="+brews$stackShots+" form="+form+" ores="+ores.size());
                brews$shot(mc,"sight-morph-"+brews$stackShots+".png");
                System.out.println("SIGHT_MORPH_CLIENT_STACK "+brews$stackShots+" form="+form+" ores="+ores.size());brews$stackShots++;
            }
            if(mc.player.tickCount>=550) {mc.options.setCameraType(CameraType.FIRST_PERSON);mc.stop();}
            return;
        }
        if(java.nio.file.Files.exists(Path.of(mc.gameDirectory.getAbsolutePath(),"stand-capture.flag"))) {
            mc.getToasts().clear();((MouseCoordinates)mc.mouseHandler).brews$setX(5);((MouseCoordinates)mc.mouseHandler).brews$setY(5);
            if(mc.screen instanceof net.minecraft.client.gui.screens.inventory.CraftingScreen && mc.player.tickCount>40 && !brews$craftingShot) {
                brews$shot(mc,"chaos-crafting-native.png");brews$craftingShot=true;
                System.out.println("NATIVE_CHAOS_STAND_CRAFTING_CAPTURED");
            }
            if(mc.screen==null && mc.player.tickCount>=120 && mc.player.tickCount<=225 && mc.level.getGameTime()>=brews$last+2) {
                brews$last=mc.level.getGameTime();mc.options.hideGui=true;
                brews$shot(mc,String.format("chaos-stand-native-%03d.png",brews$standFrames++));
            }
            if(mc.player.tickCount>=240) {mc.options.hideGui=false;mc.options.save();mc.stop();}
            return;
        }
        String name=null;
        if(!brews$world && mc.screen==null && mc.player.tickCount>30) {name="chaos-world.png";brews$world=true;}
        if(mc.screen instanceof ChaosScreen && mc.level.getGameTime()>=brews$last+2) {
            mc.getToasts().clear();((MouseCoordinates)mc.mouseHandler).brews$setX(5);((MouseCoordinates)mc.mouseHandler).brews$setY(5);
            brews$last=mc.level.getGameTime();name=String.format("chaos-ui-%03d.png",brews$frame++);
            if(brews$screenTicks>=10 && brews$screenTicks<25 && !brews$baseShot) {brews$shot(mc,"chaos-picker-base.png");brews$baseShot=true;}
            if(brews$screenTicks>=55 && brews$screenTicks<65 && !brews$upgradeShot) {brews$shot(mc,"chaos-picker-upgrades.png");brews$upgradeShot=true;}
        }
        if(brews$frame>=200 && mc.screen==null && mc.level.getGameTime()>=brews$last+2) {
            brews$last=mc.level.getGameTime();name=String.format("chaos-fx-%03d.png",brews$fxFrame++);
            var ores=SightCache.ores();
            var expected=java.util.List.of(new BlockPos(0,66,-3),new BlockPos(4,66,-3),new BlockPos(-4,67,-3),new BlockPos(-2,69,-3),new BlockPos(2,69,-3),new BlockPos(6,68,-3),new BlockPos(0,71,-3),new BlockPos(-5,70,-3),new BlockPos(7,71,-3),new BlockPos(1,65,14));
            if(ores.containsAll(expected) && !brews$ore) {
                if(ores.contains(new BlockPos(1,65,17)))throw new IllegalStateException("Ore outside 32 blocks is highlighted");
                brews$ore=true;brews$shot(mc,"xray-diamond.png");System.out.println("REMAINING_CLIENT_ALL_ORES_32_OK "+ores.size());
            }
            if(brews$ore && ores.contains(new BlockPos(4,66,-3)) && !ores.contains(new BlockPos(0,66,-3)) && !brews$gold) {
                brews$gold=true;brews$shot(mc,"xray-gold.png");System.out.println("REMAINING_CLIENT_MINED_ORE_REMOVED_OTHERS_REMAIN_OK");
            }
            int form=Morphs.form(mc.player);
            if(form==Morphs.BAT && brews$batTicks>=25 && !brews$bat) {brews$bat=true;brews$shot(mc,"morph-bat.png");System.out.println("REMAINING_CLIENT_BAT_MODEL_OK");}
            if(form==Morphs.BAT && mc.player.getAbilities().flying && mc.player.getY()>66 && !brews$flight) {brews$flight=true;System.out.println("REMAINING_CLIENT_BAT_TAKEOFF_OK");}
            if(form==Morphs.BRUTE && brews$bruteTicks>=10 && !brews$brute) {
                if(mc.player.getMaxHealth()!=40 || mc.player.getHealth()!=40)throw new IllegalStateException("Brute health bonus was not synchronized");
                brews$brute=true;brews$shot(mc,"morph-brute.png");System.out.println("REMAINING_CLIENT_BRUTE_MODEL_AND_DOUBLE_HEALTH_OK");
            }
            if(brews$mining && !brews$smash && Destruction.plane(new BlockPos(1,66,-20),new net.minecraft.world.phys.Vec3(0,0,-1)).stream().allMatch(p->mc.level.getBlockState(p).isAir())) {
                brews$smash=true;brews$shot(mc,"morph-smash.png");System.out.println("REMAINING_CLIENT_SMASH_OK");
            }
            if(form>=3 && brews$randomTicks>=20 && !brews$random) {brews$random=true;brews$shot(mc,"morph-random.png");System.out.println("REMAINING_CLIENT_RANDOM_MODEL_OK "+form);}
            boolean up=((MorphState)mc.player).brews$gravityUp();
            if(brews$gravityTicks>=40 && up && mc.player.getY()>75 && !brews$up) {brews$up=true;brews$shot(mc,"gravity-up.png");System.out.println("REMAINING_CLIENT_GRAVITY_FIRST_JUMP_OK");}
            if(brews$up && brews$gravityTicks>=120 && !up && !brews$down) {brews$down=true;brews$shot(mc,"gravity-down.png");System.out.println("REMAINING_CLIENT_GRAVITY_SECOND_JUMP_OK");}
            if(brews$down && mc.player.onGround() && brews$gravityTicks>150 && !brews$landed) {brews$landed=true;System.out.println("REMAINING_CLIENT_GRAVITY_LANDING_OK");}
            if(brews$landed && VersionApi.effectInstance(mc.player,"gravity")==null && form==0 && !mc.player.getAbilities().mayfly && !mc.player.isNoGravity() && !brews$cleared) {
                if(mc.player.getMaxHealth()!=20)throw new IllegalStateException("Brute health bonus survived milk");
                brews$cleared=true;brews$shot(mc,"remaining-milk.png");System.out.println("REMAINING_CLIENT_MILK_AND_MOVEMENT_RESET_OK");
            }
        }
        if(name!=null) {brews$shot(mc,name);System.out.println("FORBIDDEN_BREWS_CAPTURE "+name);}
        if(brews$fxFrame>=560) {
            if(!brews$ore || !brews$gold || !brews$bat || !brews$flight || !brews$brute || !brews$smash || !brews$random || !brews$up || !brews$down || !brews$landed || !brews$cleared)
                throw new IllegalStateException("Incomplete X-ray, morph, smash, gravity or milk verification");
            mc.options.keyJump.setDown(false);mc.options.setCameraType(CameraType.FIRST_PERSON);mc.stop();
        }
    }
}
