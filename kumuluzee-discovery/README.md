# KumuluzEE Discovery samples

> Samples for KumuluzEE Discovery extension

This module contains four samples that will introduce you to KumuluzEE
Discovery extension:

- [`discovery-register`](http://github.com/kumuluz/kumuluzee-samples/tree/master/kumuluzee-discovery/discovery-register) JAX-RS microservice that registers itself to etcd
- [`discovery-discover-servlet`](http://github.com/kumuluz/kumuluzee-samples/tree/master/kumuluzee-discovery/discovery-discover-servlet) servlet microservice that discovers
and calls `discovery-register` microservice
- [`discovery-discover-jaxrs`](http://github.com/kumuluz/kumuluzee-samples/tree/master/kumuluzee-discovery/discovery-discover-jaxrs) JAX-RS microservice that discovers
and calls `discovery-register` microservice
- [`discovery-kubernetes`](https://github.com/kumuluz/kumuluzee-samples/tree/master/kumuluzee-discovery/discovery-kubernetes) deploy `discovery-register` and `discovery-discover-jaxrs` samples to Kubernetes cluster 

More information about the samples can be found in the README of each sample.
