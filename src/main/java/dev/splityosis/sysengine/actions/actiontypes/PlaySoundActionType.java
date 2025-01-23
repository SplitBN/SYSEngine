package dev.splityosis.sysengine.actions.actiontypes;

import com.cryptomorin.xseries.XSound;
import dev.splityosis.sysengine.actions.ActionType;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class PlaySoundActionType implements ActionType {

    @Override
    public String getName() {
        return "playSound";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("sound");
    }

    @Override
    public String getDescription() {
        return "Plays a sound to a target player";
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
        if (!(target instanceof Player)) return;
        Player player = (Player) target;

        params = applyPlaceholders(player, params, replacements);
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
        sound.play(player, volume, pitch);
    }
}