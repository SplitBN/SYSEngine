package dev.splityosis.sysengine.configlib.configuration;

import dev.splityosis.sysengine.configlib.manager.MapperRegistry;

import java.lang.annotation.*;

/**
 * The Configuration interface defines annotations used to map configuration sections and fields,
 * as well as attach comments to those sections and fields.
 *
 * This interface provides a set of annotations to describe the structure of a configuration file.
 * Sections and fields can be annotated with comments (both normal and inline) to provide
 * helpful context for users and maintainers.
 */
public interface Configuration {

    /**
     * A constant that represents a special value used to generate a path from a name.
     * This value is typically used internally and shouldn't be required for general use.
     */
    String PATH_FROM_NAME_SECRET = "GENERATE_PATH_FROM_NAME_5410"; // If you actually need this path then you have problems

    /**
     * Annotation used to attach a comment to a configuration section.
     * This comment will appear above the section in the configuration file.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @interface SectionComment {
        /**
         * The comment(s) to attach to the section.
         *
         * @return an array of comment strings.
         */
        String[] value();
    }

    /**
     * Annotation used to attach an inline comment to a configuration section.
     * This comment will appear on the same line as the section.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @interface SectionInlineComment {
        /**
         * The inline comment(s) to attach to the section.
         *
         * @return an array of comment strings.
         */
        String[] value();
    }

    /**
     * Annotation used to define a configuration section.
     * A section groups related fields together in the configuration file.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @interface Section {
        /**
         * The name of the section, used to identify the group of fields.
         *
         * @return the name of the section (e.g., "world-settings").
         */
        String value();
    }

    /**
     * Annotation used to attach a comment to a configuration field.
     * This comment will appear above the field in the configuration file.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @interface FieldComment {
        /**
         * The comment(s) to attach to the field.
         *
         * @return an array of comment strings.
         */
        String[] value() default "";
    }

    /**
     * Annotation used to attach an inline comment to a configuration field.
     * This comment will appear on the same line as the field.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @interface FieldInlineComment {
        /**
         * The inline comment(s) to attach to the field.
         *
         * @return an array of comment strings.
         */
        String[] value() default "";
    }

    /**
     * Annotation used to define a configuration field.
     * Fields represent individual configuration options within a section.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @interface Field {
        /**
         * The path where the field is located in the configuration.
         * If left as the default value, the path will be generated from the field name.
         *
         * @return the path to set the field at.
         */
        String value() default PATH_FROM_NAME_SECRET; // The path to set the field at
    }

    /**
     * Annotation used to specify a custom mapper for a field.
     * A mapper is a function or handler that defines how the field's value is converted
     * when reading from or writing to a configuration (e.g., YAML).
     *
     * <p>The {@code mapper} attribute specifies the name of the registered mapper function or handler.</p>
     *
     * <p>Example:</p>
     * <pre>
     * public class MyConfig {
     *     @Mapper(mapper = "custom-itemstack-mapper")
     *     private ItemStack item;
     * }
     * </pre>
     *
     * @see MapperRegistry  // Manages registered mappers
     */
    @Retention(RetentionPolicy.RUNTIME)
    @interface Mapper {
        /**
         * The name of the registered mapper to use for this field.
         *
         * @return the name of the mapper.
         */
        String value() default "";
    }


    /**
     * Skips adding this field to the configuration if its value is `null`.
     *
     * <p>Example:
     * <pre>
     * {@code
     * @IgnoreIfNull
     * private String optionalSetting = null;
     * }
     * </pre>
     */
    @Retention(RetentionPolicy.RUNTIME)
    @interface IgnoreIfNull {}
}
