package dev.splityosis.sysengine.actions;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A utility class for parsing and serializing action definitions.
 * Supports parsing actions from strings and maps, as well as converting them back to string or map formats.
 */
public class ActionsParser {

    private static final Pattern TYPE_PATTERN = Pattern.compile("^([^\\s{]+)(.*)$");
    private static final Pattern UNESCAPED_BRACES_PATTERN = Pattern.compile("\\{([^}]*)\\}");
    private static final String PLACEHOLDER_OPEN  = "\u0001";
    private static final String PLACEHOLDER_CLOSE = "\u0002";

    /**
     * Parses a raw list of actions from various formats (Strings or Maps) into an {@link Actions} object.
     *
     * @param rawActions List of raw actions, either Strings or Maps.
     * @return An {@link Actions} object containing parsed {@link ActionDefinition}s.
     */
    public static Actions parseActions(List<?> rawActions) {
        if (rawActions == null) {
            return new Actions();
        }

        List<ActionDefinition> result = new ArrayList<>();
        for (Object item : rawActions) {
            if (item instanceof String) {
                ActionDefinition def = parseLine((String) item);
                if (def != null) {
                    result.add(def);
                }
            } else if (item instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> mapItem = (Map<String, Object>) item;
                result.addAll(parseMapAllEntries(mapItem));
            } else {
                System.out.println("Unknown action format: " + item);
            }
        }
        return new Actions(result);
    }

    /**
     * Parses a single-line action definition string.
     *
     * @param line The input string to parse, e.g., "actionType{param1}{param2}".
     * @return A parsed {@link ActionDefinition}, or null if parsing fails.
     */
    private static ActionDefinition parseLine(String line) {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }

        Matcher matcher = TYPE_PATTERN.matcher(line.trim());
        if (!matcher.find()) {
            return null;
        }

        String actionType = matcher.group(1);
        String leftover = matcher.group(2).trim();

        List<String> params = extractParameters(leftover);
        if (params.isEmpty() && !leftover.isEmpty()) {
            params.add(leftover);
        }

        return new ActionDefinition(actionType, params);
    }

    /**
     * Parses all entries of a map into separate {@link ActionDefinition} objects.
     *
     * @param mapItem The map containing action definitions.
     * @return A list of parsed {@link ActionDefinition}s.
     */
    private static List<ActionDefinition> parseMapAllEntries(Map<String, Object> mapItem) {
        if (mapItem.isEmpty()) {
            return Collections.emptyList();
        }

        List<ActionDefinition> results = new ArrayList<>();

        for (Map.Entry<String, Object> entry : mapItem.entrySet()) {
            String actionType = entry.getKey();
            if (actionType == null) continue;

            String leftover = entry.getValue() == null ? "" : entry.getValue().toString().trim();
            List<String> params = extractParameters(leftover);

            if (params.isEmpty() && !leftover.isEmpty()) {
                params.add(leftover);
            }

            results.add(new ActionDefinition(actionType, params));
        }

        return results;
    }

    /**
     * Extracts parameters from unescaped { ... } blocks in a string.
     *
     * @param input The input string to extract parameters from.
     * @return A list of extracted parameters.
     */
    private static List<String> extractParameters(String input) {
        String placeholdered = input
                .replace("\\{", PLACEHOLDER_OPEN)
                .replace("\\}", PLACEHOLDER_CLOSE);

        List<String> params = new ArrayList<>();
        Matcher matcher = UNESCAPED_BRACES_PATTERN.matcher(placeholdered);
        int lastEnd = 0;

        while (matcher.find()) {
            params.add(unescapePlaceholders(matcher.group(1)));
            lastEnd = matcher.end();
        }

        return params;
    }

    /**
     * Reverts placeholder characters to their original brace forms.
     *
     * @param text The text containing placeholders.
     * @return Text with placeholders reverted to braces.
     */
    private static String unescapePlaceholders(String text) {
        return text
                .replace(PLACEHOLDER_OPEN,  "{")
                .replace(PLACEHOLDER_CLOSE, "}");
    }

    /**
     * Converts an {@link ActionDefinition} to a single-line string format.
     *
     * @param def The {@link ActionDefinition} to serialize.
     * @return The serialized string.
     */
    public static String toStringFormat(ActionDefinition def) {
        StringBuilder sb = new StringBuilder(def.getActionType());
        for (String param : def.getParameters()) {
            sb.append("{").append(param).append("}");
        }
        return sb.toString();
    }

    /**
     * Converts an {@link ActionDefinition} to a single-entry map format.
     *
     * @param def The {@link ActionDefinition} to serialize.
     * @return The serialized map.
     */
    public static Map<String, Object> toMapFormat(ActionDefinition def) {
        Map<String, Object> result = new LinkedHashMap<>();
        if (def.getParameters().isEmpty()) {
            result.put(def.getActionType(), "");
        } else {
            StringBuilder leftover = new StringBuilder();
            for (String param : def.getParameters()) {
                leftover.append("{").append(param).append("} ");
            }
            result.put(def.getActionType(), leftover.toString().trim());
        }
        return result;
    }
}
