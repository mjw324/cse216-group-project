// The object representation of an idea.
// The idea object uses the data from the JSON response received from backend

class UserObj {
  

  // The String representation of the idea title
  final String username;

  final String name;
  final String email;

  

  UserObj(
      {required this.name,
      required this.email,
      required this.username,});


}
