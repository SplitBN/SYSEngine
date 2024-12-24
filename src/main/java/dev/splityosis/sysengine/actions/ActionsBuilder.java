package dev.splityosis.sysengine.actions;

import com.cryptomorin.xseries.XSound;
import java.util.ArrayList;
import java.util.List;

/**
 * A builder class to fluently create and execute actions for various purposes.
 *
 * Example usage:
 * <pre>
 * {@code
 * Actions actions = new ActionsBuilder()
 *     .sendMessage("Welcome to the server!")
 *     .sendTitle("Welcome", "Enjoy your stay!", 10, 70, 20)
 *     .playSound(XSound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f)
 *     .teleport(100, 65, 200, "world")
 *     .build();
 * actions.execute(target, replacements);
 * }
 * </pre>
 */
public class ActionsBuilder {

    private List<ActionDefinition> actionDefinitionList;

    public ActionsBuilder() {
        actionDefinitionList = new ArrayList<>();
    }

    /**
     * Adds a raw action definition to the builder.
     * @param actionDefinition the action definition to add.
     * @return the current instance of the builder.
     */
    public ActionsBuilder addRawAction(ActionDefinition actionDefinition) {
        actionDefinitionList.add(actionDefinition);
        return this;
    }

    /**
     * Adds a raw action definition using type and parameters.
     * @param actionType the type of action.
     * @param parameters the parameters for the action.
     * @return the current instance of the builder.
     */
    public ActionsBuilder addRawAction(String actionType, String... parameters) {
        addRawAction(new ActionDefinition(actionType, parameters));
        return this;
    }

    /**
     * Sends a message to a single target.
     * @param message the message to send.
     * @return the current instance of the builder.
     */
    public ActionsBuilder sendMessage(String message) {
        addRawAction("sendMessage", message);
        return this;
    }

    /**
     * Sends a message to all players.
     * @param message the message to send.
     * @return the current instance of the builder.
     */
    public ActionsBuilder sendMessageAll(String message) {
        addRawAction("sendMessageAll", message);
        return this;
    }

    /**
     * Executes a command as the console.
     * @param command the command to run.
     * @return the current instance of the builder.
     */
    public ActionsBuilder runConsoleCommand(String command) {
        addRawAction("runConsoleCommand", command);
        return this;
    }

    /**
     * Plays a sound for a single target.
     * @param sound the sound to play.
     * @param volume the volume of the sound.
     * @param pitch the pitch of the sound.
     * @return the current instance of the builder.
     */
    public ActionsBuilder playSound(XSound sound, float volume, float pitch) {
        addRawAction("playSound", sound.name(), String.valueOf(volume), String.valueOf(pitch));
        return this;
    }

    /**
     * Plays a sound for a single target with default pitch.
     * @param sound the sound to play.
     * @param volume the volume of the sound.
     * @return the current instance of the builder.
     */
    public ActionsBuilder playSound(XSound sound, float volume) {
        playSound(sound, volume, 1f);
        return this;
    }

    /**
     * Plays a sound for a single target with default volume and pitch.
     * @param sound the sound to play.
     * @return the current instance of the builder.
     */
    public ActionsBuilder playSound(XSound sound) {
        playSound(sound, 1f, 1f);
        return this;
    }

    /**
     * Plays a sound for all players.
     * @param sound the sound to play.
     * @param volume the volume of the sound.
     * @param pitch the pitch of the sound.
     * @return the current instance of the builder.
     */
    public ActionsBuilder playSoundAll(XSound sound, float volume, float pitch) {
        addRawAction("playSoundAll", sound.name(), String.valueOf(volume), String.valueOf(pitch));
        return this;
    }

    /**
     * Plays a sound for all players with default pitch.
     * @param sound the sound to play.
     * @param volume the volume of the sound.
     * @return the current instance of the builder.
     */
    public ActionsBuilder playSoundAll(XSound sound, float volume) {
        playSoundAll(sound, volume, 1f);
        return this;
    }

    /**
     * Plays a sound for all players with default volume and pitch.
     * @param sound the sound to play.
     * @return the current instance of the builder.
     */
    public ActionsBuilder playSoundAll(XSound sound) {
        playSoundAll(sound, 1f, 1f);
        return this;
    }

    /**
     * Sends an action bar message to a single target.
     * @param message the message to send.
     * @return the current instance of the builder.
     */
    public ActionsBuilder sendActionBar(String message) {
        addRawAction("sendActionBar", message);
        return this;
    }

    /**
     * Sends an action bar message to all players.
     * @param message the message to send.
     * @return the current instance of the builder.
     */
    public ActionsBuilder sendActionBarAll(String message) {
        addRawAction("sendActionBarAll", message);
        return this;
    }

    /**
     * Sends a title to a single target with full parameters.
     * @param title the title text.
     * @param subtitle the subtitle text.
     * @param fadeIn the fade-in duration in ticks.
     * @param stay the duration to display the title in ticks.
     * @param fadeOut the fade-out duration in ticks.
     * @return the current instance of the builder.
     */
    public ActionsBuilder sendTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        addRawAction("sendTitle", title, subtitle, String.valueOf(fadeIn), String.valueOf(stay), String.valueOf(fadeOut));
        return this;
    }

    /**
     * Sends a title to a single target with default timings.
     * @param title the title text.
     * @param subtitle the subtitle text.
     * @return the current instance of the builder.
     */
    public ActionsBuilder sendTitle(String title, String subtitle) {
        sendTitle(title, subtitle, 10, 70, 20);
        return this;
    }

    /**
     * Sends a title to a single target with only a title.
     * @param title the title text.
     * @return the current instance of the builder.
     */
    public ActionsBuilder sendTitle(String title) {
        sendTitle(title, "", 10, 70, 20);
        return this;
    }

    /**
     * Sends a title to all players with full parameters.
     * @param title the title text.
     * @param subtitle the subtitle text.
     * @param fadeIn the fade-in duration in ticks.
     * @param stay the duration to display the title in ticks.
     * @param fadeOut the fade-out duration in ticks.
     * @return the current instance of the builder.
     */
    public ActionsBuilder sendTitleAll(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        addRawAction("sendTitleAll", title, subtitle, String.valueOf(fadeIn), String.valueOf(stay), String.valueOf(fadeOut));
        return this;
    }

    /**
     * Sends a title to all players with default timings.
     * @param title the title text.
     * @param subtitle the subtitle text.
     * @return the current instance of the builder.
     */
    public ActionsBuilder sendTitleAll(String title, String subtitle) {
        sendTitleAll(title, subtitle, 10, 70, 20);
        return this;
    }

    /**
     * Sends a title to all players with only a title.
     * @param title the title text.
     * @return the current instance of the builder.
     */
    public ActionsBuilder sendTitleAll(String title) {
        sendTitleAll(title, "", 10, 70, 20);
        return this;
    }

    /**
     * Makes a single target execute a command.
     * @param command the command to run.
     * @return the current instance of the builder.
     */
    public ActionsBuilder sudo(String command) {
        addRawAction("sudo", command);
        return this;
    }

    /**
     * Teleports a single target to specific coordinates in a world.
     * @param x the X-coordinate.
     * @param y the Y-coordinate.
     * @param z the Z-coordinate.
     * @param world the name of the world.
     * @return the current instance of the builder.
     */
    public ActionsBuilder teleport(double x, double y, double z, String world) {
        addRawAction("teleport", String.valueOf(x), String.valueOf(y), String.valueOf(z), world);
        return this;
    }

    /**
     * Teleports a single target to specific coordinates in the current world.
     * @param x the X-coordinate.
     * @param y the Y-coordinate.
     * @param z the Z-coordinate.
     * @return the current instance of the builder.
     */
    public ActionsBuilder teleport(double x, double y, double z) {
        addRawAction("teleport", String.valueOf(x), String.valueOf(y), String.valueOf(z));
        return this;
    }

    /**
     * Teleports all players to specific coordinates in a world.
     * @param x the X-coordinate.
     * @param y the Y-coordinate.
     * @param z the Z-coordinate.
     * @param world the name of the world.
     * @return the current instance of the builder.
     */
    public ActionsBuilder teleportAll(double x, double y, double z, String world) {
        addRawAction("teleportAll", String.valueOf(x), String.valueOf(y), String.valueOf(z), world);
        return this;
    }

    /**
     * Teleports all players to specific coordinates in the current world.
     * @param x the X-coordinate.
     * @param y the Y-coordinate.
     * @param z the Z-coordinate.
     * @return the current instance of the builder.
     */
    public ActionsBuilder teleportAll(double x, double y, double z) {
        addRawAction("teleportAll", String.valueOf(x), String.valueOf(y), String.valueOf(z));
        return this;
    }

    /**
     * Adds a wait action to delay the next actions.
     * @param ticks the number of ticks to wait.
     * @return the current instance of the builder.
     */
    public ActionsBuilder wait(int ticks) {
        addRawAction("wait", String.valueOf(ticks));
        return this;
    }

    /**
     * Builds the actions into an executable Actions object.
     * @return the built Actions object.
     */
    public Actions build() {
        return new Actions(actionDefinitionList);
    }
}
