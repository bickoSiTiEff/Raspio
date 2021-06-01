import {Router} from "express";

const transmission = Router();

let running = false;

transmission.post("/start", (req, res) => {
	if (!running) {
		running = true;
		res.status(204).send();
	} else {
		res.status(400).send();
	}
});

transmission.post("/stop", (req, res) => {
	if (running) {
		running = false;
		res.status(204).send();
	} else {
		res.status(400).send();
	}
});

export default transmission;