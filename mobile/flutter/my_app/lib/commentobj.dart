// The object representation of an idea.
// The idea object uses the data from the JSON response received from backend
class CommentObj {
  // The int representation of the post id
  final int postId;
  
  //the int representation of commentId
  final int commentId;

  // The String representation of the user id associated with the comment
  final String userId;

  // The String representation of the comment
   String comment;

   //The String representation of the username
   String username;

 




  CommentObj(
      {required this.postId,
      required this.commentId,
      required this.userId,
      required this.comment,
      required this.username});

  factory CommentObj.fromJson(Map<String, dynamic> json) {
    // Map's String dynamic pair is the JSON key value pair
    return CommentObj(
      // Format - value = json['key']
      postId: json['mPostId'],
      commentId: json['mCommentId'],
      userId: json['mUserId'],
      comment: json['mComment'],
      username: json['mUsername'] ??'No username'
    );
  }
}
