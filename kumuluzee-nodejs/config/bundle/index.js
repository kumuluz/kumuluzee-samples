const express = require("express");
const server = express();
const path = require("path");
const ConfigurationUtil = require("@kumuluz/kumuluzee-config").ConfigurationUtil;

const remoteConfig = require("./bundle").remoteConfig;
const configurationPath = path.join(__dirname, "..", "config.yaml");
let util = null;

const init = async () => {
	await remoteConfig.initialize({
		extension: "etcd",
		configPath: configurationPath
	});
	util = ConfigurationUtil;
};

init();

server.get("/", async (req, res) => {
	const stringProperty = await util.get("rest-config.string-property");
	const integerProperty = remoteConfig.integerProperty;
	const objectProperty = remoteConfig.objectProperty.subProperty;
	res.json({
		val_from_util: stringProperty,
		val_from_bundle: integerProperty,
		val_from_bundle_2: objectProperty
	});
});

server.listen(3000, () => console.log("server running on port 3000!"));