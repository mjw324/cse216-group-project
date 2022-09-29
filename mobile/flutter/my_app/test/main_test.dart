// Import test pkg and main.dart
import 'package:my_app/main.dart';
import 'package:test/test.dart';

void main() {
  test("Add button on press should display a text field", () {
    // Arrange

    // Act

    // Assert
  });
  test("Submit button on press should add idea to idea list", () {
    // Arrange

    // Act

    // Assert
  });
  test("Upvote button on press should increment vote counter on idea", () {
    // Arrange

    // Act

    // Assert
  });
  test(
      "Downvote button on press should decrement vote counter on idea (votes > 1)",
      () {
    // Arrange

    // Act

    // Assert
  });
  test(
      "Downvote button on press should NOT decrement vote counter on idea with no votes",
      () {
    // Arrange

    // Act

    // Assert3
  });
}
