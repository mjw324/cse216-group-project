import 'dart:html';

import 'package:flutter/material.dart';
import 'dart:async';
// Provides functions which use REST calls and interprets responses
import 'routes.dart';
import 'commentlist.dart';
import 'schedule.dart';
import 'package:provider/provider.dart';
import 'addcomment.dart';

//This is similar to idea page except with comments.
class MyCommentPage extends StatefulWidget{
  //The title for the title of the new page -> "comment page"
  final String title;
  //int id is post id-> to associate the comments with the specific posts
  int id; 
  MyCommentPage({super.key, required this.title, required this.id});

  @override 
  State<MyCommentPage> createState() => _MyCommentPage(id: id);

}

class _MyCommentPage extends State<MyCommentPage>{
  int id; 
  _MyCommentPage({required this.id});

  @override 
   Widget build(BuildContext context) {
    return ChangeNotifierProvider(
      create: (context) => MySchedule(),
      child: Scaffold(
        appBar: AppBar(title: Text(widget.title),
        ),
        body:CustomScrollView(
          slivers: <Widget>[
            AddCommentWidget(id: id),
            CommentListWidget(id: id)

          ]

        ))
    );
}
}