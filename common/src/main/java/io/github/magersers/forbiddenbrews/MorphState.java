package io.github.magersers.forbiddenbrews;

import net.minecraft.nbt.CompoundTag;

/** Synced form/direction and saved temporary movement ownership. */
public interface MorphState {
    int brews$form();
    void brews$form(int form);
    boolean brews$gravityUp();
    void brews$gravityUp(boolean up);
    Data brews$data();
    final class Data {
        public int randomForm,previousDuration=-1,lastForm;
        public long lastToggle=Long.MIN_VALUE,lastSmash=Long.MIN_VALUE;
        public boolean flightOwned,mayfly,flying,creative,spectator,safeLanding,blocked,reroll;
        public float flyingSpeed;
        public void save(CompoundTag tag) {
            var n=new CompoundTag();n.putInt("RandomForm",randomForm);n.putInt("Duration",previousDuration);
            var type=Morphs.type(randomForm);
            if(randomForm>=3 && type!=null)n.putString("RandomType",net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.getKey(type).toString());
            n.putBoolean("FlightOwned",flightOwned);n.putBoolean("Mayfly",mayfly);n.putBoolean("Flying",flying);
            n.putBoolean("Creative",creative);n.putBoolean("Spectator",spectator);n.putFloat("FlySpeed",flyingSpeed);
            n.putBoolean("SafeLanding",safeLanding);
            tag.put("ForbiddenBrewsMorph",n);
        }
        public void load(CompoundTag tag) {
            var n=tag.getCompound("ForbiddenBrewsMorph");randomForm=n.getInt("RandomForm");previousDuration=n.getInt("Duration");
            randomForm=n.contains("RandomType")?Morphs.formOf(net.minecraft.world.entity.EntityType.byString(n.getString("RandomType")).orElse(null)):Morphs.legacyForm(randomForm);
            flightOwned=n.getBoolean("FlightOwned");mayfly=n.getBoolean("Mayfly");flying=n.getBoolean("Flying");
            creative=n.getBoolean("Creative");spectator=n.getBoolean("Spectator");flyingSpeed=n.getFloat("FlySpeed");
            safeLanding=n.getBoolean("SafeLanding");
        }
    }
}
