package btw.community.mobsenhancement;

import btw.AddonHandler;
import btw.BTWAddon;

public class MobsEnhancementAddon extends BTWAddon {
    private static MobsEnhancementAddon instance;

    private MobsEnhancementAddon() {
        super("Mobs Enhancement", "0.1.0", "Mo");
    }

    @Override
    public void initialize() {
        AddonHandler.logMessage(this.getName() + " Version " + this.getVersionString() + " Initializing...");
    }

    public static MobsEnhancementAddon getInstance() {
        if (instance == null)
            instance = new MobsEnhancementAddon();
        return instance;
    }
}
