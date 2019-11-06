# KumuluzEE JCache sample

This sample shows basic usage of JCache static and dynamic configuration, usage of JCache annotations, programmatic API, CDI producers for Cache object and JCache listener registration.

A simple postman collection is included for testing.

# Requirements

In order to run this example you will need the following:

1. Java 8 (or newer), you can use any implementation:
    * If you have installed Java, you can check the version by typing the following in a command line:
        
        ```
        java -version
        ```

2. Maven 3.2.1 (or newer):
    * If you have installed Maven, you can check the version by typing the following in a command line:
        
        ```
        mvn -version
        ```
3. Git:
    * If you have installed Git, you can check the version by typing the following in a command line:
    
        ```
        git --version
        ```

# Tutorial

First create a basic rest application, you can refer to `kumuluzee-rest` sample for that. Then we will use JCache to cache and speed up our API responses.

First create 3 different endpoints for different test case.

```java
//This endpoint will invoke @CachePut to simulate storing to cache
@POST
@Path("/{id}")
public Response addData(@PathParam("id") String id, InvoiceData in) {
    
}

//This endpoint will invoke @CacheResult to return cached result from the first method or store a mock object if cache is empty.
@GET
@Path("/{id}")
public Response getData(@PathParam("id") String id) {
    
}

//This endpoint will invoke cache named "default" and simulate @CacheResult programmatically
@GET
@Path("/{id}/default")
public Response getInvoicesDefault(@PathParam("id") String id) {
    
}
```

## Add JCache dependency

Add `kumuluzee-jcache-caffeine` dependency to your root pom.

```xml
<dependency>
    <groupId>com.kumuluz.ee.jcache</groupId>
    <artifactId>kumuluzee-jcache-caffeine</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

## Business logic

Now let's implement the following interface which for our REST methods to use.
```java
public interface InvoiceService {

    //@CachePut
    InvoiceData getInvoice(String key);

    //@CacheResult
    InvoiceData putInvoice(String key, InvoiceData data);

    //Programatic API
    InvoiceData getInvoiceDefault(String key);
}
```

## JCache annotations

For our annotations we will use a cache named `invoices`. The first step is to configure it in `config.yaml`:
```yaml
kumuluzee:
  jcache:
    enabled: true
    caffeine:
      caffeine.jcache:
        invoices:
          key-type: java.lang.String
          value-type: com.kumuluz.ee.samples.jcache.rest.lib.InvoiceData
          policy:
            eager-expiration:
              after-write: "15s"
            maximum:
              size: 2
```
Cache will take `String` as a key and `InvocieData` object as value. Entries eagerly expire after 15 seconds and maximum number of entries in cache is 2.

The first method only stores data to cache and returns the same data. We want to use the path ID parameter as the cache key, so we annotate it with `@CacheKey`. We want to store the POST body as value, so we annotate it with `@CacheValue`.
```java
@CachePut(cacheName = "invoices")
@Override
public InvoiceData putInvoice(@CacheKey String key, @CacheValue InvoiceData data) {
    return data;
}
```

If we call this endpoint now:
```
POST http://localhost:8080/invoices/123
{
	"id": "123456"
}
```
We can't really see anything yet since data is only stored to cache and immediately returned.

Now to implement the getter:
```java
@CacheResult(cacheName = "invoices")
public InvoiceData getInvoice(@CacheKey String key) {
    try {
        Thread.sleep(3000);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
    return new InvoiceData("654321");
}
```

We use the same path ID parameter as cache key. If the key is already present, `@CacheResult` will return the cached value, otherwise a mock data is returned and cached. We flipped the entity ID in the example to notice the difference right away. A sleep of 3 seconds is added to simulate slow business logic.
Also note that we use the same named cache called `invoices` so both annotations operate on the same cache.

Now let's test this. Run the application and load the included postman collection.

1. CachePut+CacheResult test
```
POST http://localhost:8080/invoices/123
{
	"id": "123456"
}
```
```
GET http://localhost:8080/invoices/123
```
Executing both calls one after the other, we get a very fast response (t<3s) from the GET method due to value already existing in cache.
Waiting 15 seconds and executing GET again results in t>3s due to cache entry eagerly expiring and mock object id of `654321`.

2. CacheResult test
```
GET http://localhost:8080/invoices/123
```
Simply executing the call repeatedly will cause a slow call every 15 seconds.

3. CacheResult max entries test
```
GET http://localhost:8080/invoices/1
GET http://localhost:8080/invoices/2
GET http://localhost:8080/invoices/3
```
Execute each GET request twice. First call is slow, second is fast. After executing #3, calling #2 is fast but calling #1 is  slow. That is because we reached max entry size of 2 and earliest entry is evicted from cache.

## JCache programmatic API

Finally, let's implement an equivalent to `@CacheResult` with programmatic API. First, configure a cache named `default`.

```yaml
kumuluzee:
  jcache:
    enabled: true
    caffeine:
      caffeine.jcache:
        default:
          policy:
            eager-expiration:
              after-write: "10s"
            maximum:
              size: 10000
```

Programmatic API revolves around the `Cache` object which is thread safe and a single instance per named cache should exist for our application. Therefore we need an application scoped producer.

```java
public class CacheProducer {

    @Inject
    private CacheManager cacheManager;

    @DefaultCache
    @Produces
    @ApplicationScoped
    public Cache<String, InvoiceData> getDefaultCache() {
        return cacheManager.getCache("default");
    }
}
```
Since `default` is already configured in `config.yaml` we can simply get the cache from the manager. If we have multiple Cache producers we also add a producer qualifier to differentiate the injects:
```java
@Qualifier
@Retention(RUNTIME)
public @interface DefaultCache {
}
```

Implement the business logic method:

```java
@ApplicationScoped
public class InvoiceServiceImpl implements InvoiceService {

    @DefaultCache
    @Inject
    private Cache<String, InvoiceData> defaultCache;

    @Override
    public InvoiceData getInvoiceDefault(String key) {

        if (defaultCache.containsKey(key)) {
            return defaultCache.get(key);
        }
        else {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            InvoiceData d = new InvoiceData(key);
            defaultCache.put(key, d);
            return d;
    }
}
```

You can also find a `my` named cache in the sample code which demonstrates how to dynamically configure a `Cache` instead of using a config file.