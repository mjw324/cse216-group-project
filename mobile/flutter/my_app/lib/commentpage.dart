import 'dart:html';

import 'package:flutter/material.dart';
import 'dart:async';
// Provides functions which use REST calls and interprets responses
import 'routes.dart';
import 'commentadd.dart';
import 'commentlist.dart';
import 'schedule.dart';
import 'package:provider/provider.dart';
import 'profilepage.dart';

class MyCommentPage extends StatefulWidget{
  final String title;
  int session; 
  MyCommentPage({super.key, required this.title, required this.session});

  @override 
  State<MyCommentPage> createState() => _MyCommentPage(session: session);

}

class _MyCommentPage extends State<MyCommentPage>{
  int session; 
  _MyCommentPage({required this.session});

  @override 
   Widget build(BuildContext context) {
    return ChangeNotifierProvider(
      create: (context) => MySchedule(),
      child: Scaffold(
        appBar: AppBar(title: Text(widget.title),
        ),
        body:CustomScrollView(
          slivers: <Widget>[
            //commentadd(),
            CommentListWidget(userId: session.toString())

          ]

        ))
    );
}
}