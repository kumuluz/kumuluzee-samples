package com.kumuluz.ee.samples.tutorial.orders.api.v1.health;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class HealthManagementDemo {

    private boolean healthy;

    @PostConstruct
    private void init() {
        healthy = true;
    }

    public boolean isHealthy() {
        return healthy;
    }

    public void setHealthy(boolean healthy) {
        this.healthy = healthy;
    }
}
