package ru.minat0.minetail.core.tasks;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import ru.minat0.minetail.core.MineTail;
import ru.minat0.minetail.core.utils.Logger;

public class SaveMageDaoTask extends BukkitRunnable {

    public SaveMageDaoTask(Plugin plugin) {
        int tenMinutes = 600 * 20;
        runTaskTimerAsynchronously(plugin, 0, tenMinutes);
    }

    @Override
    public void run() {
        Logger.debug("Saving mages data...", false);
        MineTail.getMageDao().updateAll();
    }
}
