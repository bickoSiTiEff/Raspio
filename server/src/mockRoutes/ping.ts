import {Router} from "express";

const ping = Router();

ping.get("/", (req, res) => {
	res.send({
		message: "success"
	});
});

export default ping;