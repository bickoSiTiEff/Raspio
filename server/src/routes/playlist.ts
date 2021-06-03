import Router from "express-promise-router";
import {Router as ExpressRouter} from "express";
import {MPC} from "mpc-js";
import * as API from "../models/Song";

export default async (): Promise<ExpressRouter> => {
	const playlist = Router();

	const mpc = new MPC();
	await mpc.connectTCP(process.env.MPD_HOST || "localhost", Number(process.env.MPD_PORT || 6600));

	playlist.get("/", async (req, res) => {
		const playlistItems = await mpc.currentPlaylist.playlistInfo();

		const currentPlaylist: API.Song[] = playlistItems.map(item => ({
			path: item.path,
			title: item.title,
			artist: item.artist || "Unknown",
			duration: item.duration
		} as API.Song));

		res.send(currentPlaylist);
	});

	playlist.post("/", async (req, res) => {
		const {path, position}: {path: string, position: number | undefined} = req.body;

		try {
			await mpc.currentPlaylist.addId(path, position);
		} catch (e) {
			if(e.errorCode === 50) {
				res.status(404).send();
				return;
			} else {
				throw e;
			}
		}

		res.status(201).send();
	});

	playlist.delete("/", async (req, res) => {
		const position = Number(req.query.position);

		try {
			await mpc.currentPlaylist.delete(position);
		} catch (e) {
			if(e.errorCode === 2) {
				res.status(404).send();
				return;
			} else {
				throw e;
			}
		}

		res.status(204).send();
	});

	return playlist;
};