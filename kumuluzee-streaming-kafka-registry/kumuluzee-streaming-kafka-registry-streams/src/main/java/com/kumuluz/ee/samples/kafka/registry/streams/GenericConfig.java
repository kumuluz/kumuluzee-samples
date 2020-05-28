package com.kumuluz.ee.samples.kafka.registry.streams;

import com.kumuluz.ee.configuration.cdi.ConfigBundle;
import com.kumuluz.ee.configuration.cdi.ConfigValue;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
@ConfigBundle(".")
public class GenericConfig {

    @ConfigValue("kumuluzee.streaming.kafka.streams-avro.schema-registry-url")
    private String schemaRegistryUrl;

    public String getSchemaRegistryUrl() {
        return schemaRegistryUrl;
    }

    public void setSchemaRegistryUrl(String schemaRegistryUrl) {
        this.schemaRegistryUrl = schemaRegistryUrl;
    }
}
