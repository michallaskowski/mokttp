# Mokttp

## What is it

Mokttp is Kotlin Multiplatform wrapper around libraries that are creating a local web server. On Android it wraps [OkHttp's MockWebServer](https://github.com/square/okhttp/tree/master/mockwebserver/), on iOS it's [GCDWebServer](https://github.com/swisspol/GCDWebServer).

It is not a desired and final solution, since there is an additional setup needed on Kotlin Native for GCDWebServer, and the ideal approach would be to use [Ktor Server when it supports platforms other than JVM](https://github.com/ktorio/ktor/issues/571).

## When it can be useful

The main goal is to enable mocking and stubbing server response for UI testing, in combination with [Kuiks](https://github.com/michallaskowski/kuiks). But if you find having a local HTTP server useful in your app, feel free to use it.

## How to build and launch samples

Clone the repo first.
The samples contain simple segmented control/radio buttons and a button to make a call to real or mocked servers. Currently it lists contributors from the [Kuiks](https://github.com/michallaskowski/kuiks) project, which can be also provided by a local HTTP server.

## Contributing

Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change. 
Please make sure to update samples as appropriate.

## Roadmap

* Make sure it works well with Kuiks, and enhance if needs arise.
