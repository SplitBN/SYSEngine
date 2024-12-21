package dev.splityosis.sysengine.configlib.configuration;

import java.lang.reflect.Field;
import java.util.*;

public class ConfigProfile {

    private Map<String, List<String>> comments;
    private Map<String, List<String>> inlineComments;
    private Map<String, MapperClassValue> config;
    private Configuration configuration;

    public ConfigProfile(Map<String, List<String>> comments, Map<String, List<String>> inlineComments, Map<String, MapperClassValue> config) {
        this.comments = comments;
        this.inlineComments = inlineComments;
        this.config = config;
    }

    public ConfigProfile(Map<String, List<String>> comments, Map<String, List<String>> inlineComments, Map<String, MapperClassValue> config, Configuration configuration) {
        this.comments = comments;
        this.inlineComments = inlineComments;
        this.config = config;
        this.configuration = configuration;
    }

    public Map<String, List<String>> getComments() {
        return comments;
    }

    public Map<String, MapperClassValue> getConfig() {
        return config;
    }

    public Map<String, List<String>> getInlineComments() {
        return inlineComments;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    // Method to generate YMLProfile from object fields
    public static ConfigProfile readConfigObject(Object object, int sectionSpacing, int fieldSpacing) throws IllegalAccessException {
        Map<String, MapperClassValue> config = new LinkedHashMap<>();
        Map<String, List<String>> comments = new LinkedHashMap<>();
        Map<String, List<String>> inlineComments = new LinkedHashMap<>();
        Map<String, String> mappers = new LinkedHashMap<>();

        String currentSectionPath = "";

        for (Field declaredField : object.getClass().getDeclaredFields()) {
            declaredField.setAccessible(true);

            Configuration.Field fieldAnnotation = declaredField.getAnnotation(Configuration.Field.class);
            if (fieldAnnotation != null) {
                // It is a field that needs to be handled

                Configuration.Section sectionAnnotation = declaredField.getAnnotation(Configuration.Section.class);
                if (sectionAnnotation != null) {
                    // Handle section stuff
                    currentSectionPath = sectionAnnotation.value().trim();

                    Configuration.SectionComment sectionCommentAnnotation = declaredField.getAnnotation(Configuration.SectionComment.class);
                    List<String> sectionComments = getSpaceList(sectionSpacing);
                    if (sectionCommentAnnotation != null) {
                        // Handle section comment
                        processComment(sectionCommentAnnotation.value(), sectionComments);
                    }
                    comments.put(currentSectionPath, sectionComments);

                    Configuration.SectionInlineComment sectionInlineCommentAnnotation = declaredField.getAnnotation(Configuration.SectionInlineComment.class);
                    if (sectionInlineCommentAnnotation != null) {
                        // Handle section inline comment
                        inlineComments.put(currentSectionPath, processComment(sectionInlineCommentAnnotation.value(), new ArrayList<>()));
                    }
                }

                String absolutePath = currentSectionPath + (!currentSectionPath.isBlank() ? '.' : "") + getFieldPath(declaredField, fieldAnnotation);

                Configuration.FieldComment fieldCommentAnnotation = declaredField.getAnnotation(Configuration.FieldComment.class);
                List<String> fieldComments = getSpaceList(fieldSpacing);
                if (fieldCommentAnnotation != null) {
                    // Handle field comment
                    processComment(fieldCommentAnnotation.value(), fieldComments);
                }
                comments.put(absolutePath, fieldComments);

                Configuration.FieldInlineComment fieldInlineCommentAnnotation = declaredField.getAnnotation(Configuration.FieldInlineComment.class);
                if (fieldInlineCommentAnnotation != null) {
                    inlineComments.put(absolutePath, processComment(fieldInlineCommentAnnotation.value(), new ArrayList<>()));
                }

                Configuration.Mapper mapperAnnotation = declaredField.getAnnotation(Configuration.Mapper.class);
                String mapper = "";
                if (mapperAnnotation != null)
                    mapper = mapperAnnotation.value();

                config.put(absolutePath, new MapperClassValue(mapper, declaredField, declaredField.get(object)));
            }
        }

        if (object instanceof Configuration configuration)
            return new ConfigProfile(comments, inlineComments, config, configuration);
        return new ConfigProfile(comments, inlineComments, config);
    }

    private static List<String> processComment(String[] comment, List<String> listToModify) {
        for (String s : comment) {
            if (s.isEmpty()) listToModify.add(null);
            else listToModify.add(s);
        }
        return listToModify;
    }

    private static List<String> getSpaceList(int spaces){
        List<String> lst =  new ArrayList<>();
        for (int i = 0; i < spaces; i++)
            lst.add(null);
        return lst;
    }

    public static String getFieldPath(Field declaredField, Configuration.Field fieldAnnotation) {
        if (fieldAnnotation.value().equals(Configuration.PATH_FROM_NAME_SECRET))
            return convertToPathFormat(declaredField.getName());
        return fieldAnnotation.value();
    }

    // Turns field name into a standard yaml path
    public static String convertToPathFormat(String fieldName) {
        if (fieldName.length() == 1) return fieldName;

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

    public static class MapperClassValue {

        private String mapper;
        private Field field;
        private Class<?> fieldClass;
        private Object value;

        public MapperClassValue(String mapper, Field field, Object value) {
            this.mapper = mapper;
            this.field = field;
            this.fieldClass = field.getType();
            this.value = value;
        }

        public String getMapper() {
            return mapper;
        }

        public Class<?> getFieldClass() {
            return fieldClass;
        }

        public Field getField() {
            return field;
        }

        public Object getValue() {
            return value;
        }
    }

}
