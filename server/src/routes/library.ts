import Router from "express-promise-router";
import {Router as ExpressRouter} from "express";
import {MPC, Song} from "mpc-js";
import * as API from "../models/Song";
import {raw} from "body-parser";
import {parseBuffer} from "music-metadata";
import {writeFile} from "fs/promises";

export default async (): Promise<ExpressRouter> => {
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
				duration: Math.round(song.duration as number)
			} as API.Song));

		res.send(songs);
	});

	library.post("/", raw({
		limit: "1GB",
		type: "audio/mp3"
	}), async (req, res) => {
		const meta = await parseBuffer(req.body, "audio/mp3", {
			skipCovers: true
		});

		if (meta.common.title === undefined || meta.common.artist === undefined || meta.format.duration === undefined) {
			console.error("Upload is missing metadata!");
			res.status(500).send();
			return;
		}

		const fileName = `/raspio/music/${Math.round(Math.random() * 10000)}.mp3`;
		console.log(`Saving "${meta.common.title}" to ${fileName}...`);

		await writeFile(fileName, req.body);

		console.log(`Saved ${fileName}.`);
	});

	return library;
};