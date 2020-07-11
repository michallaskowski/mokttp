//
//  SampleiOSUITests.swift
//  SampleiOSUITests
//
//  Created by mlaskowski on 11/07/2020.
//  Copyright © 2020 Michal Laskowski. All rights reserved.
//

import XCTest
import mokttp

final class SampleiOSUITests: XCTestCase {

    private var httpServer: HttpServer!

    override func setUpWithError() throws {
        continueAfterFailure = false
        httpServer = HttpServer()
    }

    override func tearDownWithError() throws {
        httpServer.stop()
    }

    func testStubs() {
        let port: Int32 = Int32.random(in: 1025...10000)
        httpServer.router = MockRouter()
        httpServer.start(port: port)
        // UI tests must launch the application that they test.
        let app = XCUIApplication()
        app.launchArguments = ["-contributors_url", "http://localhost:\(port)"]
        app.launch()

        app.segmentedControls.buttons["original"].tap()
        app.buttons["contributors"].tap()

        XCTAssertTrue(app.staticTexts["xcuitest"].waitForExistence(timeout: 5.0))
    }
}

final class MockRouter: Router {
    func handleRequest(request: Request) -> Response {
        let data = try! JSONSerialization.data(withJSONObject: [
            ["login": "xcuitest", "contributions": 1]
        ], options: [])
        return Response(status: 200, headers: [:], body: data, contentType: "application/json")
    }
}
