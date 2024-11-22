package dev.splityosis.sysengine.utils;

import org.bukkit.Bukkit;

/**
 * Utility class for handling and comparing Minecraft server versions.
 * <p>
 * This class retrieves the current server version from the Bukkit API and parses it into
 * major, minor, and patch components for easy comparison. It provides methods for
 * comparing the current server version with a specified version string, such as
 * checking if the server is before, after, at most, or at least a particular version.
 * <p>
 * The version is represented in "major.minor.patch" format, where:
 * - Major: The primary version number (e.g., 1 in "1.20.2")
 * - Minor: The secondary version number (e.g., 20 in "1.20.2")
 * - Patch: The patch version number (e.g., 2 in "1.20.2")
 * <p>
 * Example usage:
 * <pre>
 *     if (VersionUtil.isServerAtLeast("1.20.2")) {
 *         // Code for servers 1.20.2 and above
 *     }
 * </pre>
 *
 * This class is designed to be used statically and cannot be instantiated.
 */
public class VersionUtil {

    private static final int major;
    private static final int minor;
    private static final int patch;

    static {
        String version = Bukkit.getVersion();
        version = version.substring(version.indexOf(':') + 2, version.lastIndexOf(')'));

        int[] parts = parseVersion(version);
        major = parts[0];
        minor = parts[1];
        patch = parts[2];
    }

    private VersionUtil() {}

    /**
     * Converts a version string to an array of integers representing major, minor, and patch versions.
     * <p>
     * For example, "1.20.2" would return {1, 20, 2}.
     *
     * @param version the version string in "major.minor.patch" format
     * @return an array of integers {major, minor, patch}
     */
    public static int[] parseVersion(String version) {
        String[] parts = version.split("\\.");

        int major = parts.length > 0 ? Integer.parseInt(parts[0]) : 0;
        int minor = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
        int patch = parts.length > 2 ? Integer.parseInt(parts[2]) : 0;

        return new int[] {major, minor, patch};
    }

    /**
     * Returns the current Minecraft version as a string in "major.minor.patch" format.
     *
     * @return the current Minecraft version
     */
    public static String getMinecraftVersion() {
        return major + "." + minor + "." + patch;
    }

    /**
     * Checks if the current Minecraft version is strictly before (less than) the specified version.
     *
     * @param version the version to compare against, as a string (e.g., "1.20.2")
     * @return {@code true} if the current version is before the specified version, {@code false} otherwise
     */
    public static boolean isServerBefore(String version) {
        return compareVersion(version) < 0;
    }

    /**
     * Checks if the current Minecraft version is at most (less than or equal to) the specified version.
     *
     * @param version the version to compare against, as a string (e.g., "1.20.2")
     * @return {@code true} if the current version is at most the specified version, {@code false} otherwise
     */
    public static boolean isServerAtMost(String version) {
        return compareVersion(version) <= 0;
    }

    /**
     * Checks if the current Minecraft version is strictly after (greater than) the specified version.
     *
     * @param version the version to compare against, as a string (e.g., "1.20.2")
     * @return {@code true} if the current version is after the specified version, {@code false} otherwise
     */
    public static boolean isServerAfter(String version) {
        return compareVersion(version) > 0;
    }

    /**
     * Checks if the current Minecraft version is at least (greater than or equal to) the specified version.
     *
     * @param version the version to compare against, as a string (e.g., "1.20.2")
     * @return {@code true} if the current version is at least the specified version, {@code false} otherwise
     */
    public static boolean isServerAtLeast(String version) {
        return compareVersion(version) >= 0;
    }

    /**
     * Gets the major version.
     */
    public static int getMajor() {
        return major;
    }

    /**
     * Gets the minor version.
     */
    public static int getMinor() {
        return minor;
    }

    /**
     * Gets the patch version.
     */
    public static int getPatch() {
        return patch;
    }

    /**
     * Compares the current Minecraft version with the specified version string.
     * Returns a positive integer if the current version is greater, a negative integer if it is less,
     * and 0 if they are equal.
     *
     * @param version the version to compare against, as a string (e.g., "1.20.2")
     * @return a positive integer if the current version is greater, a negative integer if less, and 0 if equal
     */
    public static int compareVersion(String version) {
        int[] otherParts = parseVersion(version);
        if (major != otherParts[0]) return Integer.compare(major, otherParts[0]);
        if (minor != otherParts[1]) return Integer.compare(minor, otherParts[1]);
        return Integer.compare(patch, otherParts[2]);
    }
}