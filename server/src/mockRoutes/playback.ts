import {Router} from "express";

const playback = Router();

const playbackState = {
	"duration": 200,
	"elapsed": 0,
	"consume": true,
	"shuffle": false,
	"repeat": false,
	"currentlyPlayingIndex": 0,
	"state": "play",
	"volume": 100,
	"crossfade": 0
};

playback.get("/", (req, res) => {
	res.status(200).send(playbackState);
});

playback.post("/play", (req, res) => {
	console.log("[MOCK] Play!");
	playbackState.state = "play";
	res.status(204).send();
});

playback.post("/pause", (req, res) => {
	console.log("[MOCK] Pause!");
	playbackState.state = "pause";
	res.status(204).send();
});

playback.post("/previous", (req, res) => {
	console.log("[MOCK] Previous!");
	playbackState.state = "play";
	res.status(204).send();
});

playback.post("/next", (req, res) => {
	console.log("[MOCK] Next!");
	playbackState.elapsed = 0;
	res.status(204).send();
});

playback.post("/stop", (req, res) => {
	console.log("[MOCK] Stop!");
	playbackState.state = "stop";
	res.status(204).send();
});

playback.post("/seek", (req, res) => {
	const position = Number(req.query.time);
	console.log(`[MOCK] Seek to ${position}`);
	playbackState.elapsed = position;
	res.status(204).send();
});

playback.post("/consume", (req, res) => {
	const consume = Boolean(req.query.consume);
	console.log(`[MOCK] Turn consume mode ${consume ? "on" : "off"}.`);
	playbackState.consume = consume;
	res.status(204).send();
});

playback.post("/crossfade", (req, res) => {
	const amount = Number(req.query.amount);
	console.log(`[MOCK] Set crossfade amount to ${amount}.`);
	playbackState.crossfade = amount;
	res.status(204).send();
});

playback.post("/shuffle", (req, res) => {
	const shuffle = Boolean(req.query.shuffle);
	console.log(`[MOCK] Playlist will ${shuffle ? "" : "not "} be shuffled.`);
	playbackState.shuffle = shuffle;
	res.status(204).send();
});

playback.post("/repeat", (req, res) => {
	const repeat = Boolean(req.query.repeat);
	console.log(`[MOCK] Repeat mode is ${repeat ? "enabled" : "disabled"}.`);
	playbackState.repeat = repeat;
	res.status(204).send();
});

playback.post("/volume", (req, res) => {
	const volume = Number(req.query.volume);
	console.log(`[MOCK] Volume is set to ${volume}.`);
	playbackState.volume = volume;
	res.status(204).send();
});

export default playback;