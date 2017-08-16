# KumuluzEE Discovery - Kubernetes sample

> Deploy KumuluzEE microservices, which use Service Discovery to Kubernetes cluster.

The objective of this sample is to show how to deploy services, using KumuluzEE service discovery to Kubernetes cluster.
The tutorial will guide you through the necessary steps. You will build two KumuluzEE samples and deploy them to
Kubernetes cluster.
Required knowledge: basic familiarity with Kubernetes and basic familarity with KumuluzEE service discovery.

## Requirements

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

4. kubectl command tool:
    * If you have installed kubectl, you can check the version by typing the following in a command line:
    
        ```
        kubectl version
        ```

5. Kubernetes cluster:
    * This sample uses local [minikube](http://kubernetes.io/docs/tasks/tools/install-minikube/) cluster.
    You can check the version by typing the following in a command line:
    
        ```
        minikube version
        ```

6. Docker:
    * If you have installed [docker](http://docs.docker.com/engine/installation/)
     you can check the version by typing the following in a command line:
    
        ```
        docker version
        ```

## Prerequisites

In order to start this sample, you will have to setup a Kubernetes cluster and configure kubectl tool to use it.
To run a local minikube cluster run following commands:

1. Start minikube cluster and configure kubectl to use it:
    
    ```bash
    $ minikube start
    ```

2. Configure docker to use minikube registry:

    ```bash
    $ eval $(minikube docker-env)
    ```

## Usage

1. Build `discovery-discover-jaxrs` and `discovery-register` samples using maven and create Docker images:

    ```bash
    $ cd discovery-samples/discovery-discover-jaxrs
    $ mvn clean package
    $ docker build -t discover-sample:v1 .
    $ cd ../discovery-register
    $ mvn clean package
    $ docker build -t register-sample:v1 .
    ```

2. Start an etcd instance in Kubernetes cluster:

    ```bash
    $ cd ../discovery-kubernetes
    $ kubectl create -f etcd.yaml
    ```

3. Run both samples in Kubernetes cluster:

    ```bash
    $ kubectl create -f register-deployment.yaml
    $ kubectl create -f discover-deployment.yaml
    ```

4. Create service for discover deployment and expose its NodePort:

    ```bash
    $ kubectl create -f discover-service.yaml
    ```

5. Get the port, which was assigned to discover service:

    ```bash
    $ kubectl describe service discover | egrep NodePort:
    ```

The application/service can be accessed on the following URL:
* JAX-RS REST resource, discovery using annotations - http://MINIKUBE_IP:DISCOVER_SERVICE_PORT/v1/discover
* JAX-RS REST resource, programmatic discovery - http://MINIKUBE_IP:DISCOVER_SERVICE_PORT/v1/programmatic

You can find minikube IP using following command:

```bash
$ minikube ip
```

To remove Kubernetes deployments and service run following commands:

```bash
$ kubectl delete deployment register-deployment discover-deployment
$ kubectl delete service discover etcd
```

To shut down the minikube cluster run following command:

```bash
$ minikube stop
```

## Tutorial

This tutorial will guide you through the steps required to deploy two sample services,
which use KumuluzEE Service Discovery, to Kubernetes cluster.
We will deploy a sample, which registers itself to etcd and a sample, which discovers it.

We will follow these steps:
* Create two Dockerfiles for existing samples
* Build both samples
* Create single instance etcd deployment and service
* Create deployments for both samples
* Create a service to expose discover sample outside the cluster

### Create two Dockerfiles for existing samples

Add Dockerfile to the root of the `discovery-discover-jaxrs` project:
```dockerfile
FROM isuper/java-oracle

COPY target /usr/src/myapp

WORKDIR /usr/src/myapp

EXPOSE 8080

CMD ["java", "-server", "-cp", "classes:dependency/*", "com.kumuluz.ee.EeApplication"]
```

We will use the same Dockerfile for both samples, so copy it into the root of the `discovery-register` project.

### Build both samples

Use commands, described in the Usage section step 1 to build both images.

### Create single instance etcd deployment and service

Use the following deployment to create single etcd instance in the cluster:

```yaml
apiVersion: apps/v1beta1
kind: Deployment
metadata:
  name: etcd-deployment
spec:
  replicas: 1
  template:
    metadata:
      labels:
        app: etcd
    spec:
      containers:
      - command:
        - /usr/local/bin/etcd
        - --name
        - etcd0
        - --initial-advertise-peer-urls
        - http://etcd:2380
        - --listen-peer-urls
        - http://0.0.0.0:2380
        - --listen-client-urls
        - http://0.0.0.0:2379
        - --advertise-client-urls
        - http://etcd:2379
        - --initial-cluster-state
        - new
        image: quay.io/coreos/etcd:latest
        name: etcd
        ports:
        - containerPort: 2379
          name: client
          protocol: TCP
        - containerPort: 2380
          name: server
          protocol: TCP

---

apiVersion: v1
kind: Service
metadata:
  name: etcd
spec:
  type: NodePort
  ports:
  - name: client
    port: 2379
    protocol: TCP
    targetPort: 2379
  - name: server
    port: 2380
    protocol: TCP
    targetPort: 2380
  selector:
    app: etcd
```

Use commands, described in the Usage section step 2 to create the deployment.

By creating etcd Kubernetes service, etcd instance is available outside the cluster by its
NodePort (http://NODE-IP:NODE-PORT). If you wish to register services outside the cluster, make sure you use this
address in `KUMULUZEE_DISCOVERY_ETCD_HOSTS` key.

### Create deployments for both samples

Create deployment for the discover sample:

```yaml
apiVersion: apps/v1beta1
kind: Deployment
metadata:
  name: discover-deployment
spec:
  replicas: 1
  template:
    metadata:
      labels:
        app: discover
    spec:
      containers:
      - image: discover-sample:v1
        name: discover
        env:
          - name: KUMULUZEE_DISCOVERY_CLUSTER
            value: minikube
          - name: KUMULUZEE_DISCOVERY_ETCD_HOSTS
            value: http://etcd:2379
        ports:
        - containerPort: 8080
          name: server
          protocol: TCP
```

Environment variable `KUMULUZEE_DISCOVERY_CLUSTER` is an id that should be the same for every service,
running in the same cluster (for those which register and those which discover services).
We are using id `minikube` in this example.

We will use Kubernetes DNS entry for our etcd host, which was created automatically when we added etcd service in the
previous step. If you have Kubernetes DNS disabled, you could use etcd service IP instead.

Create deployment for the register sample:

```yaml
apiVersion: apps/v1beta1
kind: Deployment
metadata:
  name: register-deployment
spec:
  replicas: 1
  template:
    metadata:
      labels:
        app: register
    spec:
      containers:
      - image: register-sample:v1
        name: register
        env:
#          - name: KUMULUZEE_SERVER_BASEURL
#            value: http://NODE-IP:SERVICE-NODEPORT
          - name: KUMULUZEE_DISCOVERY_CLUSTER
            value: minikube
          - name: KUMULUZEE_DISCOVERY_ETCD_HOSTS
            value: http://etcd:2379
        ports:
        - containerPort: 8080
          name: server
          protocol: TCP
```

We use the same environment variables in this deployment.

If you want your service to be accessible outside the cluster, you should also specify environment
variable `KUMULUZEE_SERVER_BASEURL` (commented lines above). Services outside your cluster will then access your service by
`KUMULUZEE_SERVER_BASEURL`, services inside your cluster will access your service by its pod IP.

Use commands, described in the Usage section step 3 to create the deployment.

### Create a service to expose discover sample outside the cluster

In order to access discovering service, we need to expose it through Kubernetes service:

```yaml
apiVersion: v1
kind: Service
metadata:
  name: discover
spec:
  type: NodePort
  ports:
  - port: 8080
    protocol: TCP
    targetPort: 8080
  selector:
    app: discover

```

Use commands, described in the Usage section step 5 to access the service.

#### Running services outside cluster

If you want, you can also run services outside your cluster. In that case, don't specify `KUMULUZEE_DISCOVEY_CLUSTER`
key and services will be able to correctly register and discover each other. You can also run services in different
cluster, in which case you should specify different `KUMULUZEE_DISCOVEY_CLUSTER` key for all services in another
cluster. If you are running services on different locations, remember that in order for services to correctly discover
each other, every service should be registered in the same etcd cluster.
