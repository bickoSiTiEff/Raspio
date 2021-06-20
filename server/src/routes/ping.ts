import {Router as ExpressRouter} from "express";
import Router from "express-promise-router";

export default async (): Promise<ExpressRouter> => {
	const ping = Router();

	ping.get("/", (req, res) => {
		res.send({
			message: "success"
		});
	});

	return ping;
};