import {Router} from "express";

const transmission = Router();

let running = false;

transmission.post("/start", (req, res) => {
	const frequency = Number(req.query.frequency);

	if (!running) {
		running = true;
		console.log(`[MOCK] Started transmitter on frequency ${frequency}!`);
		res.status(204).send();
	} else {
		res.status(400).send();
	}
});

transmission.post("/stop", (req, res) => {
	if (running) {
		running = false;
		console.log("[MOCK] Stopped transmitter.");
		res.status(204).send();
	} else {
		res.status(400).send();
	}
});

export default transmission;