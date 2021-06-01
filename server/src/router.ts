import {Router} from "express";

import transmission from "./routes/transmission";
import library from "./routes/library";
import playlist from "./routes/playlist";
import playback from "./routes/playback";

const router = Router();

router.use("/transmission", transmission);
router.use("/songs", library);
router.use("/playlist", playlist);
router.use("/playback", playback);

export default router;