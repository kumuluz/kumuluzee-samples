const express = require("express");
const server = express();
const path = require("path");
const ConfigurationUtil = require("@kumuluz/kumuluzee-config").ConfigurationUtil;

const configurationPath = path.join(__dirname, "..", "config.yaml");
let util = null;

const init = async () => {
	await ConfigurationUtil.initialize({
		extension: "etcd",
		configPath: configurationPath
	});
	util = ConfigurationUtil;
};

init();

server.get("/", async(req, res) => {
	const val = await util.get("rest-config.string-property");
	res.json({val: val});
});

server.listen(3000, () => console.log("server running on port 3000!"));
