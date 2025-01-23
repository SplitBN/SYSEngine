package dev.splityosis.sysengine.configlib.configuration;

import dev.splityosis.sysengine.configlib.manager.strategy.FieldPathConverter;
import dev.splityosis.sysengine.utils.ReflectionUtil;

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
    public static ConfigProfile readConfigObject(Object object, int sectionSpacing, int fieldSpacing, FieldPathConverter fieldPathConverter) throws IllegalAccessException {
        Map<String, MapperClassValue> config = new LinkedHashMap<>();
        Map<String, List<String>> comments = new LinkedHashMap<>();
        Map<String, List<String>> inlineComments = new LinkedHashMap<>();

        String currentSectionPath = "";

        for (Field declaredField : ReflectionUtil.getAnnotatedFields(object.getClass(), Configuration.Field.class, Configuration.Section.class)) {
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

                String absolutePath = currentSectionPath + (!currentSectionPath.isEmpty() ? '.' : "") + getFieldPath(fieldPathConverter, declaredField, fieldAnnotation);

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
            else {
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
            }
        }

        if (object instanceof Configuration)
            return new ConfigProfile(comments, inlineComments, config, (Configuration) object);
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

    public static String getFieldPath(FieldPathConverter fieldPathConverter, Field declaredField, Configuration.Field fieldAnnotation) {
        if (fieldAnnotation.value().equals(Configuration.PATH_FROM_NAME_SECRET))
            return fieldPathConverter.convertToPathFormat(declaredField.getName());
        return fieldAnnotation.value();
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
