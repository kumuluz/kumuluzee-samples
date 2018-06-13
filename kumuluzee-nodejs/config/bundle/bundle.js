const ConfigBundle = require("@kumuluz/kumuluzee-config").default;

const remoteConfig = new ConfigBundle({
    prefixKey: "rest-config",
    type: "object",
    fields: {
        stringProperty: { // custom name to be used in code
            type: "string",
            name: "string-property", // name as it is written in config.yaml
            watch: true // listen for changes
        },
        integerProperty: {
            type: "number",
            name: "integer-property"
        },
        booleanProperty: {
            type: "boolean",
            name: "boolean-property"
        },
        objectProperty: {
            type: "object",
            name: "object-property",
            fields: {
                subProperty: {
                    type: "string",
                    name: "sub-property"
                },
                subProperty2: {
                    type: "string",
                    name: "sub-property-2"
                }
            }
        }
    }
});

exports.remoteConfig = remoteConfig;
