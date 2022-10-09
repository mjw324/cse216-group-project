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
  late Future<List<IdeaObj>> _listIdeas;
  final _biggerFont = const TextStyle(fontSize: 16);

  // This is called when IdeasListWidget is first initialized. The _listIdeas variable is initialized with fetchIdeas() in routes.dart
  @override
  void initState() {
    super.initState();
    _listIdeas = fetchIdeas();
  }

  void retry() {
    setState(() {
      _listIdeas = fetchIdeas();
    });
  }

  @override
  Widget build(BuildContext context) {
    // We need to create a schedule in order to access MySchedule (check to see if instance of Consumer and Provider concurrently is code smell)
    final schedule = Provider.of<MySchedule>(context);
    // Creates scheduleList to access current ideas list stored in schedule
    List<IdeaObj> scheduleList = schedule.ideas;
    // If it isn't empty, we create our IdeasListWidget from the scheduleList.
    // This will need to be refactored because schedule.ideas only updates when 1) it is initialized in else block 2) we make updates to it on app
    // *It does not take into account new ideas from other clients after it has been initialized*
    if (scheduleList.isNotEmpty) {
      // Whenever MySchedule notifiesListeners(), the consumers update state
      return Consumer<MySchedule>(
          builder: (context, schedule, _) => SliverList(
                  delegate: SliverChildBuilderDelegate(
                (context, i) {
                  IdeaObj idea = scheduleList[i];
                  return Column(
                    children: <Widget>[
                      ListTile(
                          leading: VoteButtonWidget(
                              idx: idea.id, liked: idea.userVotes),
                          title: Text(
                            idea.title,
                            style: _biggerFont,
                          ),
                          subtitle: Text(
                            idea.message,
                          )),
                      const Divider(height: 1.0),
                    ],
                  );
                },
                childCount: scheduleList.length,
              )));
    } else {
      return Consumer<MySchedule>(
          builder: (context, schedule, _) => FutureBuilder<List<IdeaObj>>(
              future: _listIdeas,
              builder: ((BuildContext context,
                  AsyncSnapshot<List<IdeaObj>> snapshot) {
                Widget child;
                if (snapshot.hasData) {
                  List<IdeaObj> list = snapshot.data!;
                  schedule.ideasList = list;
                  child = SliverList(
                      //padding: const EdgeInsets.all(16),
                      delegate: SliverChildBuilderDelegate(
                    (context, i) {
                      IdeaObj idea = list[i];
                      return Column(
                        children: <Widget>[
                          ListTile(
                              leading: VoteButtonWidget(
                                  idx: idea.id, liked: idea.userVotes),
                              title: Text(
                                idea.title,
                                style: _biggerFont,
                              ),
                              subtitle: Text(
                                idea.message,
                              )),
                          const Divider(height: 1.0),
                        ],
                      );
                    },
                    childCount: list.length,
                  ));
                } else if (snapshot.hasError) {
                  child = Text('${snapshot.error}');
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
