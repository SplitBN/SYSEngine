package dev.splityosis.sysengine.configlib.manager;

public class ConfigOptions {

    private int fieldSpacing = 0;
    private int sectionSpacing = 1;


    public ConfigOptions() {}

    /**
     * Gets the number of empty lines before a field.
     * @return the number of empty lines before the field
     */
    public int getFieldSpacing() {
        return fieldSpacing;
    }

    /**
     * Sets the number of empty lines before a field.
     * @param fieldSpacing the number of empty lines before the field
     * @return the current ConfigOptions instance (for chaining)
     */
    public ConfigOptions setFieldSpacing(int fieldSpacing) {
        this.fieldSpacing = fieldSpacing;
        return this;
    }

    /**
     * Gets the number of empty lines before a section.
     * @return the number of empty lines before the section
     */
    public int getSectionSpacing() {
        return sectionSpacing;
    }

    /**
     * Sets the number of empty lines before a section.
     * @param sectionSpacing the number of empty lines before the section
     * @return the current ConfigOptions instance (for chaining)
     */
    public ConfigOptions setSectionSpacing(int sectionSpacing) {
        this.sectionSpacing = sectionSpacing;
        return this;
    }
}
