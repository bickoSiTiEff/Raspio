import Router from "express-promise-router";
import {Router as ExpressRouter} from "express";

import mockTransmission from "./mockRoutes/transmission";
import mockLibrary from "./mockRoutes/library";
import mockPlaylist from "./mockRoutes/playlist";
import mockPlayback from "./mockRoutes/playback";

import transmission from "./routes/transmission";
import library from "./routes/library";
import playlist from "./routes/playlist";
import playback from "./routes/playback";

export default async (): Promise<ExpressRouter> => {
	const router = Router();

	if (process.env.MOCK === "true") {
		router.use("/transmission", mockTransmission);
		router.use("/songs", mockLibrary);
		router.use("/playlist", mockPlaylist);
		router.use("/playback", mockPlayback);
	} else {
		router.use("/transmission", await transmission());
		router.use("/songs", await library());
		router.use("/playlist", await playlist());
		router.use("/playback", await playback());
	}

	return router;
};