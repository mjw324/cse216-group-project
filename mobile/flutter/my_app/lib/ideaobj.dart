// The object representation of an idea.
// The idea object uses the data from the JSON response received from backend
class IdeaObj {
  // The int representation of the idea id
  final int id;

  // The String representation of the idea title
  final String title;

  // The String representation of the idea message
  final String message;

  // The int representation of the amount of votes. This is not final because vote count can change
  int votes;

  String userId;


  // The String representation of the amount of votes
  // Possible technical debt - this is a question of type Date vs type String
  //final String createdAt;

  // This attribute will need to change when adding functionality for multiple users.
  // This tracks if the current user has upvoted (1), downvoted (-1), or not voted (0) on an idea
  int userVotes;

  IdeaObj(
      {required this.id,
      required this.title,
      required this.message,
      required this.votes,
      required this.userId,
      required this.userVotes});

  factory IdeaObj.fromJson(Map<String, dynamic> json) {
    // Map's String dynamic pair is the JSON key value pair
    // The following is what this idea factory receives as input
    // {'mId':INT, 'mTitle':STRING, mMessage:STRING, mVotes:INT, mCreatedAt:STRING}
    return IdeaObj(
      // Format - value = json['key']
      id: json['mId'],
      title: json['mTitle'],
      message: json['mMessage'],
      votes: json['mVotes'],
      userId: json['mUserId'],
      userVotes: 0,
    );
  }
}
