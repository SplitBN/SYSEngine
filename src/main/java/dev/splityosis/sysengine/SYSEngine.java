package dev.splityosis.sysengine;

import dev.splityosis.sysengine.configlib.ConfigLib;
import dev.splityosis.sysengine.configlib.manager.ConfigManager;
import dev.splityosis.sysengine.configlib.manager.ConfigOptions;
import dev.splityosis.sysengine.test.TestCommand;
import dev.splityosis.sysengine.test.TestConfig;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;


public final class SYSEngine extends JavaPlugin {

    private ConfigManager configManager = ConfigLib.createConfigManager()
            .setConfigOptions(new ConfigOptions()
                    .setSectionSpacing(1)
                    .setFieldSpacing(0));

    @Override
    public void onEnable() {



    }

    @Override
    public void onDisable() {

        // onTestDisable();
    }


    private TestConfig testConfig;

    private void onTestEnable() {
//        // Modify options (Optional)
//        configManager.setConfigOptions(
//                new ConfigOptions()
//                        .setFieldSpacing(0)
//                        .setSectionSpacing(1)
//        );
//
//        testConfig = new TestConfig();
//        try {
//            configManager.registerConfig(testConfig, new File(getDataFolder(), "test-config.yml"));
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//
//        commandManager.registerCommand(new TestCommand(testConfig, configManager));

    }


    private void onTestDisable() {

    }
}
