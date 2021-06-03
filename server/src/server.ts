import express from "express";
import * as OpenApiValidator from "express-openapi-validator";
import cors from "cors";
import {json} from "body-parser";
import router from "./router";

const startServer = async () => {
	const PORT = process.env.PORT || 3000;

	const app = express();

	app.use(cors());
	app.use(json());

	app.use(
		OpenApiValidator.middleware({
			apiSpec: process.env.API_SPEC || "../api-specification.yaml",
			validateApiSpec: true,
			validateRequests: true,
			validateResponses: true
		})
	);

	app.use("/", await router());

	// eslint-disable-next-line @typescript-eslint/no-unused-vars, @typescript-eslint/no-explicit-any
	app.use((err: { status: number, message: string, errors: any }, req: express.Request, res: express.Response, _next: express.NextFunction) => {
		console.error(err);
		res.status(err.status || 500).json({
			message: err.message,
			errors: err.errors
		});
	});

	app.listen(PORT, () => {
		console.log(`Server is running on http://localhost:${PORT}!`);
	});
};

console.log("Starting server...");
startServer()
	.then(() => console.log("Finished initialization!"))
	.catch(e => console.error("Error during initialization!", e));