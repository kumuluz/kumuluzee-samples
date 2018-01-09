package com.kumuluz.ee.samples.tutorial.orders.api.v1.health;

import org.eclipse.microprofile.health.Health;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Health
@ApplicationScoped
public class OrderServiceHealthCheck implements HealthCheck {

    @Inject
    private HealthManagementDemo healthManagementDemo;

    @Override
    public HealthCheckResponse call() {

        if (healthManagementDemo.isHealthy()) {
            return HealthCheckResponse.named(OrderServiceHealthCheck.class.getSimpleName()).up().build();
        } else {
            return HealthCheckResponse.named(OrderServiceHealthCheck.class.getSimpleName()).down().build();
        }

    }
}
