//
//  ContributorsView.swift
//  SampleiOS
//
//  Created by Michał Laskowski on 18/03/2020.
//  Copyright © 2016 Michał Laskowski. All rights reserved.
//

import Foundation

import Foundation
import SwiftUI
import Combine

struct Contributor: Codable {
    let login: String
    let contributions: Int
}

protocol ContributorsProviding {
    func contributors(owner: String, repo: String) -> AnyPublisher<[Contributor], Error>
}

private class ContributorsProvider: ContributorsProviding {

    private let baseUrl: URL

    init(baseUrl: URL) {
        self.baseUrl = baseUrl
    }

    convenience init(environment: Environment, override: String? = UserDefaults.standard.string(forKey: "contributors_url")) {
        if let override = override {
            self.init(baseUrl: URL(string: override)!)
        } else {
            self.init(baseUrl: URL(string: environment.rawValue)!)
        }
    }

    func contributors(owner: String, repo: String) -> AnyPublisher<[Contributor], Error> {
        let url = baseUrl.appendingPathComponent("/repos/\(owner)/\(repo)/contributors")
        return URLSession.shared.dataTaskPublisher(for: url).tryMap { (data, _) -> [Contributor] in
            try JSONDecoder().decode([Contributor].self, from: data)
        }.eraseToAnyPublisher()
    }
    // not used, but kept if we need to remove Combine (for iOS < 13)
    func contributors(owner: String, repo: String,
                      callback: @escaping ([Contributor]) -> Void) {
        let url = baseUrl.appendingPathComponent("/repos/\(owner)/\(repo)/contributors")

        let request = URLSession.shared.dataTask(with: url) { (data, response, error) in
            if let error = error {
                assertionFailure(error.localizedDescription)
                callback([])
                return
            }

            guard let data = data else {
                assertionFailure("Missing data but no error")
                callback([])
                return
            }

            do {
                let decodedResponse = try JSONDecoder().decode([Contributor].self, from: data)
                callback(decodedResponse)
            } catch {
                assertionFailure("Failed to decode response: \(error.localizedDescription)")
                callback([])
            }
        }

        request.resume()
    }
}

// you can treat it as a view model
final class ContributorsResource: ObservableObject {
    @Published var contributors = "Loading..."

    private let contributorsProvider: ContributorsProviding

    init(contributorsProvider: ContributorsProviding) {
        self.contributorsProvider = contributorsProvider
    }

    private var cancellables = Set<AnyCancellable>()

    func getContributors() {
        contributorsProvider.contributors(owner: "michallaskowski", repo: "kuiks")
        .map { contributors in
            contributors.map {
                $0.login
        }.joined(separator: ", ")
        }.replaceError(with: "Error!")
        .receive(on: RunLoop.main)
        .sink(receiveValue: { [weak self] in
            self?.contributors = $0
        })
        .store(in: &cancellables)
    }
}

struct ContributorsView: View {

    @ObservedObject private var resource: ContributorsResource

    init(environment: Environment) {
        resource = ContributorsResource(contributorsProvider: ContributorsProvider(environment: environment))
    }

    var body: some View {
        Text(resource.contributors).accessibility(identifier: "label")
            .onAppear {
                self.resource.getContributors()
        }

    }
}

struct ContributorsView_Previews: PreviewProvider {
    static var previews: some View {
        ContributorsView(environment: .mocked)
    }
}
