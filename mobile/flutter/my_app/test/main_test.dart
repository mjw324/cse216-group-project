// Import test pkg and main.dart
import 'package:my_app/ideapage.dart';
import 'package:my_app/main.dart';
import 'package:my_app/profileobj.dart';
import 'package:test/test.dart';
import 'package:my_app/ideaobj.dart';
import 'package:my_app/profileobj.dart';
import 'package:my_app/commentobj.dart';

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

  test("ProfileObj class should accurately capture idea attributes", () {
    // Arrange
    
    String username = 'abc123';
    String email = 'abc123@gmail.com';
    String GI = 'GI';
    String SO = 'SO';
    String note = 'random note';



    ProfileObj profile = ProfileObj(
      username: username, 
      email: email, 
      GI: GI, 
      SO: SO, 
      note: note);



    // Assert
    expect(profile.username, username);
    expect(profile.email, email);
    expect(profile.GI, GI);
    expect(profile.SO, SO);
    expect(profile.note, note);
  });

    test("CommentObj class should accurately capture idea attributes", () {
    // Arrange
    int postId = 123;
    int commentId = 123;
    String comments = 'comment';
    String userId = '1234';




    CommentObj comment = CommentObj(
      postId: postId, 
      commentId: commentId, 
      userId: userId, 
      comment: comments);

    // Assert
    expect(comment.postId, postId);
    expect(comment.commentId, commentId);
    expect(comment.comment, comments);
    expect(comment.userId, userId);
  });



}
