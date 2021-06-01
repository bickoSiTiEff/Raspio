import {Router} from "express";

import mockTransmission from "./mockRoutes/transmission";
import mockLibrary from "./mockRoutes/library";
import mockPlaylist from "./mockRoutes/playlist";
import mockPlayback from "./mockRoutes/playback";

import transmission from "./routes/transmission";
import library from "./routes/library";
import playlist from "./routes/playlist";
import playback from "./routes/playback";

const router = Router();

if (process.env.MOCK === "true") {
	router.use("/transmission", mockTransmission);
	router.use("/songs", mockLibrary);
	router.use("/playlist", mockPlaylist);
	router.use("/playback", mockPlayback);
} else {
	router.use("/transmission", transmission);
	router.use("/songs", library);
	router.use("/playlist", playlist);
	router.use("/playback", playback);
}

export default router;