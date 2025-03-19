package dev.splityosis.sysengine.common.io;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.Random;

public class DataFile extends ConfigFile{

    private double autoSaveCode = 0;

    public DataFile(File file) {
        super(file);
    }

    @Override
    public void delete() {
        super.delete();
        stopAutoSave();
    }

    public void startAutoSave(JavaPlugin plugin, long interval){
        if (isAutoSave())
            return;

        // Start autosave after a random interval to minimize instances saving at the same time
        Random random = new Random();
        long saveOffset = random.nextInt((int) interval);

        autoSaveCode = Math.random();
        new BukkitRunnable(){
            private final double sessionCode = autoSaveCode;
            @Override
            public void run() {
                if (sessionCode != autoSaveCode){
                    cancel();
                    return;
                }
                save();
            }
        }.runTaskTimerAsynchronously(plugin, saveOffset, interval);
    }

    public void stopAutoSave(){
        autoSaveCode = 0;
    }

    public boolean isAutoSave() {
        return autoSaveCode != 0;
    }
}