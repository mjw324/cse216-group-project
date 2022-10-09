import 'package:flutter/material.dart';
import 'votebutton.dart';
import 'ideaobj.dart';
import 'routes.dart'; // is this necessary?
import 'package:provider/provider.dart';
import 'schedule.dart';

class IdeasListWidget extends StatefulWidget {
  const IdeasListWidget({Key? key}) : super(key: key);

  @override
  State<IdeasListWidget> createState() => _IdeasListWidgetState();
}

class _IdeasListWidgetState extends State<IdeasListWidget> {
  late Future<List<IdeaObj>> _list_ideas;
  final _biggerFont = const TextStyle(fontSize: 16);

  @override
  void initState() {
    super.initState();
    _list_ideas = fetchIdeas();
  }

  @override
  Widget build(BuildContext context) {
    return FutureBuilder<List<IdeaObj>>(
      future: _list_ideas,
      builder: ((BuildContext context, AsyncSnapshot<List<IdeaObj>> snapshot) {
        Widget child;
        if(snapshot.hasData) {
          child = ListView.builder(
            shrinkWrap: true,
            padding: const EdgeInsets.all(16),
            itemCount: snapshot.data!.length,
            itemBuilder: (context, i) {
            return Column(
              children: <Widget>[
                ListTile(
                    leading: VoteButtonWidget(
                        idx: snapshot.data![i].id,
                        votes: snapshot.data![i].votes),
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
          child = Text('${snapshot.error}');
        } else {
          child = const Center(child: SizedBox(width: 30, height: 30, child: CircularProgressIndicator()));
        }
        return child;
    }));
    
  }
}

  //   child = const Center(
  //       child: SizedBox(
  //           width: 50,
  //           height: 50,
  //           child:
  //               CircularProgressIndicator())); //show a loading spinner.
  // }
  // return child;


  // padding: const EdgeInsets.all(16.0),
  // itemCount: _list_ideas.length,
  // itemBuilder: /*1*/ (context, i) {
  //   return Column(
  //     children: <Widget>[
  //       ListTile(
  //           leading: VoteButtonWidget(
  //               idx: _list_ideas[i].id,
  //               votes: _list_ideas[i].votes),
  //           title: Text(
  //             _list_ideas[i].title,
  //             style: const TextStyle(fontSize: 16),
  //           ),
  //           subtitle: Text(
  //             _list_ideas[i].message,
  //           )),
  //       const Divider(height: 1.0),
  //     ],
  //   );
  // });