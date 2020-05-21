package com.kumuluz.ee.samples.openapi.test;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

@RunWith(Arquillian.class)
public class OpenapiUiTest {

    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Test
    public void uiTest() throws IOException {
        HttpURLConnection httpClient =
                (HttpURLConnection) new URL("http://localhost:8080/api-specs/ui").openConnection();
        httpClient.setRequestMethod("GET");
        int responseCode = httpClient.getResponseCode();
        Assert.assertEquals(responseCode, 200);
    }
}