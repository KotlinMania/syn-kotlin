import re
import unittest
from pathlib import Path

from check_swift_tests import CASES, verify


def passing_log(alias=True):
    lines = []
    for name, description in CASES.items():
        suffix = " (aka '" + name + "()')" if alias else ""
        lines.append('✔ Test "' + description + '"' + suffix + ' passed after 0.001 seconds.')
    lines.append("✔ Test run with 6 tests in 1 suite passed after 0.002 seconds.")
    for module, mode in [("Syn", "5"), ("KotlinRuntimeSupport", "5"),
                         ("KotlinCoroutineSupport", "5"), ("SwiftTestHarnessTests", "6")]:
        lines.append("swift-frontend -module-name " + module + " -swift-version " + mode
                     + " -warnings-as-errors -target arm64-apple-macosx15.0")
    return "\n".join(lines)


class SwiftAcceptanceTest(unittest.TestCase):
    def test_declared_cases_are_exact(self):
        source = Path(__file__).resolve().parents[1] / "swift-test-harness/Tests/SwiftTestHarnessTests/SynExportTests.swift"
        found = re.findall(r'@Test\("([^"]+)"\)\s+func (\w+)\(', source.read_text())
        self.assertEqual({name: description for description, name in found}, CASES)

    def test_current_and_older_display_formats(self):
        for alias in [True, False]:
            self.assertEqual(verify(passing_log(alias))["actual_behavior_tests"], 6)

    def test_missing_case(self):
        with self.assertRaises(ValueError):
            verify("\n".join(passing_log().splitlines()[1:]))

    def test_duplicate_case(self):
        text = passing_log()
        with self.assertRaises(ValueError):
            verify(text + "\n" + text.splitlines()[0])

    def test_unexpected_extra_case(self):
        with self.assertRaises(ValueError):
            verify(passing_log() + '\n✔ Test "extra" passed after 0.001 seconds.')

    def test_wrong_total(self):
        with self.assertRaises(ValueError):
            verify(passing_log().replace("with 6 tests", "with 0 tests"))

    def test_failed_or_skipped(self):
        for status in ["failed", "skipped"]:
            with self.assertRaises(ValueError):
                verify(passing_log() + '\n✘ Test "extra" ' + status)

    def test_dependency_mode_and_warning_policy(self):
        for text in [passing_log().replace("-swift-version 5", "-swift-version 6"),
                     passing_log().replace("-warnings-as-errors", "")]:
            with self.assertRaises(ValueError):
                verify(text)

    def test_missing_authored_frontend(self):
        with self.assertRaises(ValueError):
            verify("\n".join(passing_log().splitlines()[:-1]))

    def test_authored_consumer_requires_mode6(self):
        with self.assertRaises(ValueError):
            verify(passing_log().replace("SwiftTestHarnessTests -swift-version 6",
                                         "SwiftTestHarnessTests -swift-version 5"))


if __name__ == "__main__":
    unittest.main()
