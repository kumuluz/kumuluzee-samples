package com.dk.tracingblog.gamma;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import java.util.HashMap;

@ApplicationScoped
public class Database {
    private HashMap<Integer, String> data;

    @PostConstruct
    private void init() {
        data = new HashMap<Integer, String>();
        data.put(1, "gamma");
    }

    public String get(Integer id) {
        return data.get(id);
    }

}
