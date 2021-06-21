import Router from "express-promise-router";
import {Router as ExpressRouter} from "express";
import {ChildProcess, spawn} from "child_process";

export default async (): Promise<ExpressRouter> => {
	const transmission = Router();
	let transmitter: ChildProcess | undefined = undefined;

	transmission.post("/start", (req, res) => {
		if (transmitter !== undefined) {
			res.send(400).send();
			return;
		}
		
		const frequency = Number(req.query.frequency);

		console.log("Launching transmitter on frequency " + frequency);
		transmitter = spawn("/bin/bash", ["-c", `sudo arecord -fS16_LE -r 22050 -Dplughw:0,1 -c 1 - | sudo /raspio/fm_transmitter -f ${frequency} -d 255 -`]);
		transmitter.stdout?.on("data", data => console.log("[TRANS-OUT] " + data));
		transmitter.stderr?.on("data", data => console.error("[TRANS-ERR] " + data));
		transmitter.on("close", code => console.log(`[TRANS] Transmitter stopped with code ${code}.`));
	});
	
	transmission.post("/stop", (req, res) => {
		if(transmitter === undefined) {
			res.send(400).send();
			return;
		}

		console.log("Killing transmitter!");

		transmitter.kill();
	});

	return transmission;
};