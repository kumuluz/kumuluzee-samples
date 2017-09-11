package com.kumuluz.ee.samples.cors;

import com.kumuluz.ee.cors.annotations.CrossOrigin;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * Created by zvoneg on 01/08/17.
 */
@ApplicationPath("v1")
public class CustomerApplication extends Application {
}
