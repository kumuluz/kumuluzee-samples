package com.kumuluz.ee.samples.kumuluz_config;

import com.kumuluz.ee.configuration.ConfigurationDecoder;

import javax.xml.bind.DatatypeConverter;
import java.util.Arrays;
import java.util.List;

public class CustomConfigurationDecoder implements ConfigurationDecoder {

    @Override
    public List<String> encodedKeys() {
        return Arrays.asList("rest-config.encoded-property");
    }

    @Override
    public String decode(String value) {
        return new String(DatatypeConverter.parseBase64Binary(value));
    }
}
