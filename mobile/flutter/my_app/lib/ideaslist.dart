import 'package:flutter/material.dart';
import 'votebutton.dart';
import 'ideaobj.dart';
import 'routes.dart'; 
import 'package:provider/provider.dart';
import 'schedule.dart';
import 'profilepage.dart';
import 'commentpage.dart';
//This files is for showing the list of ideas in the Homepage
class IdeasListWidget extends StatefulWidget {
  int sessionId; 
  IdeasListWidget({Key? key, required this.sessionId}) : super(key: key);

  @override
  State<IdeasListWidget> createState() => _IdeasListWidgetState(session: sessionId);
}

class _IdeasListWidgetState extends State<IdeasListWidget> {

  late Future<List<IdeaObj>> _listIdeas;
  int session;
  _IdeasListWidgetState({required this.session}); 
  final _biggerFont = const TextStyle(fontSize: 16);

 
  @override
  Widget build(BuildContext context) {
    // We need to create a schedule in order to access MySchedule (check to see if instance of Consumer and Provider concurrently is code smart)
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
                  return Card(
                      color: Colors.grey[350],
                      shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(20),
                      ),
                      child: Column(
                        children: <Widget>[
                          ListTile(
                              trailing: ElevatedButton(
                                child: const Icon(Icons.comment,
                                    size: 30, color: Colors.white),
                                onPressed: () {
                                  Navigator.of(context).push(MaterialPageRoute(
                                      builder: (context) => MyCommentPage(
                                          title: 'Comment Page', id: idea.id)));
                                },
                              ),
                              leading: VoteButtonWidget(
                                  idx: idea.id, liked: idea.userVotes),
                              title: Text(
                                idea.title,
                                style: const TextStyle(fontSize: 20),
                              ),
                              subtitle: ElevatedButton(
                                style: ElevatedButton.styleFrom(
                                    backgroundColor: Colors.grey,
                                    alignment: Alignment.centerLeft,
                                    fixedSize: const Size.fromWidth(100)),
                                child: Text(
                                  'By: ${idea.username}',
                                ),
                                onPressed: () {
                                  Navigator.of(context).push(MaterialPageRoute(
                                      builder: (context) => ProfileWidget(
                                          title: 'Profile Page',
                                          userId: idea.userId)));
                                },
                              )),
                          Center(
                              child: Container(
                            alignment: Alignment.bottomCenter,
                            child: Text(idea.message, style: _biggerFont),
                          )),
                        ],
                      ));
                },
                childCount: scheduleList.length,
              )));
    } else {
      //Must include this type of format with builder, future for fetching from the routes
      return Consumer<MySchedule>(
          builder: (context, schedule, _) => FutureBuilder<List<IdeaObj>>(
              future: _listIdeas= routes.fetchPosts(),
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
                      //this takes the data from routes and makes it an idea obj
                      IdeaObj idea = list[i];
                      return Column(
                        children: <Widget>[
                          ListTile
                          (
                              //if the comment button is pressed, it goes to the comment page
                              trailing: 
                                ElevatedButton(
                                  style: ElevatedButton.styleFrom( backgroundColor: Colors.brown),
                                //style: ElevatedButton.styleFrom(backgroundColor: Colors.amber),
                                child:const Icon(Icons.comment, size: 30, color: Colors.white),
                                onPressed: (){
                                  Navigator.push(context, MaterialPageRoute(builder: (context) =>MyCommentPage(title: 'Comment Page', id: idea.id) ));
                                },
                              ),
                              //Vote button
                              leading: VoteButtonWidget(
                                  idx: idea.id, liked: idea.userVotes),
                              //the button with the title and can press it to see who posted it
                              title: ElevatedButton(
                                child:Text(
                                  idea.title + ' by ' + idea.username,
                                  style: _biggerFont,
                                ),
                                onPressed: (){
                                    Navigator.of(context).push( MaterialPageRoute(builder: (context) => ProfileWidget(title: 'Profile Page', userId: idea.userId) ));
                                 },
                              ), 
                              // shows the message
                              subtitle:
                                Text(
                                  idea.link != null ? idea.message + ' ' + idea.link :
                                  idea.message,
                                )
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


