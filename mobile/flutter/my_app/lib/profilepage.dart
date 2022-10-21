import 'package:flutter/material.dart';
import 'dart:async';
// Provides functions which use REST calls and interprets responses
import 'routes.dart';
import 'profileinfo.dart';
import 'ideaslist.dart';
import 'schedule.dart';
import 'package:provider/provider.dart';

class MyProfilePage extends StatefulWidget {
  const MyProfilePage({super.key, required this.title});

  // This widget is the home page of your application. It is stateful, meaning
  // that it has a State object (defined below) that contains fields that affect
  // how it looks.

  // This class is the configuration for the state. It holds the values (in this
  // case the title) provided by the parent (in this case the App widget) and
  // used by the build method of the State. Fields in a Widget subclass are
  // always marked "final".

  final String title;

  @override
  State<MyProfilePage> createState() => _MyProfilePage();
}

class _MyProfilePage extends State<MyProfilePage> {
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
            body: const CustomScrollView(
              // This is the optimized version of ListView, where slivers don't need to be rendered when not on screen (in viewport)
              slivers: <Widget>[
                AddProfileWidget(),
                
              ],
            )));
  }
} 