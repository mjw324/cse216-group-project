// Import test pkg and main.dart
import 'package:my_app/ideapage.dart';
import 'package:my_app/main.dart';
import 'package:test/test.dart';

void main() {
  test("IdeaObj class should accurately capture idea attributes", () {
    // Arrange
    int id = 123;
    String title = "Hello";
    String message = "World";
    int votes = 50;
    String createdAt = "Nov 30, 2022, 5:19:48 PM";
    // Act
    final IdeaObj idea = IdeaObj(
        id: id,
        title: title,
        message: message,
        votes: votes,
        createdAt: createdAt);
    // Assert
    expect(idea.id, id);
    expect(idea.title, title);
    expect(idea.message, message);
    expect(idea.votes, votes);
    expect(idea.createdAt, createdAt);
  });
}
