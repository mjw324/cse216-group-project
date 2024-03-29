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
  //This String representation of the userId associated with the post
  String userId;
  //This string representation of the username associated with the post
  String username;

  String link;


  // This tracks if the current user has upvoted (1), downvoted (-1), or not voted (0) on an idea
  int userVotes;

  IdeaObj(
      {required this.id,
      required this.title,
      required this.message,
      required this.link,
      required this.votes,
      required this.userId,
      required this.userVotes,
      required this.username});

  factory IdeaObj.fromJson(Map<String, dynamic> json) {
    // Map's String dynamic pair is the JSON key value pair
    // The following is what this idea factory receives as input
    // {'mId':INT, 'mTitle':STRING, mMessage:STRING, mVotes:INT, mCreatedAt:STRING}
    return IdeaObj(
      // Format - value = json['key']
      id: json['mId'],
      title: json['mTitle'],
      message: json['mMessage'],
      link: json['mLink'],
      votes: json['mVotes'],
      userId: json['mUserId'],
      userVotes: 0,
      username: json['mUsername'] ??'No username',
    );
  }
}
