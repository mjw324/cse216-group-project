// The object representation of an profile.
// The profile object uses the data from the JSON response received from backend
class ProfileObj {
  
  // The String representation of the String username
  late final String username;

  // The String representation of the email
  final  String email;

  // The String representation of the Gender Identity
  final String GI;
  // The String representation of the Sexual Orientation
  final String SO;
  // The String representation of the note
  final String note;





  ProfileObj(
      {required this.username,
      required this.email,
      required this.GI,
      required this.SO,
      required this.note});

  factory ProfileObj.fromJson(Map<String, dynamic> json) {
    // Map's String dynamic pair is the JSON key value pair
    // The following is what this profile factory receives as input
    // {'mUsername':STRING, 'mEmail':STRING, 'mGI':STRING, 'mSO':STRING, 'mNote':STRING}
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
