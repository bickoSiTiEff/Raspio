import Router from "express-promise-router";
import {MPC, Song} from "mpc-js";
import * as API from "../models/Song";

export default async () => {
	const library = Router();

	const mpc = new MPC();
	await mpc.connectTCP(process.env.MPD_HOST || "localhost", Number(process.env.MPD_PORT || 6600));

	library.get("/", async (req, res) => {
		const database = await mpc.database.listAllInfo();
		
		const songs = database
			.filter(entry => entry.isSong())
			.map(i => i as Song) // This has been checked in the previous step, but the compiler is dumb
			.map((song: Song) => ({
				path: song.path,
				title: song.title,
				artist: song.artist || song.composer || song.performer || "Unknown",
				duration: song.duration
			} as API.Song));

		res.send(songs);
	});

	return library;
};