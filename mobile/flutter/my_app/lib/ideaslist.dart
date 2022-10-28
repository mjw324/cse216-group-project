import 'package:flutter/material.dart';
import 'package:my_app/profileobj.dart';
import 'votebutton.dart';
import 'ideaobj.dart';
import 'routes.dart'; // is this necessary?
import 'package:provider/provider.dart';
import 'schedule.dart';
import 'commentlist.dart';
import 'commentpage.dart';

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
 /*
  void initState() {
    super.initState();
    print(Provider.of<MySchedule>(context, listen: true).sessionId);
    _listIdeas = fetchIdeas(Provider.of<MySchedule>(context, listen: false).sessionId);
  }

  void retry() {
    setState(() {
      print(Provider.of<MySchedule>(context, listen: true).sessionId);
      _listIdeas = fetchIdeas(Provider.of<MySchedule>(context, listen: false).sessionId);
    });
  }
*/
  @override
  Widget build(BuildContext context) {
    // We need to create a schedule in order to access MySchedule (check to see if instance of Consumer and Provider concurrently is code smell)
    final schedule = Provider.of<MySchedule>(context);
    print('this is from idea list page');
    print(session);
   // fetchIdeas(session).then((value) => print(value),);
    // Creates scheduleList to access current ideas list stored in schedule
    List<IdeaObj> scheduleList = schedule.ideas;
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
                  IdeaObj idea = scheduleList[i];
                  return Column(
                    children: <Widget>[
                      ListTile(
                        trailing: ElevatedButton(
                          child:const Icon(Icons.comment, size: 30, color: Colors.white),
                          onPressed: (){
                            Navigator.of(context).push( MaterialPageRoute(builder: (context) => MyCommentPage(title: 'Comment Page', session: session) ));
                          },
                          ),
                         
                          leading:
                           VoteButtonWidget(
                              idx: idea.id, liked: idea.userVotes),
                              
                          title:
                            ElevatedButton(
                              style:ElevatedButton.styleFrom(
                                backgroundColor: Colors.brown,
                              ),
                            child:Text(
                              idea.title + ' by ' + idea.userId,
                              style: _biggerFont,
                            ),
                            onPressed: () => showDialog<String>(
                              context: context, 
                              builder: (BuildContext context) => AlertDialog(
                                title: const Text('Profile Information'),
                                content: const Text('Profile Information'),
                                actions: <Widget>[
                                TextButton(
                                  onPressed: () => Navigator.pop(context, 'OK'),
                                  child: const Text('OK'),
                                ),
                              ],
                              ),
                            ),
                            
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
                      IdeaObj idea = list[i];
                      
                      return Column(
                        children: <Widget>[
                          ListTile(
                              trailing: 
                                ElevatedButton(
                                child:const Icon(Icons.comment, size: 30, color: Colors.white),
                                onPressed: (){
                                  Navigator.push(context, MaterialPageRoute(builder: (context) =>CommentListWidget(userId: routes.user_id) ));
                                },
                                ),
                              leading: VoteButtonWidget(
                                  idx: idea.id, liked: idea.userVotes),
                              title: ElevatedButton(
                                child:Text(
                                  idea.title + ' by ' + idea.userId,
                                  style: _biggerFont,
                                ),
                                onPressed: () => showDialog<String>(
                                  context: context, 
                                  builder: (BuildContext context) => AlertDialog(
                                    title: const Text('Profile Information'),
                                    content: Text(ideasList.fetchProfile(idea.userId).toString()),
                                    actions: <Widget>[
                                    TextButton(
                                      onPressed: () => Navigator.pop(context, 'OK'),
                                      child: const Text('OK'),
                                    ),
                                  ],
                                  ),
                                ),
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

class ideasList{
  static List<ProfileObj> fetchProfile(String userId){
    Future<List<ProfileObj>> prof; 
    List<ProfileObj> profile = []; 
    prof = routes.fetchProfileInfo(userId);
    print(prof);
    prof.then((value) => {profile.add(value.first)});
    
    print(profile.length);
    //ProfileObj userProfile = profile[profile.length];
    return profile;
  }
}
/*
Future<List<ProfileObj>> prof; 
List<ProfileObj> profile = []; 
prof = routes.fetchProfileInfo(idea.userId);
prof.then((value) => {profile = value});
schedule.profileList = profile;
print(profile.length);
ProfileObj userProfile = profile[profile.length];
*/
