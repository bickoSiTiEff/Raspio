import {Router} from "express";
import {Song} from "../models/Song";
import {mockLibrary} from "./library";

const playlist = Router();

const mockPlaylist: Song[] = [
	mockLibrary[0]
];

playlist.get("/", (req, res) => {
	res.send(mockPlaylist);
});

playlist.post("/", (req, res) => {
	const {path, position}: { path: string, position?: number } = req.body;

	const song = mockLibrary.find(i => i.path == path);
	if (song === undefined) {
		res.status(404).send();
		return;
	}

	if (position === undefined)
		mockPlaylist.push(song);
	else
		mockPlaylist.splice(position, 0, song);
	console.log("[MOCK] Add!");
	res.status(201).send();
});

playlist.delete("/", (req, res) => {
	const position = Number(req.query.position);

	if (mockPlaylist[position] === undefined) {
		res.status(404).send();
		return;
	}

	mockPlaylist.splice(position, 1);
	res.status(204).send();
});

export default playlist;