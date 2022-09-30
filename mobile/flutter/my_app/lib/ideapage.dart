import 'package:flutter/material.dart';
import 'dart:developer' as developer;
import 'package:http/http.dart' as http;
import 'dart:convert';
import 'dart:async';

class MyHomePage extends StatefulWidget {
  const MyHomePage({super.key, required this.title});

  // This widget is the home page of your application. It is stateful, meaning
  // that it has a State object (defined below) that contains fields that affect
  // how it looks.

  // This class is the configuration for the state. It holds the values (in this
  // case the title) provided by the parent (in this case the App widget) and
  // used by the build method of the State. Fields in a Widget subclass are
  // always marked "final".

  final String title;

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  final _titleController = TextEditingController();
  final _ideaController = TextEditingController();
  bool changesMade = false;
  String title = '';
  String idea = '';
  Widget ideasList(bool changesMade) {
    var fb = FutureBuilder<List<IdeaObj>>(
      future: fetchIdeas(),
      builder: (BuildContext context, AsyncSnapshot<List<IdeaObj>> snapshot) {
        Widget child;

        if (snapshot.hasData) {
          // developer.log('`using` ${snapshot.data}', name: 'my.app.category');
          // create  listview to show one row per array element of json response
          child = ListView.builder(
              shrinkWrap:
                  true, //expensive! consider refactoring. https://api.flutter.dev/flutter/widgets/ScrollView/shrinkWrap.html
              padding: const EdgeInsets.all(16.0),
              itemCount: snapshot.data!.length,
              itemBuilder: /*1*/ (context, i) {
                return Column(
                  children: <Widget>[
                    ListTile(
                        // pass idea id through for updating votes
                        leading: VoteButtonWidget(id: snapshot.data![i].id),
                        title: Text(
                          snapshot.data![i].title,
                          style: const TextStyle(fontSize: 16),
                        ),
                        subtitle: Text(
                          snapshot.data![i].message,
                        )),
                    const Divider(height: 1.0),
                  ],
                );
              });
        } else if (snapshot.hasError) {
          // newly added
          child = Text('${snapshot.error}');
        } else {
          // awaiting snapshot data, return simple text widget
          // child = Text('Calculating answer...');
          child = const CircularProgressIndicator(); //show a loading spinner.
        }
        return child;
      },
    );

    return fb;
  }

  @override
  Widget build(BuildContext context) {
    // This method is rerun every time setState is called
    //
    // The Flutter framework has been optimized to make rerunning build methods
    // fast, so that you can just rebuild anything that needs updating rather
    // than having to individually change instances of widgets.
    return Scaffold(
      appBar: AppBar(
        // Here weFsrt take the value from the MyHomePage object that was created by
        // the App.build method, and use it to set our appbar title.
        title: Text(widget.title),
      ),
      body: ListView(padding: const EdgeInsets.all(8.0), children: <Widget>[
        TextField(
          controller: _titleController,
          decoration: const InputDecoration(
            hintText: 'Title',
            border: OutlineInputBorder(),
          ),
          maxLength: 128, // max amount of characters accepted is 128
        ),
        TextField(
          controller: _ideaController,
          decoration: const InputDecoration(
            hintText: 'What is your next big idea?',
            border: OutlineInputBorder(),
          ),
          maxLength: 1024, // max amount of characters accepted is 1024
        ),
        Align(
          alignment: Alignment.centerLeft,
          child: MaterialButton(
            onPressed: () {
              // checks if both title and idea field are filled before adding
              if (_titleController.text != '' && _ideaController.text != '') {
                title = _titleController.text;
                idea = _ideaController.text;
                Future<String> mMessage = addIdea(title,
                    idea); // Currently mMessage isn't being used, but will be useful in the future for error handling
                _ideaController.clear();
                _titleController.clear();
                setState(() {
                  // Not the cleanest way to update the ideasList widget, but it works and don't know how to do otherwise
                  changesMade = !changesMade;
                });
              }
            },
            color: Colors.brown,
            child: const Text('Add', style: TextStyle(color: Colors.white)),
          ),
        ),
        ideasList(changesMade),
      ]),
    );
  }
}

// The object representation of an idea.
// The idea object uses the data from the JSON response received from backend
class IdeaObj {
  // The int representation of the idea id
  final int id;

  // The String representation of the idea title
  final String title;

  // The String representation of the idea message
  final String message;

  // The int representation of the amount of votes
  final int votes;

  // The String representation of the amount of votes
  // Possible technical debt - this is a question of type Date vs type String
  final String createdAt;

  const IdeaObj({
    required this.id,
    required this.title,
    required this.message,
    required this.votes,
    required this.createdAt,
  });

  factory IdeaObj.fromJson(Map<String, dynamic> json) {
    print(json);
    // {'id':INT, 'title':STRING, message:STRING, votes:INT, createdAt:STRING}
    return IdeaObj(
      id: json['mId'],
      title: json['mTitle'],
      message: json['mMessage'],
      votes: json['mVotes'],
      createdAt: json['mCreated'],
    );
  }
}

Future<String> addIdea(String title, String message) async {
  final response = await http.post(
    Uri.parse('https://whispering-sands-78580.herokuapp.com/messages'),
    body: jsonEncode(<String, String>{'mTitle': title, 'mMessage': message}),
  );
  var res = jsonDecode(response.body);
  return res['mMessage'];
}

Future<int> voteIdea(int idx, bool isUpvote) async {
  String vote = isUpvote ? 'upvotes' : 'downvotes';
  final response = await http.put(Uri.parse(
      'https://whispering-sands-78580.herokuapp.com/messages/$idx/$vote'));
  var res = jsonDecode(response.body);
  print(res);
  return res['mData'];
}

// Takes the /ideas route JSON response and converts it into a list of Idea Objects
Future<List<IdeaObj>> fetchIdeas() async {
  final response = await http
      .get(Uri.parse('https://whispering-sands-78580.herokuapp.com/messages'));

  if (response.statusCode == 200) {
    // If the server did return a 200 OK response, then parse the JSON.
    final List<IdeaObj> returnData;
    developer.log(response.body);
    var res = jsonDecode(response.body);
    print('json decode: $res');
    print('The keys are: ${res.keys}');
    print('The values are: ${res.values}');
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
    return returnData;
  } else {
    // If the server did not return a 200 OK response,
    // then throw an exception.
    throw Exception('Did not receive success status code from request.');
  }
}

class VoteButtonWidget extends StatefulWidget {
  final int idx;
  const VoteButtonWidget({required int id}) : idx = id;

  @override
  State<VoteButtonWidget> createState() => _VoteButtonWidgetState();
}

class _VoteButtonWidgetState extends State<VoteButtonWidget> {
  bool _upvotePressed = false; // used to change color of upvote
  bool _downvotePressed = false; // used to change color of upvote
  @override
  Widget build(BuildContext context) {
    return Container(
        width: 60,
        child: Row(
          children: [
            MaterialButton(
              minWidth: 10,
              height: 40,
              color: _upvotePressed ? Colors.red[300] : Colors.grey[850],
              child: const Text(
                '↑',
                style: TextStyle(fontSize: 15, color: Colors.white),
              ),
              onPressed: () => {
                setState(() => {
                      if (_downvotePressed)
                        {
                          // Will have to upvote twice to make up for previous downvote
                          _downvotePressed = false,
                          _upvotePressed = true,
                          voteIdea(widget.idx, true),
                          voteIdea(widget.idx, true),
                        }
                      else if (_upvotePressed)
                        {
                          // Will remove upvote if already upvoted
                          _upvotePressed = false,
                          voteIdea(widget.idx, false),
                        }
                      else
                        {
                          // Will upvote if it hasn't been downvoted or upvoted
                          _upvotePressed = true,
                          voteIdea(widget.idx, true),
                        }
                    })
              },
            ),
            const SizedBox(width: 4), // Invis. Box between Upvote and Downvote
            MaterialButton(
              minWidth: 10,
              height: 40,
              color: _downvotePressed ? Colors.indigo[300] : Colors.grey[850],
              child: const Text(
                '↓',
                style: TextStyle(fontSize: 15, color: Colors.white),
              ),
              onPressed: () => {
                setState(() => {
                      if (_upvotePressed)
                        {
                          // Will have to downvote twice to make up for previous upvote
                          _upvotePressed = false,
                          _downvotePressed = true,
                          voteIdea(widget.idx, false),
                          voteIdea(widget.idx, false),
                        }
                      else if (_downvotePressed)
                        {
                          // Will remove downvote if already downvoted
                          _downvotePressed = false,
                          voteIdea(widget.idx, true),
                        }
                      else
                        {
                          // Will downvote if it hasn't been downvoted or upvoted
                          _downvotePressed = true,
                          voteIdea(widget.idx, false),
                        }
                    })
              },
            ),
          ],
        ));
  }
}
