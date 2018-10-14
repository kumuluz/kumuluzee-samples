package com.kumuluz.ee.samples.graphql_advanced.providers;

import io.leangen.graphql.metadata.strategy.value.DefaultValueProvider;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.AnnotatedType;

public class DefaultIndexProvider implements DefaultValueProvider {

    @Override
    public Object getDefaultValue(AnnotatedElement targetElement, AnnotatedType type, Object initialValue) {
        return 0;
    }
}
