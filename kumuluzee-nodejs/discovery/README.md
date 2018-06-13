# KumuluzEE Node.js Discovery

> register and discover services in kumuluzEE format.

The goal of this sample is to demonstrate how to use library for discovery in Node.js. The tutorial will guide you through the necessary steps. 

## Requirements

In order to run this sample you will need:
1. Node.js version >=8.0.0 installed. 
  * If you have Node.js installed, you can check your version by typing the following in command line:
  ```
    node -v
  ```
2. NPM version >= 5.5.0 installed.
  * If you have NPM installed, you can check your version by typing the following in command line:
   ```
     npm -v
   ```
3. Git:
  * If you have installed Git, you can check the version by typing the following in a command line:
  ```
    git --version
  ```
  
## Prerequisites

To run this sample you will need an etcd instance. Note that such setup with only one etcd node is not viable for 
production environments, but only for developing purposes. Here is an example on how to quickly run an etcd instance 
with docker:

   ```bash
    $ docker run -d -p 2379:2379 \
      --name etcd \
      --volume=/tmp/etcd-data:/etcd-data \
      quay.io/coreos/etcd:latest \
      /usr/local/bin/etcd \
      --name my-etcd-1 \
      --data-dir /etcd-data \
      --listen-client-urls http://0.0.0.0:2379 \
      --advertise-client-urls http://0.0.0.0:2379 \
      --listen-peer-urls http://0.0.0.0:2380 \
      --initial-advertise-peer-urls http://0.0.0.0:2380 \
      --initial-cluster my-etcd-1=http://0.0.0.0:2380 \
      --initial-cluster-token my-etcd-token \
      --initial-cluster-state new \
      --auto-compaction-retention 1 \
      -cors="*"
   ```

It is also recommended that you are familiar with Node.js environment.

## Usage

This example is installed and run with NPM.

1. Install dependencies using NPM: `$ npm install`
2. Run the sample using `$ npm run start`
  
Application can be accessed on the following URL:
* http://localhost:3000/lookup

To shut down the example simply stop the processes in the foreground.

## Tutorial

This tutorial will guide you through the steps required to register and discover services.

We will develop light Express application which will register service and return its url by looking it up.

We will follow this steps:
* Create NPM project
* Install required dependencies
* Initialize discovery util
* Run it

### Create NPM project

We can create blank NPM project by typing `$ npm init` in command line.

During this process you will be asked to choose the name, version, etc. of your project. You can keep all the defaults by pressing enter. 

When project is initialized you will get a file called package.json. Optionally you can setup start script, so that your project can be run using `$ npm start`:
```json
"scripts": {
    "start": "node index.js"
  },
``` 

### Install required dependencies
Since we will use Express framework to setup server we need to install it by typing this in command line:
```bash
$ npm install --save express
```

Now, we install KumuluzEE Discovery library:
```bash
$ npm install --save @kumuluz/kumuluzee-discovery
```

**Note that if you install discovery library, the configuration library is installed alongside it.**

### Initializing discovery util

First, we are going to create file called **config.yaml**, where we will write our application's configuration:
```yaml
kumuluzee:
  # name of our service
  name: testni-servis
  server:
    # url where our service will live
    base-url: http://localhost:3000
    http:
      port: 3000
  env: 
    name: dev
  # specify hosts for discovery register
  discovery:
    etcd:
      hosts: http://localhost:2379
  # specify hosts for remote configuration
  config:
    etcd:
      hosts: http://localhost:2379
```

Now, we need to initialize our discovery util and register our service:

```javascript 1.7
// require path library (part of nodejs core)
const path = require("path");
// get Discovery util
const KumuluzeeDiscovery = require("@kumuluz/kumuluzee-discovery").default;
// get Configuration util
const configurationUtil = require("@kumuluz/kumuluzee-config").ConfigurationUtil;
// specify path to config.yaml
const configurationPath = path.join(__dirname, "config.yaml");


let util = null;

const register = async() => {
    // first initialize configuration
    await configurationUtil.initialize({
        extension: "etcd",
        configPath: configurationPath
    });
    util = configurationUtil;
    // initialize discovery util
    await KumuluzeeDiscovery.initialize({extension: "etcd"});
    // register service
    KumuluzeeDiscovery.registerService();
};

register();
```

Now we will create simple Express server, which will lookup url of our service and send it to client:
```javascript 1.7
const express = require("express");
const server = express();

server.get("/lookup", async (req, res) => {
	const response = await KumuluzeeDiscovery.discoverService({
		value: "testni-servis", // name of our service
		version: "1.0.0", // version of our service
		environment: "dev", // environment
		accessType: "DIRECT"
	});
	res.json({service: response});
});

server.listen(3000, () => console.log("server running on port 3000!"));
```

Response should be:
```json
{
  "service": "http://localhost:3000/"
}
```