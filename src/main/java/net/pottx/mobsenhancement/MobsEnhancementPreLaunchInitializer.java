package net.pottx.mobsenhancement;

import btw.community.mobsenhancement.MobsEnhancementAddon;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

public class MobsEnhancementPreLaunchInitializer implements PreLaunchEntrypoint {
    /**
     * Runs the PreLaunch entrypoint to register BTW-Addon.
     * Don't initialize anything else here, use
     * the method Initialize() in the Addon.
     */
    @Override
    public void onPreLaunch() {
        MobsEnhancementAddon.getInstance();
    }
}
