import {Router} from "express";
import {Song} from "../models/Song";

const library = Router();

library.get("/", (req, res) => {
	res.send([
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
	] as Song[]);
})

export default library;