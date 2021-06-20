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

	playback.post("/pause", async (req, res) => {
		await mpc.playback.pause();
		res.status(204).send();
	});

	playback.post("/previous", async (req, res) => {
		await mpc.playback.previous();
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

	playback.post("/seek", async (req, res) => {
		const time = Number(req.query.time);
		await mpc.playback.seekCur(time, false);
		res.status(204).send();
	});

	playback.post("/consume", async (req, res) => {
		const consume = Boolean(req.query.consume);
		await mpc.playbackOptions.setConsume(consume);
		res.status(204).send();
	});

	playback.post("/crossfade", async (req, res) => {
		const amount = Number(req.query.amount);
		await mpc.playbackOptions.setCrossfade(amount);
		res.status(204).send();
	});

	playback.post("/shuffle", async (req, res) => {
		const shuffle = Boolean(req.query.shuffle);
		await mpc.playbackOptions.setRandom(shuffle);
		res.status(204).send();
	});

	playback.post("/repeat", async (req, res) => {
		const repeat = Boolean(req.query.repeat);
		await mpc.playbackOptions.setRepeat(repeat);
		res.status(204).send();
	});

	playback.post("/volume", async (req, res) => {
		const volume = Number(req.query.volume);
		await mpc.playbackOptions.setVolume(volume);
		res.status(204).send();
	});

	return playback;
};