import 'package:flutter/material.dart';
import 'package:my_app/commentobj.dart';
import 'votebutton.dart';
import 'ideaobj.dart';
import 'routes.dart'; // is this necessary?
import 'package:provider/provider.dart';
import 'schedule.dart';
import 'commentlist.dart';

class CommentListWidget extends StatefulWidget {
  String userId = routes.user_id; 
  CommentListWidget({Key? key, required this.userId}) : super(key: key);

  @override
  State<CommentListWidget> createState() => _CommentListWidgetState(user_id: userId);
}

class _CommentListWidgetState extends State<CommentListWidget> {

  late Future<List<CommentObj>> _listComments;
  late String user_id;
  _CommentListWidgetState({required this.user_id}); 
  final _biggerFont = const TextStyle(fontSize: 16);

  @override
  Widget build(BuildContext context) {
    // We need to create a schedule in order to access MySchedule (check to see if instance of Consumer and Provider concurrently is code smell)
    final schedule = Provider.of<MySchedule>(context);
    // Creates scheduleList to access current ideas list stored in schedule
    List<CommentObj> scheduleList = schedule.comment;
     // This is called when IdeasListWidget is first initialized. The _listIdeas variable is initialized with fetchIdeas() in routes.dart
  
    // If it isn't empty, we create our IdeasListWidget from the scheduleList.
    // This will need to be refactored because schedule.ideas only updates when 1) it is initialized in else block 2) we make updates to it on app
    // *It does not take into account new ideas from other clients after it has been initialized*
    if (scheduleList.isNotEmpty) {
      // Whenever MySchedule notifiesListeners(), the consumers update state
      return Consumer<MySchedule>(
          builder: (context, schedule, _) => SliverList(
                  delegate: SliverChildBuilderDelegate(
                (context, i) {
                  CommentObj comment = scheduleList[i];
                  return Column(
                    children: <Widget>[
                      ListTile(                         
                          leading: Text(
                            comment.userId,
                          ),
                          title: Text(
                            comment.comment,
                            style: _biggerFont,
                          ),
                          ),
                      const Divider(height: 1.0),
                    ],
                  );
                },
                childCount: scheduleList.length,
              )));
    } else {
      return Consumer<MySchedule>(
          builder: (context, schedule, _) => FutureBuilder<List<CommentObj>>(
              future: _listComments= routes.fetchComments(),
              builder: ((BuildContext context,
                  AsyncSnapshot<List<CommentObj>> snapshot) {
                Widget child;
                if (snapshot.hasData) {
                  List<CommentObj> list = snapshot.data!;
                  schedule.commentList = list;
                  child = SliverList(
                      //padding: const EdgeInsets.all(16),
                      delegate: SliverChildBuilderDelegate(
                    (context, i) {
                      CommentObj comment = list[i];
                      return Column(
                        children: <Widget>[
                      ListTile(                         
                          leading: Text(
                            comment.userId,
                          ),
                          title: Text(
                            comment.comment,
                            style: _biggerFont,
                          ),
                          ),
                      const Divider(height: 1.0),
                    ],
                      );
                    },
                    childCount: list.length,
                  ));
                } else if (snapshot.hasError) {
                  child = SliverToBoxAdapter(child: Text('${snapshot.error}'));
                } else {
                  child = const SliverToBoxAdapter(
                      child: Center(
                          child: SizedBox(
                              width: 30,
                              height: 30,
                              child: CircularProgressIndicator())));
                }
                return child;
              })));
    }

  }
}
