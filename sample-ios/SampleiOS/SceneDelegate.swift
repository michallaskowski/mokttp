//
//  SceneDelegate.swift
//  SampleiOS
//
//  Created by mlaskowski on 08/02/2020.
//  Copyright © 2020 Michal Laskowski. All rights reserved.
//

import UIKit
import SwiftUI
import mokttp
import sharedMock

class SceneDelegate: UIResponder, UIWindowSceneDelegate {

    var window: UIWindow?

    private var mockServer: HttpServer?
    private var commonMockServer: MockServer?

    func scene(_ scene: UIScene, willConnectTo session: UISceneSession, options connectionOptions: UIScene.ConnectionOptions) {
        let contentView = EnvironmentPickerView(didTap: { [weak self] environment in
            self?.presentContributors(in: environment)
        })
        let contentViewController = UIHostingController(rootView: contentView)

        // Use a UIHostingController as window root view controller.
        if let windowScene = scene as? UIWindowScene {
            let window = UIWindow(windowScene: windowScene)
            window.rootViewController = contentViewController
            self.window = window
            window.makeKeyAndVisible()
        }
    }

    private func presentContributors(in environment: Environment) {
        switch environment {
        case .mocked:
            setupMockServer()
        case .sharedMock:
            setupCommonMockServer()
        case .original:
            break
        }

        let contributorsView = ContributorsView(environment: environment)
        let contributorsViewController = UIHostingController(rootView: contributorsView)
        self.window?.rootViewController?.present(contributorsViewController, animated: true, completion: nil)
    }

    private func setupMockServer() {
        guard mockServer == nil else {
            return
        }

        mockServer = HttpServer()
        mockServer?.router = MockingRouter()
        mockServer?.start(port: 8080)
    }

    private func setupCommonMockServer() {
        guard commonMockServer == nil else {
            return
        }

        commonMockServer = MockServer()
        commonMockServer?.start(port: 8081)
    }
}

private final class MockingRouter: Router {
    func handleRequest(request: Request) -> Response {
        if request.method == "GET" && request.path?.starts(with: "/repos/") == true {
            let responseBody = try! JSONSerialization.data(withJSONObject: [
                ["login": "test", "contributions": 1]
            ], options: [])
            return Response(status: 200, headers: [:], body: responseBody, contentType: "application/json")
        } else {
            return Response(status: 404, headers: [:], body: nil, contentType: nil)
        }
    }
}
