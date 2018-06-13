const express = require("express");
const server = express();
const path = require("path");

const configurationPath = path.join(__dirname, "config.yaml");

const KumuluzeeDiscovery = require("@kumuluz/kumuluzee-discovery").default;
const configurationUtil = require("@kumuluz/kumuluzee-config").ConfigurationUtil;

let util = null;

const register = async() => {
	await configurationUtil.initialize({
		extension: "etcd",
		configPath: configurationPath
	});
	util = configurationUtil;
	await KumuluzeeDiscovery.initialize({extension: "etcd"});
	KumuluzeeDiscovery.registerService();
};
register();

server.get("/lookup", async (req, res) => {
	const response = await KumuluzeeDiscovery.discoverService({
		value: "testni-servis",
		version: "1.0.0",
		environment: "dev",
		accessType: "DIRECT"
	});
	res.json({service: response});
});

server.listen(3000, () => console.log("server running on port 3000!"));
