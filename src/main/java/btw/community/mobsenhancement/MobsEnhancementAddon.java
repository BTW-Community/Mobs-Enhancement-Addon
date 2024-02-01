package btw.community.mobsenhancement;

import btw.AddonHandler;
import btw.BTWAddon;
import net.minecraft.server.MinecraftServer;
import net.pottx.mobsenhancement.MEAEffectManager;

public class MobsEnhancementAddon extends BTWAddon {
    private static MobsEnhancementAddon instance;

    private MobsEnhancementAddon() {
        super("Mobs Enhancement", "0.2.0", "MEA");
    }

    @Override
    public void initialize() {
        AddonHandler.logMessage(this.getName() + " Version " + this.getVersionString() + " Initializing...");

        if (!MinecraftServer.getIsServer()) {
            MEAEffectManager.initEffects();
        }
    }

    public static MobsEnhancementAddon getInstance() {
        if (instance == null)
            instance = new MobsEnhancementAddon();
        return instance;
    }
}
