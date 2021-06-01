import {Router} from "express";

const playback = Router();

playback.post("/play", (req, res) => {
	console.log("[MOCK] Play!");
	res.status(204).send();
});

playback.post("/next", (req, res) => {
	console.log("[MOCK] Next!");
	res.status(204).send();
});

playback.post("/stop", (req, res) => {
	console.log("[MOCK] Stop!");
	res.status(204).send();
});

export default playback;