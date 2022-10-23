import 'package:http/http.dart' as http;
import 'dart:developer' as developer;
import 'dart:convert';
import 'dart:io';
import 'ideaobj.dart';


//Post-> sends the id_token to backend
Future<String> sendToken(String? id_token) async {
  final response = await http.post(
    Uri.parse('https://whispering-sands-78580.herokuapp.com/signin/mToken'),
    body: jsonEncode(<String?, String?>{'token': id_token}),
  );
  var res = jsonDecode(response.body);
  return res['mStatus'];
}

/*Returns the username of the user id_token
Future<String> fetchUsername(String? id_token) async {
  final response = await http.get(
      Uri.parse('https://whispering-sands-78580.herokuapp.com/profile/username/$id_token'));
  var res = jsonDecode(response.body);
  print(res);
  return res['mData'];
}

//Returns the email of the user id_token
Future<String> fetchEmail(String? id_token) async {
  final response = await http.get(
      Uri.parse('https://whispering-sands-78580.herokuapp.com/profile/email/$id_token'));
  var res = jsonDecode(response.body);
  print(res);
  return res['mData'];
}

//Adds SI of the user id_token
Future<String> addSI(String? id_token, String SI) async {
  final response = await http.post(
      Uri.parse('https://whispering-sands-78580.herokuapp.com/profile/SI'),
      body: jsonEncode(<String, String?>{'id_token': id_token, 'SI': SI}),
  );
  var res = jsonDecode(response.body);
  return res['mMessage'];
}

Future<String> addGO(String? id_token, String GO) async {
  final response = await http.post(
      Uri.parse('https://whispering-sands-78580.herokuapp.com/profile/GO'),
      body: jsonEncode(<String, String?>{'id_token': id_token, 'GO': GO}),
  );
  var res = jsonDecode(response.body);
  return res['mMessage'];
}
Future<String> addnote(String? id_token, String note) async {
  final response = await http.post(
      Uri.parse('https://whispering-sands-78580.herokuapp.com/profile/note'),
      body: jsonEncode(<String, String?>{'id_token': id_token, 'note': note}),
  );
  var res = jsonDecode(response.body);
  return res['mMessage'];
}
*/
// POST /messages route given a new idea with title and messages, should return String of new ID
Future<String> addIdea(String title, String message) async {
  final response = await http.post(
    Uri.parse('https://whispering-sands-78580.herokuapp.com/messages'),
    body: jsonEncode(<String, String>{'mTitle': title, 'mMessage': message}),
  );
  var res = jsonDecode(response.body);
  return res['mMessage'];
}

// Returns an IdeaObj from GET /messages/:id given its id
Future<IdeaObj> fetchIdea(int idx) async {
  final response = await http.get(
      Uri.parse('https://whispering-sands-78580.herokuapp.com/messages/$idx'));
  var res = jsonDecode(response.body);
  print(res);
  return res['mData'];
}


// Perform PUT on /messages/:id/[upvotes or downvotes]
Future<int> voteIdea(int idx, bool isUpvote, int voteCount) async {
  // isUpvote boolean determines which vote route to HTTP PUT
  String vote = isUpvote ? 'upvotes' : 'downvotes';
  // ignore: prefer_typing_uninitialized_variables
  final response;
  if (voteCount > 1) {
    // If front ends needs to increment/decrement votes more than once, we specify the route with an addition /voteCount
    response = await http.put(Uri.parse(
        'https://whispering-sands-78580.herokuapp.com/messages/$idx/$vote/$voteCount'));
  } else {
    response = await http.put(Uri.parse(
        'https://whispering-sands-78580.herokuapp.com/messages/$idx/$vote'));
  }

  // res (response) should decode two key value pairs: mStatus and mData
  var res = jsonDecode(response.body);
  print(res);
  return res['mData'];
}

// Perform GET on /ideas route, retrieves JSON response and converts it into a list of Idea Objects
Future<List<IdeaObj>> fetchIdeas() async {
  final response = await http
      .get(Uri.parse('https://whispering-sands-78580.herokuapp.com/messages'));

  if (response.statusCode == 200) {
    // If the server did return a 200 OK response, then parse the JSON.
    final List<IdeaObj> returnData;
    var res = jsonDecode(response.body);
    // Coded out lines are to debug/print response
    //developer.log(response.body);
    //developer.log('json decode: $res');
    //developer.log('The keys are: ${res.keys}');
    //developer.log('The values are: ${res.values}');
    var ideas = res['mData'];
    if (ideas is List) {
      returnData = ideas.map((x) => IdeaObj.fromJson(x)).toList();
    } else if (ideas is Map) {
      returnData = <IdeaObj>[IdeaObj.fromJson(res as Map<String, dynamic>)];
    } else {
      developer
          .log('ERROR: Unexpected json response type (was not a List or Map).');
      returnData = List.empty();
    }
    //print(res);
    return returnData;
  } else {
    // If the server did not return a 200 OK response,
    // then throw an exception.
    throw Exception('Did not receive success status code from request.');
  }
}

// Returns how many votes an idea has. This could probably be removed given the refactoring
Future<String> voteCounter(int idx) async {
  final response = await http.get(
      Uri.parse('https://whispering-sands-78580.herokuapp.com/messages/$idx'));
  // res (response) should decode two key value pairs: mStatus and mData. mData has the idea key value pairs we need
  var res = jsonDecode(response.body);
  IdeaObj idea = IdeaObj.fromJson(res['mData']);
  return (idea.votes).toString();
}
