package io.github.magersers.forbiddenbrews;

import net.minecraft.core.*;
import net.minecraft.gametest.framework.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.alchemy.*;
import net.minecraft.world.item.enchantment.*;
import net.minecraft.world.effect.*;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.ContainerHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.gametest.*;

@GameTestHolder(ChaosContent.MOD_ID)
@PrefixGameTestTemplate(false)
public final class ChaosStandTests {
    private static final BlockPos POS=new BlockPos(1,1,1);
    private static ChaosBrewingBlockEntity stand(GameTestHelper h) {
        h.setBlock(POS,ForbiddenBrews.STAND.get());return (ChaosBrewingBlockEntity)h.getBlockEntity(POS);
    }
    private static void fill(ChaosBrewingBlockEntity be,ChaosRecipes.Recipe r) {
        be.clearContent();be.progress=0;be.recipeIndex=-1;be.selectedRecipe=-1;be.fuel=0;
        be.setItem(0,r.base());be.setItem(1,new ItemStack(ForbiddenBrews.WART_ITEM.get()));
        for(int i=0;i<r.components().size();i++) {
            var c=r.components().get(i);be.setItem(i+2,new ItemStack(c.item(),c.count()));
        }
        be.setItem(6,new ItemStack(Items.BLAZE_POWDER));
    }
    private static void tick(GameTestHelper h,ChaosBrewingBlockEntity be,int count) {
        var ticker=ForbiddenBrews.STAND.get().getTicker(h.getLevel(),be.getBlockState(),ForbiddenBrews.STAND_TYPE.get());
        h.assertTrue(ticker!=null,"Registered server ticker required");
        for(int i=0;i<count;i++)ticker.tick(h.getLevel(),be.getBlockPos(),be.getBlockState(),be);
    }
    @GameTest(template="empty",timeoutTicks=40)
    public static void craftingRequiresNetheriteAndScrap(GameTestHelper h) {
        var grid=new TransientCraftingContainer(new CraftingMenu(0,h.makeMockPlayer().getInventory()),3,3);
        Item[] parts={Items.AMETHYST_SHARD,Items.BREWING_STAND,Items.AMETHYST_SHARD,Items.AIR,Items.NETHERITE_INGOT,Items.AIR,Items.OBSIDIAN,Items.BLAZE_ROD,Items.OBSIDIAN};
        for(int i=0;i<9;i++)grid.setItem(i,new ItemStack(parts[i]));
        var recipe=h.getLevel().getRecipeManager().getRecipeFor(RecipeType.CRAFTING,grid,h.getLevel());
        h.assertTrue(recipe.isPresent() && recipe.get().assemble(grid,h.getLevel().registryAccess()).is(ForbiddenBrews.STAND_ITEM.get()),"Stand recipe");
        grid.setItem(4,new ItemStack(Items.IRON_INGOT));h.assertFalse(recipe.get().matches(grid,h.getLevel()),"Iron cannot replace netherite");
        grid.clearContent();grid.setItem(0,new ItemStack(Items.NETHER_WART));grid.setItem(4,new ItemStack(Items.NETHERITE_SCRAP));
        var wart=h.getLevel().getRecipeManager().getRecipeFor(RecipeType.CRAFTING,grid,h.getLevel());
        h.assertTrue(wart.isPresent() && wart.get().assemble(grid,h.getLevel().registryAccess()).is(ForbiddenBrews.WART_ITEM.get()),"Wart crafting");
        grid.setItem(4,ItemStack.EMPTY);h.assertFalse(wart.get().matches(grid,h.getLevel()),"Scrap required");h.succeed();
    }
    @GameTest(template="empty",timeoutTicks=40)
    public static void allRecipesConsumeWartAndExactIngredients(GameTestHelper h) {
        var be=stand(h);h.assertTrue(ChaosRecipes.ALL.size()==14,"Fourteen recipes");
        for(var r:ChaosRecipes.ALL) {
            fill(be,r);tick(h,be,r.ticks());
            h.assertTrue(be.getItem(7).is(r.output().getItem()),"Output "+r.id());
            for(int i=0;i<7;i++)h.assertTrue(be.getItem(i).isEmpty(),"Consumption "+r.id()+" slot "+i);
            h.assertTrue(be.fuel==19,"One charge used");
            h.assertTrue(PotionUtils.getMobEffects(be.getItem(7)).get(0).getAmplifier()==r.result().level()-1,"Correct effect level");
            int duration=r.result().family().equals("homeward")?1:switch(r.result().level()) { case 1 -> 2400; case 2 -> 6000; default -> 9600; };
            h.assertTrue(PotionUtils.getMobEffects(be.getItem(7)).get(0).getDuration()==duration,"Duration 2/5/8 minutes for "+r.id());
        }h.succeed();
    }
    @GameTest(template="empty",timeoutTicks=40)
    public static void missingWartWrongIngredientsAndBlockedOutput(GameTestHelper h) {
        var be=stand(h);var r=ChaosRecipes.ALL.get(0);fill(be,r);
        be.setItem(1,ItemStack.EMPTY);tick(h,be,240);
        h.assertTrue(be.progress==0 && be.getItem(6).getCount()==1,"No wart must not consume fuel");
        be.setItem(1,new ItemStack(ForbiddenBrews.WART_ITEM.get()));be.setItem(5,new ItemStack(Items.DIAMOND));tick(h,be,240);
        h.assertTrue(be.getItem(7).isEmpty() && be.progress==0,"Extra material invalidates recipe");
        be.setItem(5,ItemStack.EMPTY);be.setItem(7,r.output());tick(h,be,240);
        h.assertTrue(be.getItem(0).getCount()==1 && be.getItem(1).getCount()==1,"Blocked output preserves inputs");h.succeed();
    }
    @GameTest(template="empty",timeoutTicks=40)
    public static void changingIngredientsResetsProgress(GameTestHelper h) {
        var be=stand(h);var r=ChaosRecipes.ALL.get(0);fill(be,r);tick(h,be,100);
        h.assertTrue(be.progress==100,"Progress before interruption");
        be.setItem(2,ItemStack.EMPTY);tick(h,be,1);h.assertTrue(be.progress==0,"Invalid inputs reset");
        be.setItem(2,new ItemStack(Items.RABBIT_FOOT));tick(h,be,100);h.assertTrue(be.getItem(7).isEmpty(),"Fresh full duration required");
        tick(h,be,100);h.assertTrue(!be.getItem(7).isEmpty(),"Restart completes");h.succeed();
    }
    @GameTest(template="empty",timeoutTicks=40)
    public static void serializationResumesAndOldInventoryIsPreserved(GameTestHelper h) {
        var be=stand(h);fill(be,ChaosRecipes.ALL.get(0));tick(h,be,80);
        var tag=be.saveWithFullMetadata();var restored=(ChaosBrewingBlockEntity)BlockEntity.loadStatic(be.getBlockPos(),be.getBlockState(),tag);
        h.assertTrue(restored!=null && restored.progress==80 && restored.fuel==20 && restored.getItem(1).getCount()==1,"Saved progress and ingredients");
        restored.setLevel(h.getLevel());tick(h,restored,120);h.assertTrue(!restored.getItem(7).isEmpty(),"Resume after reload");
        var old=NonNullList.withSize(5,ItemStack.EMPTY);for(int i=0;i<3;i++)old.set(i,VersionApi.water());
        old.set(3,new ItemStack(Items.NETHER_WART,5));old.set(4,new ItemStack(Items.BLAZE_POWDER,3));
        var legacy=new net.minecraft.nbt.CompoundTag();ContainerHelper.saveAllItems(legacy,old);be.load(legacy);
        int count=0;for(int i=0;i<8;i++)count+=be.getItem(i).getCount();h.assertTrue(count==11,"Migration preserves all old items");
        var player=h.makeMockPlayer();player.setPos(be.getBlockPos().getX()+.5,be.getBlockPos().getY()+.5,be.getBlockPos().getZ()+.5);
        var menu=be.createMenu(0,player.getInventory(),player);h.assertTrue(menu instanceof ChaosMenu && menu.stillValid(player),"Custom menu opens");h.succeed();
    }
    @GameTest(template="empty",timeoutTicks=40)
    public static void fortuneAndLootingApplyAndStackWithEnchantments(GameTestHelper h) {
        var player=h.makeMockPlayer();ChaosContent.brew(new BrewSpec("fortune",3,false)).finishUsingItem(h.getLevel(),player);
        h.assertTrue(player.getEffect(ForbiddenBrews.FORTUNE.get()).getAmplifier()==2,"Fortune III effect");
        h.assertTrue(player.getAttributeValue(net.minecraft.world.entity.ai.attributes.Attributes.LUCK)==0,"Vanilla Luck must remain unchanged");
        var sword=new ItemStack(Items.DIAMOND_SWORD);sword.enchant(Enchantments.MOB_LOOTING,2);player.setItemSlot(EquipmentSlot.MAINHAND,sword);
        ChaosContent.brew(new BrewSpec("looting",2,false)).finishUsingItem(h.getLevel(),player);
        h.assertTrue(EnchantmentHelper.getMobLooting(player)==4,"Potion and sword stack");
        player.removeEffect(ForbiddenBrews.LOOTING.get());h.assertTrue(EnchantmentHelper.getMobLooting(player)==2,"Bonus disappears");h.succeed();
    }
    @GameTest(template="empty",timeoutTicks=40)
    public static void fortuneUsesRealBlockLootAndPreservesTools(GameTestHelper h) {
        var player=h.makeMockPlayer();var pos=h.absolutePos(POS);
        var pick=new ItemStack(Items.DIAMOND_PICKAXE);pick.enchant(Enchantments.BLOCK_FORTUNE,3);
        ChaosContent.brew(new BrewSpec("fortune",3,false)).finishUsingItem(h.getLevel(),player);
        h.assertTrue(EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_FORTUNE,VersionApi.fortuneTool(pick,player))==6,"Fortune III + III = VI");
        boolean exceedsFour=false;
        for(int i=0;i<128;i++) {
            var drops=Block.getDrops(Blocks.DIAMOND_ORE.defaultBlockState(),h.getLevel(),pos,null,player,pick);
            int count=drops.stream().filter(s->s.is(Items.DIAMOND)).mapToInt(ItemStack::getCount).sum();
            if(count>4)exceedsFour=true;
        }
        h.assertTrue(exceedsFour,"Actual diamond loot must exceed the Fortune III limit");
        h.assertTrue(EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_FORTUNE,pick)==3,"Original pickaxe is unchanged");
        var axe=new ItemStack(Items.DIAMOND_AXE);axe.enchant(Enchantments.BLOCK_FORTUNE,3);
        boolean exceedsSeven=false;var crop=ForbiddenBrews.WART.get().defaultBlockState().setValue(NetherWartBlock.AGE,3);
        for(int i=0;i<256;i++) {
            int count=Block.getDrops(crop,h.getLevel(),pos,null,player,axe).stream().filter(s->s.is(ForbiddenBrews.WART_ITEM.get())).mapToInt(ItemStack::getCount).sum();
            if(count>7)exceedsSeven=true;
        }
        h.assertTrue(exceedsSeven,"Axe Fortune and potion stack in crop loot too");
        h.assertTrue(EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_FORTUNE,axe)==3,"Original axe is unchanged");
        var silk=new ItemStack(Items.DIAMOND_PICKAXE);silk.enchant(Enchantments.SILK_TOUCH,1);
        var silkDrops=Block.getDrops(Blocks.DIAMOND_ORE.defaultBlockState(),h.getLevel(),pos,null,player,silk);
        h.assertTrue(silkDrops.size()==1 && silkDrops.get(0).is(Items.DIAMOND_ORE),"Silk Touch keeps vanilla priority");
        player.removeEffect(ForbiddenBrews.FORTUNE.get());
        for(int i=0;i<64;i++) {
            int count=Block.getDrops(Blocks.DIAMOND_ORE.defaultBlockState(),h.getLevel(),pos,null,player,pick).stream().mapToInt(ItemStack::getCount).sum();
            h.assertTrue(count<=4,"After effect ends only tool Fortune remains");
        }
        h.succeed();
    }
    @GameTest(template="empty",timeoutTicks=40)
    public static void recipePickerFiltersBaseAndServerValidatesSelection(GameTestHelper h) {
        var initial=ChaosRecipes.available(ItemStack.EMPTY);
        h.assertTrue(initial.size()==3 && initial.stream().allMatch(r->r.source()==null),"Only three base potions initially");
        var options=ChaosRecipes.available(ChaosContent.brew(new BrewSpec("fortune",1,false)));
        h.assertTrue(options.size()==2 && options.stream().anyMatch(r->r.result().equals(new BrewSpec("fortune",2,false))) &&
            options.stream().anyMatch(r->r.result().equals(new BrewSpec("fortune",1,true))),"Fortune I unlocks II and its splash");
        h.assertTrue(ChaosRecipes.available(ChaosContent.brew(new BrewSpec("looting",3,false))).size()==1,"Level III only converts to splash");
        h.assertTrue(ChaosRecipes.available(ChaosContent.brew(new BrewSpec("fortune",1,true))).isEmpty(),"Splash is not an upgrade base");
        var be=stand(h);fill(be,ChaosRecipes.ALL.get(0));var player=h.makeMockPlayer();
        player.setPos(be.getBlockPos().getX()+.5,be.getBlockPos().getY()+.5,be.getBlockPos().getZ()+.5);
        var menu=(ChaosMenu)be.createMenu(0,player.getInventory(),player);
        h.assertFalse(menu.clickMenuButton(player,1),"Server rejects locked Fortune II for water");
        h.assertFalse(menu.clickMenuButton(player,999),"Invalid selection rejected");
        h.assertTrue(menu.clickMenuButton(player,3),"Looting I available for water");tick(h,be,200);
        h.assertTrue(be.getItem(7).isEmpty() && be.getItem(6).getCount()==1,"Selected recipe prevents brewing a different recipe");
        var saved=be.saveWithFullMetadata();var restored=(ChaosBrewingBlockEntity)BlockEntity.loadStatic(be.getBlockPos(),be.getBlockState(),saved);
        h.assertTrue(restored.selectedRecipe==3,"Selection persists on reload");
        h.assertTrue(menu.clickMenuButton(player,0),"Choose Fortune I");tick(h,be,200);
        h.assertTrue(be.getItem(7).is(ChaosContent.brew(new BrewSpec("fortune",1,false)).getItem()),"Selected Fortune I brews");
        be.setItem(0,ChaosContent.brew(new BrewSpec("fortune",1,false)));be.setItem(7,ItemStack.EMPTY);
        h.assertTrue(menu.clickMenuButton(player,1),"Putting Fortune I in the middle unlocks Fortune II");
        be.setItem(0,VersionApi.water());tick(h,be,1);h.assertTrue(be.selectedRecipe==-1,"Changing base clears incompatible selection");
        h.succeed();
    }
    @GameTest(template="empty",timeoutTicks=40)
    public static void homewardReturnsToSafeSpawn(GameTestHelper h) {
        for(int x=0;x<4;x++)for(int z=0;z<4;z++)h.setBlock(new BlockPos(x,0,z),Blocks.STONE);
        var player=new net.minecraft.server.level.ServerPlayer(h.getLevel().getServer(),h.getLevel(),
            new com.mojang.authlib.GameProfile(java.util.UUID.randomUUID(),"brews-test"));
        var home=h.absolutePos(POS);
        // Forge's teleport hook inspects the connection pipeline. Give the
        // GameTest mock an in-memory channel, without opening a network socket.
        var channel=new io.netty.channel.embedded.EmbeddedChannel();
        var connection=new net.minecraft.network.Connection(net.minecraft.network.protocol.PacketFlow.SERVERBOUND) {
            @Override public io.netty.channel.Channel channel() { return channel; }
            @Override public void send(net.minecraft.network.protocol.Packet<?> packet) {}
            @Override public void send(net.minecraft.network.protocol.Packet<?> packet,net.minecraft.network.PacketSendListener listener) {}
        };
        player.connection=new net.minecraft.server.network.ServerGamePacketListenerImpl(player.getServer(),connection,player);
        player.setRespawnPosition(h.getLevel().dimension(),home,0,true,false);player.setPos(home.getX()+20,home.getY()+8,home.getZ()+20);
        ChaosContent.brew(new BrewSpec("homeward",1,false)).finishUsingItem(h.getLevel(),player);
        h.assertTrue(player.distanceToSqr(home.getX()+.5,home.getY(),home.getZ()+.5)<2,"Return to safe forced home");h.assertTrue(player.fallDistance==0,"Reset fall damage");
        var bed=Blocks.RED_BED.defaultBlockState().setValue(BedBlock.FACING,Direction.SOUTH);
        h.setBlock(POS.north(),bed.setValue(BedBlock.PART,net.minecraft.world.level.block.state.properties.BedPart.FOOT));
        h.setBlock(POS,bed.setValue(BedBlock.PART,net.minecraft.world.level.block.state.properties.BedPart.HEAD));
        player.setRespawnPosition(h.getLevel().dimension(),home,0,false,false);player.setPos(home.getX()+20,home.getY()+8,home.getZ()+20);
        ChaosContent.brew(new BrewSpec("homeward",1,false)).finishUsingItem(h.getLevel(),player);
        h.assertTrue(player.distanceToSqr(home.getX()+.5,home.getY(),home.getZ()+.5)<16,"Return beside actual bed");
        channel.finishAndReleaseAll();h.succeed();
    }
    @GameTest(template="empty",timeoutTicks=40)
    public static void cropGrowsOnlyOnSoulSandAndMatureHarvestMultiplies(GameTestHelper h) {
        var pos=h.absolutePos(POS);var crop=ForbiddenBrews.WART.get();
        h.setBlock(POS.below(),Blocks.SOUL_SAND);h.assertTrue(crop.defaultBlockState().canSurvive(h.getLevel(),pos),"Soul sand supports crop");
        h.setBlock(POS.below(),Blocks.SOUL_SOIL);h.assertFalse(crop.defaultBlockState().canSurvive(h.getLevel(),pos),"Soul soil rejected");
        h.setBlock(POS.below(),Blocks.SOUL_SAND);h.setBlock(POS,crop);var random=RandomSource.create(123);
        for(int i=0;i<300;i++)crop.randomTick(h.getLevel().getBlockState(pos),h.getLevel(),pos,random);
        h.assertTrue(h.getLevel().getBlockState(pos).getValue(NetherWartBlock.AGE)==3,"Crop matures");
        h.getLevel().destroyBlock(pos,true);
        int count=h.getLevel().getEntitiesOfClass(ItemEntity.class,new AABB(pos).inflate(2)).stream().filter(e->e.getItem().is(ForbiddenBrews.WART_ITEM.get())).mapToInt(e->e.getItem().getCount()).sum();
        h.assertTrue(count>=2 && count<=4,"Mature crop drops 2-4 wart: "+count);h.succeed();
    }
    @GameTest(template="empty",timeoutTicks=40)
    public static void splashUseConsumesOneAndImpactAppliesEffect(GameTestHelper h) {
        var player=h.makeMockPlayer();var pos=h.absolutePos(POS);player.setPos(pos.getX(),pos.getY(),pos.getZ());
        var spec=new BrewSpec("looting",3,true);
        player.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,ChaosContent.brew(spec));
        player.getMainHandItem().use(h.getLevel(),player,net.minecraft.world.InteractionHand.MAIN_HAND);
        h.assertTrue(player.getMainHandItem().isEmpty(),"Splash consumes item in survival");
        var thrown=h.getLevel().getEntitiesOfClass(net.minecraft.world.entity.projectile.ThrownPotion.class,new AABB(pos).inflate(4));
        h.assertTrue(thrown.size()==1,"Use creates vanilla projectile");
        var target=h.spawn(net.minecraft.world.entity.EntityType.PIG,POS);
        var impact=new net.minecraft.world.entity.projectile.ThrownPotion(h.getLevel(),pos.getX(),pos.getY(),pos.getZ()) {
            public void hit(net.minecraft.world.entity.Entity entity) { super.onHit(new net.minecraft.world.phys.EntityHitResult(entity)); }
        };
        impact.setItem(thrown.get(0).getItem());impact.hit(target);
        h.assertTrue(target.hasEffect(ForbiddenBrews.LOOTING.get()) && target.getEffect(ForbiddenBrews.LOOTING.get()).getAmplifier()==2,"Impact applies Looting III");
        h.succeed();
    }
}
