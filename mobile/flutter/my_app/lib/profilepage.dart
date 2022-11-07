import 'package:flutter/material.dart';
import 'package:my_app/commentobj.dart';
import 'routes.dart'; // is this necessary?
import 'package:provider/provider.dart';
import 'schedule.dart';
import 'commentlist.dart';
import 'addcomment.dart';
import 'profileobj.dart';

class ProfileWidget extends StatefulWidget {
    final String title;
  String userId; 
  ProfileWidget({Key? key, required this.title, required this.userId}) : super(key: key);

  @override
  State<ProfileWidget> createState() => _ProfileWidgetState(userId: userId);
}

class _ProfileWidgetState extends State<ProfileWidget> {

 
  late Future<List<ProfileObj>> _listProfile;
  String userId;
  _ProfileWidgetState({required this.userId}); 
  final _biggerFont = const TextStyle(fontSize: 16);

  @override
  Widget build(BuildContext context) {
    // We need to create a schedule in order to access MySchedule (check to see if instance of Consumer and Provider concurrently is code smell)
    final schedule = Provider.of<MySchedule>(context);
    // Creates scheduleList to access current ideas list stored in schedule
    List<ProfileObj> scheduleList = schedule.profile;
     // This is called when IdeasListWidget is first initialized. The _listIdeas variable is initialized with fetchIdeas() in routes.dart
  
    // If it isn't empty, we create our IdeasListWidget from the scheduleList.
    // This will need to be refactored because schedule.ideas only updates when 1) it is initialized in else block 2) we make updates to it on app
    // *It does not take into account new ideas from other clients after it has been initialized*
      return FutureBuilder<List<ProfileObj>>(
              future: _listProfile= routes.fetchProfileInfo(userId),
              builder: ((BuildContext context,
                  AsyncSnapshot<List<ProfileObj>> snapshot) {
                Widget child;
                if (snapshot.hasData) {
                  List<ProfileObj> list = snapshot.data!;
                  schedule.profileList = list;
                      ProfileObj prof = list[0];
                      return Scaffold(
                        appBar: AppBar(title: const 
                        Text(
                            "User's Profile!",
                          ),
                          backgroundColor: Colors.brown,
                          ),
                          body: Center(
                          child: Column(
                            mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                            children: <Widget>[
                              Text(
                                style: const TextStyle(height: 1, fontSize: 20),
                                'Username: ${prof.username}'
                              ),
                              Text(
                                style: const TextStyle(height: 1, fontSize: 20),
                                'Email: ${prof.email}'
                              ),
                              Text(
                                  style: const TextStyle(height: 1, fontSize: 20),
                                'Note: ${prof.note}' 
                              ),
                            ]
                          )
                          ));
                    
               
  } else if (snapshot.hasError) {
                  child =  Text('${snapshot.error}');
                } else {
                  child = const Center(
                          child: SizedBox(
                              width: 30,
                              height: 30,
                              child: CircularProgressIndicator()));
                }
                return child;
 }));

  }
}

