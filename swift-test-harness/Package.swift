// swift-tools-version: 6.0
import PackageDescription
import Foundation

let exportBuildRoot = ProcessInfo.processInfo.environment["KOTLINMANIA_SWIFT_EXPORT_BUILD"] ?? "../build"

let package = Package(
    name: "SwiftTestHarness",
    platforms: [
        .macOS(.v15),
    ],
    dependencies: [
        .package(name: "Syn", path: "\(exportBuildRoot)/SPMPackage/macosArm64/Debug")
    ],
    targets: [
        .testTarget(
            name: "SwiftTestHarnessTests",
            dependencies: [
                .product(name: "SynLibrary", package: "Syn")
            ],
            linkerSettings: [
                .unsafeFlags([
                    "-L", "\(exportBuildRoot)/swift-test",
                    "-lSyn",
                ]),
            ]
        ),
    ]
)
