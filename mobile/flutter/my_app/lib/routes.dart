import 'package:http/http.dart' as http;
import 'package:my_app/commentobj.dart';
import 'dart:developer' as developer;
import 'dart:convert';
import 'dart:io';
import 'ideaobj.dart';
import 'profileobj.dart';
import 'dart:core';

class routes{
  //these are static variables to be acessed in all the dart files
  static int sessionId = 1;
  static String user_id = ""; 
  static String link = ""; 
  static http.Client client = http.Client();

  //sends request to backend by specifying the path, Need to use headers as we send the sessionId to make
  //sure the ideas, comments, and profile are all user based
  static Future<dynamic> sendRequest(String method, String path, {String? body}) async{
    final request = http.Request(method, Uri.parse('https://whispering-sands-78580.herokuapp.com$path'));
    request.headers["Session-ID"] = sessionId.toString();
    if(body != null){
      request.body = body;
    }
    final response = await http.Response.fromStream(await client.send(request));
    if(response.statusCode != 200){
      statusCode: response.statusCode; message: response.body;
    }
    return jsonDecode(response.body);
  }

  //Fetch all the posts from the backend
  static Future<List<IdeaObj>> fetchPosts() async{
    print(routes.sessionId);
    final data = await sendRequest("GET", "/messages");
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
    return returnData;
  
  }

  //Fetch profile information from backend of the user logged in 
    static Future<List<ProfileObj>> fetchProfile() async{
    final data = await sendRequest("GET", '/profile');
     final List<ProfileObj> returnData;
     var ideas = data['mData'];
      print(ideas);
     if(ideas is List){
      final posts = ideas.map((x) => ProfileObj.fromJson(x)).toList();
      return posts;
    }else if (ideas is Map) {
      returnData = <ProfileObj>[ProfileObj.fromJson(ideas as Map<String, dynamic>)];
    } else {
      developer
          .log('ERROR: Unexpected json response type (was not a List or Map).');
      returnData = List.empty();
    }
    return returnData;
  }

  //Fetches profile information of the other user depending on the user ID
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
    return returnData;

  }
  //Fetches all the comments from backend 
  static Future<List<CommentObj>> fetchComments(int id) async{
    print(id);
    final data = await sendRequest("GET", '/comments/$id');
    print("data:$data");
     final List<CommentObj> returnData;
     var ideas = data['mData'];
     //print(data['mData']);
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
    return returnData;
    

  }

}

//Uses the post method to add an idea by sending the title, message and sessionId
  Future<String> addIdea(String title, String message) async {
  final response = await http.post(
    Uri.parse('https://whispering-sands-78580.herokuapp.com/messages'),
    body: jsonEncode(<String, String>{'mTitle': title, 'mMessage': message, 'mSessionId' :routes.sessionId.toString()}),
  );
  var res = jsonDecode(response.body);
  print(res);
  return res['mData'];

}

//Uses the post method to add an idea by sending the title, message and sessionId
  Future<String> addIdeaMedia(String title, String message, String base64) async {
  final response = await http.post(
    Uri.parse('https://whispering-sands-78580.herokuapp.com/messages'),
    body: jsonEncode(<String, String>{'mTitle': title, 'mMessage': message, 'mBase64Image': base64, 'mSessionId' :routes.sessionId.toString()}),
  );
  var res = jsonDecode(response.body);
  //print(res['mData']);
  routes.link = res['mData'];
  //print(routes.link);
  print(res);
  return res['mData'];
}

//Post-> sends the id_token to backend and gets a session Id and user ID
Future<Map> sendToken(String? mToken) async {
  print('mToken');
  print(mToken);
  final response = await http.post(
    Uri.parse('https://whispering-sands-78580.herokuapp.com/signin'),
    body: jsonEncode(<String, String?>{'mToken': mToken}),
  );
  //developer.log(response.body);
  var res = jsonDecode(response.body);
  /*
  developer.log('json decode: $res');
  developer.log('The keys are: ${res.keys}');
  developer.log('The values are: ${res.values}');
   */ 
  //print(res['mData']);
  routes.sessionId = int.parse(res['mData']['Session-ID']);
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
  response = await http.put(Uri.parse( 'https://whispering-sands-78580.herokuapp.com/messages/$idx/$vote'), body: jsonEncode(<String, int?>{'mSessionId': routes.sessionId}),);

  /*if (voteCount > 1) {
    // If front ends needs to increment/decrement votes more than once, we specify the route with an addition /voteCount
    response = await http.put(Uri.parse(
        'https://whispering-sands-78580.herokuapp.com/messages/$idx/$vote'),
        body: jsonEncode(<String, int?>{'mSessionId': routes.sessionId}),);
  } else {
    response = await http.put(Uri.parse(
        'https://whispering-sands-78580.herokuapp.com/messages/$idx/$vote'),
        body: jsonEncode(<String, int?>{'mSessionId': routes.sessionId}),);
  }*/


  // res (response) should decode two key value pairs: mStatus and mData
  var res = jsonDecode(response.body);
  print(res);
  return res['mData'];
}

//Takes care of the upvote of an idea
/*Future<int> upVote(int idx) async{
  final response = await http.put(Uri.parse(
        'https://whispering-sands-78580.herokuapp.com/messages/$idx'),
        body: jsonEncode(<String, int?>{'mSessionId': routes.sessionId}),);
  var res = jsonDecode(response.body);
  print(res);
  return res['mData'];

}*/


// POST /comment/id route add a comment to a post given the postId
Future<String> addComment(String comment, int id ) async {
  final response = await http.post(
    Uri.parse('https://whispering-sands-78580.herokuapp.com/comment/$id'),
    body: jsonEncode(<String, String>{'mCommentId': '-1', 'mComment': comment, 'mSessionId' :routes.sessionId.toString()}),
  );
  var res = jsonDecode(response.body);
  //Once added, should print res{mStatus: ok, mMessage: 1}, if 0, there is an error on frontend
  print("res$res");
  return res['mMessage'];

}

Future<String> addCommentMedia(String comment, int id, String base64Com) async {
  final response = await http.post(
    Uri.parse('https://whispering-sands-78580.herokuapp.com/comment/$id'),
    body: jsonEncode(<String, String>{'mCommentId': '-1', 'mComment': comment, 'mBase64Image': base64Com, 'mSessionId' :routes.sessionId.toString()}),
  );
  var res = jsonDecode(response.body);
  //Once added, should print res{mStatus: ok, mMessage: 1}, if 0, there is an error on frontend
  print("res$res");
  return res['mData'];
}


//PUT on the /comment route-> edits a comment given that it was from the same user
Future<String> editComment(String comment, int id, int commentId) async {
  print("post ID: $id");
  final response = await http.put(
    Uri.parse('https://whispering-sands-78580.herokuapp.com/comment'),
    body: jsonEncode(<String, dynamic>{'mCommentId': commentId,'mComment': comment, 'mSessionId' :routes.sessionId}),
  );
  print(commentId);
  var res = jsonDecode(response.body);
  //should print res Edit{mStatus: ok, mMessage: 1} once the edit has been made
  print("res Edit$res");
  print('userId');
  return res['mMessage'];

}
//PUT on the /profile route -> Sends the editted profile to backend
Future<int> sendProfile(String username, String SO, String GI, String note) async{
   final response = await http.put(
    Uri.parse('https://whispering-sands-78580.herokuapp.com/profile'),
    body: jsonEncode(<String, String>{'mSessionId' :routes.sessionId.toString(), 'mGI': GI, 'mSO' : SO,  'mUsername': username.substring(0, username.indexOf(' and')),'mNote': note }),
  );
  var res = jsonDecode(response.body);
  //RES should be res{mStatus: ok, mData: 1} if it is all sent correctly
  print("res$res");
  return res['mData'];
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


