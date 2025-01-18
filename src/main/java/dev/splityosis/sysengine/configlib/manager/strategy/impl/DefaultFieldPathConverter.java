package dev.splityosis.sysengine.configlib.manager.strategy.impl;

import dev.splityosis.sysengine.configlib.manager.strategy.FieldPathConverter;

import java.util.regex.Pattern;

public class DefaultFieldPathConverter implements FieldPathConverter {
    @Override
    public String convertToPathFormat(String fieldName) {
        if (fieldName.length() == 1) return fieldName;

        String[] split = fieldName.split(Pattern.quote("_"));
        if (split.length > 1) {
            StringBuilder finalBuilder = new StringBuilder();

            for (String s : split) {
                if (s.isBlank()) continue;
                finalBuilder.append(convertToPathFormat(s));
                finalBuilder.append(".");
            }

            finalBuilder.deleteCharAt(finalBuilder.length()-1);
            return finalBuilder.toString();
        }
        StringBuilder pathFormat = new StringBuilder();

        boolean dashed = true;

        for (int i = 0; i < fieldName.length(); i++) {
            char currentChar = fieldName.charAt(i);
            char nextChar = (i + 1 < fieldName.length()) ? fieldName.charAt(i + 1) : '\0';

            boolean appendDash = false;

            if (nextChar == '\0') {
                pathFormat.append(currentChar);
                return pathFormat.toString();
            }

            // ...aa... -> ...a...
            else if (Character.isLowerCase(currentChar) && Character.isLowerCase(nextChar)) {
                pathFormat.append(currentChar);
            }

            // ...11... -> ...1...
            else if (Character.isDigit(currentChar) && Character.isDigit(nextChar)) {
                pathFormat.append(currentChar);
            }

            // ...AA... -> ...A...
            else if (Character.isUpperCase(currentChar) && Character.isUpperCase(nextChar)) {
                pathFormat.append(currentChar);
            }

            // ...Ab... -> ...a...
            else if (Character.isUpperCase(currentChar) && Character.isLowerCase(nextChar)) {
                if (!dashed)
                    pathFormat.append("-");
                pathFormat.append(Character.toLowerCase(currentChar));
            }

            // ...aB... -> ...a-...
            else if (Character.isLowerCase(currentChar) && Character.isUpperCase(nextChar)) {
                pathFormat.append(currentChar);
                appendDash = true;
            }

            // ...1b... -> ...1-... || ...b1... -> ...b-...
            else if (Character.isDigit(currentChar) != Character.isDigit(nextChar) && nextChar != '_') {
                pathFormat.append(currentChar);
                appendDash = true;
            }

            else pathFormat.append(currentChar);

            if (currentChar == '_') dashed = true;

            if (appendDash && !dashed) {
                pathFormat.append("-");
                dashed = true;
            }
            else
                dashed = false;
        }

        return pathFormat.toString();
    }
}
