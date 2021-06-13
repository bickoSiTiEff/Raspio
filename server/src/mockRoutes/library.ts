import Router from "express-promise-router";
import {raw} from "body-parser";
import {Song} from "../models/Song";
import {parseBuffer} from "music-metadata";

const library = Router();

export const mockLibrary: Song[] = [
	{
		path: "stefan-rauch/15er-steyr.mp3",
		title: "15er Steyr",
		artist: "Stefan Rauch",
		duration: 192
	},
	{
		path: "sääftig/dirndl-weed.wav",
		title: "Dirndl Weed",
		artist: "SÄÄFTIG",
		duration: 132
	}
];

library.get("/", (req, res) => {
	res.send(mockLibrary);
});

library.post("/", raw({
	limit: "1GB",
	type: "audio/mp3"
}), async (req, res) => {
	const meta = await parseBuffer(req.body, "audio/mp3", {
		skipCovers: true
	});
	const duration = Math.round(meta.format.duration as number);
	console.log(`[MOCK] Uploaded MP3 file of song "${meta.common.title}" from artist "${meta.common.artist}" with a duration of ${duration} seconds.`);
	mockLibrary.push({
		path: `${meta.common.artist}/${meta.common.title}.mp3`,
		title: meta.common.title as string,
		artist: meta.common.artist as string,
		duration: duration
	});
	res.status(201).send();
});

export default library;