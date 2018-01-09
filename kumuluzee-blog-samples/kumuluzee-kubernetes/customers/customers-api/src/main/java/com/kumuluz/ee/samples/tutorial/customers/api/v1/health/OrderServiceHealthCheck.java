package com.kumuluz.ee.samples.tutorial.customers.api.v1.health;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.health.Health;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Provider;

@Health
@ApplicationScoped
public class OrderServiceHealthCheck implements HealthCheck {

    @Inject
    @ConfigProperty(name = "rest-properties.external-dependencies.order-service.enabled")
    private Provider<Boolean> orderServiceEnabledProvider;

    @Override
    public HealthCheckResponse call() {

        if (orderServiceEnabledProvider.get().booleanValue()) {
            return HealthCheckResponse.named(OrderServiceHealthCheck.class.getSimpleName()).up().build();
        } else {
            return HealthCheckResponse.named(OrderServiceHealthCheck.class.getSimpleName()).down().build();
        }

    }
}
