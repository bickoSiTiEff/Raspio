import Router from "express-promise-router";
import {Router as ExpressRouter} from "express";
import {MPC} from "mpc-js";

export default async (): Promise<ExpressRouter> => {
	const playback = Router();

	const mpc = new MPC();
	await mpc.connectTCP(process.env.MPD_HOST || "localhost", Number(process.env.MPD_PORT || 6600));

	playback.post("/play", async (req, res) => {
		await mpc.playback.play();
		res.status(204).send();
	});

	playback.post("/next", async (req, res) => {
		await mpc.playback.next();
		res.status(204).send();
	});

	playback.post("/stop", async (req, res) => {
		await mpc.playback.stop();
		res.status(204).send();
	});

	return playback;
};