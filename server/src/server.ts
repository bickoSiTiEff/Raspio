import express from "express";
import * as OpenApiValidator from "express-openapi-validator";
import cors from "cors";
import {json} from "body-parser";
import router from "./router";

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

app.use("/", router);

app.use((err: any, req: express.Request, res: express.Response, _next: express.NextFunction) => {
	console.error(err);
	res.status(err.status || 500).json({
		message: err.message,
		errors: err.errors
	});
});

app.listen(PORT, () => {
	console.log(`Server is running on http://localhost:${PORT}!`);
});