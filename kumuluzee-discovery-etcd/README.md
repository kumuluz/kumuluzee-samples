# KumuluzEE Discovery with etcd

> Samples for KumuluzEE Discovery extension

This module contains four samples that will introduce you to KumuluzEE Discovery extension using etcd:

- [`discovery-etcd-register`](http://github.com/kumuluz/kumuluzee-samples/tree/master/kumuluzee-discovery-etcd/discovery-etcd-register) JAX-RS microservice that registers itself to etcd
- [`discovery-etcd-discover-servlet`](http://github.com/kumuluz/kumuluzee-samples/tree/master/kumuluzee-discovery-etcd/discovery-etcd-discover-servlet) servlet microservice that discovers
and calls `discovery-register` microservice
- [`discovery-etcd-discover-jaxrs`](http://github.com/kumuluz/kumuluzee-samples/tree/master/kumuluzee-discovery-etcd/discovery-etcd-discover-jaxrs) JAX-RS microservice that discovers
and calls `discovery-register` microservice
- [`discovery-etcd-kubernetes`](https://github.com/kumuluz/kumuluzee-samples/tree/master/kumuluzee-discovery-etcd/discovery-etcd-kubernetes) deploy `discovery-register` and `discovery-discover-jaxrs` samples to Kubernetes cluster 

More information about the samples can be found in the README of each sample.
