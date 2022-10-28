// The object representation of an idea.
// The idea object uses the data from the JSON response received from backend
class CommentObj {
  // The int representation of the idea id
  final int postId;
  
  //the commentId
  final String commentId;

  // The String representation of the user id
  final String userId;

  // The String representation of the idea message
  final String comment;
 




  CommentObj(
      {required this.postId,
      required this.commentId,
      required this.userId,
      required this.comment});

  factory CommentObj.fromJson(Map<String, dynamic> json) {
    // Map's String dynamic pair is the JSON key value pair
    return CommentObj(
      // Format - value = json['key']
      postId: json['mId'],
      commentId: json['mCommentId'],
      userId: json['mUserId'],
      comment: json['mComment'],
    );
  }
}
