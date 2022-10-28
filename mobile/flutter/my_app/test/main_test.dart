// Import test pkg and main.dart
import 'package:my_app/ideapage.dart';
import 'package:my_app/main.dart';
import 'package:test/test.dart';
import 'package:my_app/ideaobj.dart';

void main() {
  test("IdeaObj class should accurately capture idea attributes", () {
    // Arrange
    int id = 123;
    String title = "Hello";
    String message = "World";
    int votes = 50;
    String createdAt = "Nov 30, 2022, 5:19:48 PM";
    int userVotes = 1;
    String userId = '1234';
    // Act
    IdeaObj idea = IdeaObj(
        id: id,
        title: title,
        message: message,
        votes: votes,
        //createdAt: createdAt,
        userId: userId,
        userVotes: userVotes);
    // Assert
    expect(idea.id, id);
    expect(idea.title, title);
    expect(idea.message, message);
    expect(idea.votes, votes);
    //expect(idea.createdAt, createdAt);
    expect(idea.userVotes, userVotes);
  });
}
