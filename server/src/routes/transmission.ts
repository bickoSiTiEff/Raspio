import Router from "express-promise-router";
import {Router as ExpressRouter} from "express";

export default async (): Promise<ExpressRouter> => {
	const transmission = Router();

	return transmission;
};