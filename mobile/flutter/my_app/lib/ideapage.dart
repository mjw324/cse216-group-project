import 'package:flutter/material.dart';
import 'dart:async';
// Provides functions which use REST calls and interprets responses
import 'routes.dart';
import 'addidea.dart';
import 'ideaslist.dart';
import 'schedule.dart';
import 'package:provider/provider.dart';
import 'profilepage.dart';

class MyHomePage extends StatefulWidget {
  int session;
   MyHomePage({super.key, required this.title, required this.session});

  // This widget is the home page of your application. It is stateful, meaning
  // that it has a State object (defined below) that contains fields that affect
  // how it looks.

  // This class is the configuration for the state. It holds the values (in this
  // case the title) provided by the parent (in this case the App widget) and
  // used by the build method of the State. Fields in a Widget subclass are
  // always marked "final".

  final String title;

  @override
  State<MyHomePage> createState() => _MyHomePageState(session: session);
}

class _MyHomePageState extends State<MyHomePage> {
  late int session; 
  _MyHomePageState({required this.session});
  @override
  Widget build(BuildContext context) {
    // This method is rerun every time setState is called
    //
    // The Flutter framework has been optimized to make rerunning build methods
    // fast, so that you can just rebuild anything that needs updating rather
    // than having to individually change instances of widgets.
    return ChangeNotifierProvider(
        create: (context) => MySchedule(),
        child: Scaffold(
            appBar: AppBar(
              // Here we take the value from the MyHomePage object that was created by
              // the App.build method, and use it to set our appbar title.
              title: Text(widget.title),
            ),
            body: CustomScrollView(
              // This is the optimized version of ListView, where slivers don't need to be rendered when not on screen (in viewport)
              slivers: <Widget>[
                AddIdeaWidget(),
                IdeasListWidget(sessionId: session),
              ],
            )));
  }
} 
class TabBarDemo extends StatelessWidget {
  String name, email; 
  int session; 
   TabBarDemo( {super.key, required this.email, required this.name, required this.session});

  @override
  Widget build(BuildContext context) {
    final schedule = Provider.of<MySchedule>(context);
    schedule.sessionId = session;
    print('this is from the tabbardemo');
    print(session);
    return MaterialApp(
      home: DefaultTabController(
        length: 2,
        child: Scaffold(
          appBar: AppBar(
            bottom: const TabBar(
              tabs: [
                Tab(icon:Icon(Icons.list_alt_outlined)),
                Tab(icon: Icon(Icons.face_outlined)),
              ],
            ),
            title: const Text('The Buzz'),
          ),
          body: TabBarView(
            children: [
              MyHomePage(title: 'The Buzz Idea Page', session: session),
              MyProfilePage(title: 'Profile Page',name: name, email: email),
            ],
          ),
        ),
      ),
    );
  }
}
