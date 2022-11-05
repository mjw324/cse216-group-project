import 'package:http/http.dart' as http;
import 'package:my_app/commentobj.dart';
import 'dart:developer' as developer;
import 'dart:convert';
import 'dart:io';
import 'ideaobj.dart';
import 'profileobj.dart';
import 'dart:core';

class routes{
  static int sessionId = 1;
  static String user_id = ""; 
  static http.Client client = http.Client();
  static String email = "";
  static String name = "";
  static String note = "";
  static void userName(String email1){
    email.replaceAll(email, email1);
  }
  static Future<dynamic> sendRequest(String method, String path, {String? body}) async{
    print('now in sendRequest');
    final request = http.Request(method, Uri.parse('https://whispering-sands-78580.herokuapp.com$path'));
    print('now after Request');
    request.headers["Session-ID"] = sessionId.toString();
    //request.body = "{mSessionId :$sessionId}";
    print(request.headers);
    //print(request.body);
    if(body != null){
      request.body = body;
    }
    
    final response = await http.Response.fromStream(await client.send(request));
    print('response');
    print(response.headers);
    if(response.statusCode != 200){
      statusCode: response.statusCode; message: response.body;
    }
    return jsonDecode(response.body);
  }


  static Future<List<IdeaObj>> fetchPosts() async{
    //print('this is from fetch posts');
    print(routes.sessionId);
    final data = await sendRequest("GET", "/messages");
   // print('now after send request');
    print(data['mData']);
    
    final List<IdeaObj> returnData;
     var ideas = data['mData'];
    if (ideas is List) {
      returnData = ideas.map((x) => IdeaObj.fromJson(x)).toList();
    } else if (ideas is Map) {
      returnData = <IdeaObj>[IdeaObj.fromJson(data as Map<String, dynamic>)];
    } else {
      developer
          .log('ERROR: Unexpected json response type (was not a List or Map).');
      returnData = List.empty();
    }
    //print('res');
   // print(data);
    //print(returnData);
    return returnData;
  
  }
    static Future<List<ProfileObj>> fetchProfile() async{
    final data = await sendRequest("GET", '/profile');
    //print(data);
     final List<ProfileObj> returnData;
     var ideas = data['mData'];
      print(ideas);
     if(ideas is List){
      final posts = ideas.map((x) => ProfileObj.fromJson(x)).toList();
      return posts;
    }else if (ideas is Map) {
      returnData = <ProfileObj>[ProfileObj.fromJson(ideas as Map<String, dynamic>)];
    } else {
      //print('here 3');
      developer
          .log('ERROR: Unexpected json response type (was not a List or Map).');
      returnData = List.empty();
    }
    return returnData;

  }
   static Future<List<ProfileObj>> fetchProfileInfo(String id) async{
    print('in fetchProfile info: $id');
    final data = await sendRequest("GET", '/profile/$id');
    //print(data);
     final List<ProfileObj> returnData;
     var ideas = data['mData'];
      print(ideas);
     if(ideas is List){
      final posts = ideas.map((x) => ProfileObj.fromJson(x)).toList();
      return posts;
    }else if (ideas is Map) {
     // print('here 2');
      //print(data);
      returnData = <ProfileObj>[ProfileObj.fromJson(ideas as Map<String, dynamic>)];
    } else {
      //print('here 3');
      developer
          .log('ERROR: Unexpected json response type (was not a List or Map).');
      returnData = List.empty();
    }
   // print('here 4');
   email = returnData[0].email;
   name = returnData[0].username;
   note = returnData[0].note;
    print(returnData[0].email);
    return returnData;

  }
  static Future<List<CommentObj>> fetchComments(int id) async{
    print(id);
    final data = await sendRequest("GET", '/comments/$id');
    print("data:$data");
     final List<CommentObj> returnData;
     var ideas = data['mData'];
    if (ideas is List) {
      print('list');
      returnData = ideas.map((x) => CommentObj.fromJson(x)).toList();
    } else if (ideas is Map) {
      returnData = <CommentObj>[CommentObj.fromJson(data as Map<String, dynamic>)];
    } else {
      developer
          .log('ERROR: Unexpected json response type (was not a List or Map).');
      returnData = List.empty();
    }
    print('res');
    print(data);
    print(returnData);
    
    return returnData;
    

  }



  

}

  Future<String> addIdea(String title, String message) async {
  final response = await http.post(
    Uri.parse('https://whispering-sands-78580.herokuapp.com/messages'),
    body: jsonEncode(<String, String>{'mTitle': title, 'mMessage': message, 'mSessionId' :routes.sessionId.toString()}),
  );
  var res = jsonDecode(response.body);
  print(res);
  return res['mData'];

}
//Post-> sends the id_token to backend
Future<Map> sendToken(String? mToken) async {
  print('mToken');
  print(mToken);
  final response = await http.post(
    Uri.parse('https://whispering-sands-78580.herokuapp.com/signin'),
    body: jsonEncode(<String, String?>{'mToken': mToken}),
  );
  //developer.log(response.body);
  
  //print(response.body);
  var res = jsonDecode(response.body);
  /*
  developer.log('json decode: $res');
  developer.log('The keys are: ${res.keys}');
  developer.log('The values are: ${res.values}');
  print('hi');
  print(res);
   print('hi');
   */ 
  //print(res['mData']);
  routes.sessionId = int.parse(res['mData']['Session-ID'])!;
  routes.user_id = res['mData']['User-ID'];
  print('this is in sendIdtoken');
  print(res['mData']['Session-ID']);  
  return res['mData'];
}





// Returns an IdeaObj from GET /messages/:id given its id
Future<IdeaObj> fetchIdea(int idx, int sessionId) async {
  print('this is from the routes');
  print(sessionId);
  final response = await http.get(
      Uri.parse('https://whispering-sands-78580.herokuapp.com/messages/$idx'));
      body: jsonEncode(<String, int>{'mSessionId': sessionId });
  var res = jsonDecode(response.body);
  print(res);
  return res['mData'];
}


// Perform PUT on /messages/:id/[upvotes or downvotes]
Future<int> voteIdea(int idx, bool isUpvote, int voteCount) async {
  // isUpvote boolean determines which vote route to HTTP PUT
  String vote = isUpvote ? 'upvote' : 'downvote';
  // ignore: prefer_typing_uninitialized_variables
  final response;
  if (voteCount > 1) {
    // If front ends needs to increment/decrement votes more than once, we specify the route with an addition /voteCount
    response = await http.put(Uri.parse(
        'https://whispering-sands-78580.herokuapp.com/messages/$idx/$vote'),
        body: jsonEncode(<String, int?>{'mSessionId': routes.sessionId}),);
  } else {
    response = await http.put(Uri.parse(
        'https://whispering-sands-78580.herokuapp.com/messages/$idx/$vote'),
        body: jsonEncode(<String, int?>{'mSessionId': routes.sessionId}),);
  }


  // res (response) should decode two key value pairs: mStatus and mData
  var res = jsonDecode(response.body);
  print(res);
  return res['mData'];
}

Future<int> upVote(int idx) async{
  final response = await http.put(Uri.parse(
        'https://whispering-sands-78580.herokuapp.com/messages/$idx'),
        body: jsonEncode(<String, int?>{'mSessionId': routes.sessionId}),);
  var res = jsonDecode(response.body);
  print(res);
  return res['mData'];

}


// POST /messages route given a new idea with title and messages, should return String of new ID
Future<String> addComment(String comment, int id ) async {
  print("post ID: $id");
  final response = await http.post(
    Uri.parse('https://whispering-sands-78580.herokuapp.com/comment/$id'),
    body: jsonEncode(<String, String>{'mCommentId': '-1', 'mComment': comment, 'mSessionId' :routes.sessionId.toString()}),
  );
  var res = jsonDecode(response.body);
  print("res$res");
  print('userId');
  return res['mMessage'];

}
Future<String> editComment(String comment, int id, int commentId) async {
  print("post ID: $id");
  final response = await http.put(
    Uri.parse('https://whispering-sands-78580.herokuapp.com/comment'),
    body: jsonEncode(<String, dynamic>{'mCommentId': commentId,'mComment': comment, 'mSessionId' :routes.sessionId}),
  );
  print(commentId);
  var res = jsonDecode(response.body);
  print("res Edit$res");
  print('userId');
  return res['mMessage'];

}

Future<int> sendProfile(String username, String SO, String GI, String note) async{
   final response = await http.put(
    Uri.parse('https://whispering-sands-78580.herokuapp.com/profile'),
    body: jsonEncode(<String, String>{'mSessionId' :routes.sessionId.toString(), 'mGI': GI, 'mSO' : SO,  'mUsername': username.substring(0, username.indexOf(' and')),'mNote': note }),
  );
  var res = jsonDecode(response.body);
  print("res$res");
  return res['mData'];
}
// Perform GET on /ideas route, retrieves JSON response and converts it into a list of Idea Objects
Future<List<IdeaObj>> fetchIdeas(int SessionId) async {
  routes.sessionId = SessionId;
  print('this is from fetchideas');
  print(routes.sessionId);
  final response = await http.get(
    Uri.parse('https://whispering-sands-78580.herokuapp.com/messages'),
    //body: jsonEncode(<String?, int>{'mSessionId': SessionId}),
  );
  if (response.statusCode == 200) {
    // If the server did return a 200 OK response, then parse the JSON.
    final List<IdeaObj> returnData;
    var res = jsonDecode(response.body);
    // Coded out lines are to debug/print response
    //developer.log(response.body);
    //developer.log('json decode: $res');
    //developer.log('The keys are: ${res.keys}');
    //developer.log('The values are: ${res.values}');
    print('ideas');
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
    print('res');
    print(res);
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


// Future<String> sendSessionId(String? mSessionId) async {
//   final response = await http.post(
//     Uri.parse('https://whispering-sands-78580.herokuapp.com/signin/mSessionId'),
//     body: jsonEncode(<String?, String?>{'mSessionid': mSessionId}),
//   );
//   var res = jsonDecode(response.body);
//   return res['mStatus'];
// }

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