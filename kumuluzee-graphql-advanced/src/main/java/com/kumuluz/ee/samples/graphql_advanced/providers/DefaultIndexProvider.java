package com.kumuluz.ee.samples.graphql_advanced.providers;

import io.leangen.graphql.generator.mapping.strategy.DefaultValueProvider;
import io.leangen.graphql.metadata.OperationArgumentDefaultValue;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Parameter;

public class DefaultIndexProvider implements DefaultValueProvider {
    @Override
    public OperationArgumentDefaultValue getDefaultValue(Parameter parameter, AnnotatedType parameterType, OperationArgumentDefaultValue initialValue) {
        Integer asd = 0;
        return new OperationArgumentDefaultValue(asd);
    }
}
