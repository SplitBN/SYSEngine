package dev.splityosis.sysengine.test;

import dev.splityosis.sysengine.configlib.configuration.Configuration;

public class TestConfig implements Configuration {

    @Section("test")

    @FieldInlineComment("trust me i know")
    @Field public String volkoff = "beta";

    @FieldInlineComment("trust me i know #2")
    @Field public String split = "alpha";

}
