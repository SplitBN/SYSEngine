package dev.splityosis.sysengine.actions.actiontypes;

import com.cryptomorin.xseries.XSound;
import dev.splityosis.sysengine.actions.ActionType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class PlaySoundAllActionType implements ActionType {

    @Override
    public String getName() {
        return "playSoundAll";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("soundAll");
    }

    @Override
    public String getDescription() {
        return "Plays a sound to all players";
    }

    @Override
    public List<String> getParameters() {
        return Arrays.asList("sound");
    }

    @Override
    public List<String> getOptionalParameters() {
        return Arrays.asList("volume", "pitch");
    }

    @Override
    public void execute(Object target, @NotNull List<String> params, @NotNull Map<String, String> replacements) {
        params = applyPlaceholders(null, params, replacements);
        XSound sound = XSound.matchXSound(params.get(0).toUpperCase()).orElse(null);
        if (sound == null) {
            Bukkit.getLogger().warning("Invalid sound: " + params.get(0));
            return;
        }
        if (!sound.isSupported()) {
            Bukkit.getLogger().warning("Unsupported sound: " + params.get(0));
            return;
        }
        float volume = params.size() > 1 ? Float.parseFloat(params.get(1)) : 1.0f;
        float pitch = params.size() > 2 ? Float.parseFloat(params.get(2)) : 1.0f;

        for (Player player : Bukkit.getOnlinePlayers())
            sound.play(player, volume, pitch);
    }
}