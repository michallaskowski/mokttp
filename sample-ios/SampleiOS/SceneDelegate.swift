//
//  SceneDelegate.swift
//  SampleiOS
//
//  Created by mlaskowski on 08/02/2020.
//  Copyright © 2020 Michal Laskowski. All rights reserved.
//

import UIKit
import SwiftUI
import mockttp

class SceneDelegate: UIResponder, UIWindowSceneDelegate {

    var window: UIWindow?

    private var mockServer: HttpServer?

    func scene(_ scene: UIScene, willConnectTo session: UISceneSession, options connectionOptions: UIScene.ConnectionOptions) {
        // Use this method to optionally configure and attach the UIWindow `window` to the provided UIWindowScene `scene`.
        // If using a storyboard, the `window` property will automatically be initialized and attached to the scene.
        // This delegate does not imply the connecting scene or session are new (see `application:configurationForConnectingSceneSession` instead).

        // Create the SwiftUI view that provides the window contents.
        let contentView = ContentView(didTap: { [weak self] environment in
//            self?.presentList()
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

    private func presentSwiftUIList() {
        let listView = ListView(didTapBack: {
            self.window?.rootViewController?.dismiss(animated: true)
        })
        let listViewController = UIHostingController(rootView: listView)
        self.window?.rootViewController?.present(listViewController, animated: true, completion: nil)
    }

    private func presentList() {
        let listViewController = ListViewController()
        listViewController.didTapBack = {
            listViewController.dismiss(animated: true, completion: nil)
        }
        self.window?.rootViewController?.present(listViewController, animated: true, completion: nil)
    }

    private func presentContributors(in environment: Environment) {
        if environment == .mocked {
            setupMockServer()
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
