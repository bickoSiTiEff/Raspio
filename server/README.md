# Raspio API Server

The server component of Raspio provides a REST API that is used by the Android App to control the currently playing songs and manage the radio transmission. It interacts with the [MPD server](https://github.com/MusicPlayerDaemon/MPD) that will be running on the Raspberry Pi.

## Configuration

The following environment variables can be used to configure various server settings:

| Variable Name | Default value               | Description                                              |
| ------------- | --------------------------- | -------------------------------------------------------- |
| `MOCK`        | `false`                     | See section [Mock-Mode](#mock-mode)                      |
| `MPD_HOST`    | `localhost`                 | Hostname of the MPD server to connect to                 |
| `MPD_PORT`    | `6600`                      | Port of the MPD server to connect to                     |
| `PORT`        | `3000`                      | Port that the REST API server should listen on           |
| `API_SPEC`    | `../api-specification.yaml` | Path to the `yaml` file containing the API specification |

## Mock-Mode

If the server is launched with the environment variable `MOCK` set to `true` or alternatively with `yarn run mock`, then different handlers for the API calls are used that don't require a MPD server to be running. The server state (e.g. the current playlist) is being simulated. This is useful because you don't have to install MPD on your development machine to test the Android App.

## Request validation with `express-openapi-validator`

A library called `express-openapi-validator` is being used on the server to ensure that each request conforms to our API specification and to provide a meaningful error message when a request doesn't. This avoids a lot of boilerplate code for request validation in the individual API handlers.