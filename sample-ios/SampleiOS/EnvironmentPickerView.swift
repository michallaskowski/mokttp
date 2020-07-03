//
//  ContentView.swift
//  SampleiOS
//
//  Created by mlaskowski on 08/02/2020.
//  Copyright © 2020 Michal Laskowski. All rights reserved.
//

import SwiftUI

// navigation works once on simulators till xcode11.4, and it is back on 11.5
// https://stackoverflow.com/questions/59279176/navigationlink-works-only-for-once

enum Environment: String, Hashable {
    case mocked = "http://localhost:8080"
    case sharedMock = "http://localhost:8081"
    case original = "http://api.github.com"
}

struct EnvironmentPickerView: View {
    @State private var environment: Environment = .mocked
    var didTap: ((Environment) -> Void)?

    var body: some View {
        VStack {
            Picker(selection: $environment, label: Text("Choose environment")) {
                Text("mocked").tag(Environment.mocked)
                Text("shared mock").tag(Environment.sharedMock)
                Text("original").tag(Environment.original)
            }.pickerStyle(SegmentedPickerStyle())

            Button(action: {
                self.didTap?(self.environment)
            }, label: {
                Text("Go make that call")
            }).accessibility(identifier: "show_list")
        }
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        EnvironmentPickerView()
    }
}
