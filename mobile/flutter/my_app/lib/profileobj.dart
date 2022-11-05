// The object representation of an idea.
// The idea object uses the data from the JSON response received from backend
class ProfileObj {
  
  // The int representation of the idea id
  late final String username;

  // The String representation of the idea title
  final  String email;

  // The String representation of the idea message
  final String GI;

  final String SO;
  
  final String note;





  ProfileObj(
      {required this.username,
      required this.email,
      required this.GI,
      required this.SO,
      required this.note});

  factory ProfileObj.fromJson(Map<String, dynamic> json) {
    // Map's String dynamic pair is the JSON key value pair
    // The following is what this idea factory receives as input
    // {'mId':INT, 'mTitle':STRING, mMessage:STRING, mVotes:INT, mCreatedAt:STRING}
    return ProfileObj(
      // Format - value = json['key']
      username: json['mUsername'],
      email: json['mEmail'],
      GI: json['mGI'],
      SO: json['mSO'],
      note: json['mNote'],
    );
  }
}
