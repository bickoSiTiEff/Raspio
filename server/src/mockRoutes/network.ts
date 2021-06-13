import {Router} from "express";

const network = Router();

let config = {
	"mode": "ap",
	"ssid": "Raspio_MOCK",
	"password": "password"
};

network.get("/", (req, res) => {
	res.send({
		mode: config.mode,
		ssid: config.ssid
	});
});

network.post("/", (req, res) => {
	const newConfig = req.body;
	config = newConfig;
	const modeString = newConfig.mode === "ap" ? "Starting access point" : "Connecting to WIFI network";
	const passwordString = newConfig.password === undefined ? "no password" : `password "${newConfig.password}"`;
	console.log(`[MOCK] ${modeString} with SSID "${newConfig.ssid}" and ${passwordString}.`);
	res.status(200).send();
});

export default network;