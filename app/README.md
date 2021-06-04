# Raspio Android App

The Android app for Raspio will allow you to fully control the songs that are played via the selected Raspberry Pi server running the Raspio image.

## API Code Generation with OpenAPI KGen

We use a gradle plugin called [OpenAPI KGen](https://github.com/kroegerama/openapi-kgen) to generate API interfaces in order to communicate with the API through [Retrofit](https://github.com/square/retrofit).

It uses the `api-specification.yaml` file in the root directory to generate 4 interfaces during the gradle build which then contain all methods specified in the [API specification](https://docs.bickositieff.me).

