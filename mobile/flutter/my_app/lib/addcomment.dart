import 'package:flutter/material.dart';
import 'package:my_app/commentobj.dart';
import 'package:provider/provider.dart';
import 'routes.dart';
import 'schedule.dart';
import 'loginpage.dart';

//This is the method to add comments 
class AddCommentWidget extends StatefulWidget {
  int id; 
  AddCommentWidget({Key? key, required this.id}) : super(key: key);


  @override
  State<AddCommentWidget> createState() => _AddCommentWidget(id: id);
}

class _AddCommentWidget extends State<AddCommentWidget> {
  // title and idea controller are used to manage text input in respective fields
  final _commentController = TextEditingController();
  String comment = '';
  late CommentObj newComment = CommentObj(postId: id, commentId: 000, userId: 'userId', comment: comment, username: 'asd'); 

  int id;
  _AddCommentWidget({required this.id});

  @override
  Widget build(BuildContext context) {
    // Instantiate schedule using Provider.of, this is so we can access methods from schedule
    final schedule = Provider.of<MySchedule>(context);
    return SliverToBoxAdapter
    (
        child: Column(
        children: [
          TextField
          (
            controller: _commentController,
            decoration: const InputDecoration(
              hintText: 'comment',
              border: OutlineInputBorder(),
            ),
            maxLength: 128, // max amount of characters accepted is 128
          ),
          Align
          (
            alignment: Alignment.centerLeft,
            child: MaterialButton(
              onPressed: () {
                // checks if both title and idea field are filled before adding
                if (_commentController.text != '' ) {
                  comment = _commentController.text;
                  // Currently this string cant be used because it always returns 1. Backend needs to return id of newIdea
                  print(id);
                  Future<String> idofNewComment = addComment(comment, id);
                  // This is when it another comment is added
                  newComment = CommentObj(
                    postId: id, 
                    commentId: 0000, 
                    userId: routes.user_id, 
                    comment: comment,
                    username: SignInToGetUsername.username.substring(0,SignInToGetUsername.username.indexOf('@') ));

                  schedule.submitComment = newComment; // submits idea and notifies list listeners
                  _commentController.clear();
                }
              },
              color: Colors.blueAccent,
              child: const Text('Comment', style: TextStyle(color: Colors.white)),
          ),
        ),
      ],
    ));
  }
}



//This is the method to edit comments. Need to include the parameters of the comment id and userId
class EditCommentWidget extends StatefulWidget {
  int id; 
  int commentId;
  EditCommentWidget({Key? key, required this.id, required this.commentId}) : super(key: key);


  @override
  State<EditCommentWidget> createState() => _EditCommentWidget(id: id, commentId: commentId);
}

class _EditCommentWidget extends State<EditCommentWidget> {
  // title and idea controller are used to manage text input in respective fields
  final _commentController = TextEditingController();
  String Ncomment = '';
  

  int id;
  int commentId;
  _EditCommentWidget({required this.id, required this.commentId});

  @override
  Widget build(BuildContext context) {
    // Instantiate schedule using Provider.of, this is so we can access methods from schedule
    final schedule = Provider.of<MySchedule>(context);
    return Column(
      children: [
        TextField(
          controller: _commentController,
          decoration: const InputDecoration(
            hintText: 'new comment',
            border: OutlineInputBorder(),
          ),
          maxLength: 128, // max amount of characters accepted is 128
        ),
        Align(
          alignment: Alignment.centerLeft,
          child: MaterialButton(
            onPressed: () {
              // checks if both title and idea field are filled before adding
              if (_commentController.text != '' ) {
                Ncomment = _commentController.text;
                // Currently this string cant be used because it always returns 1. Backend needs to return id of newIdea
                Future<String> idofNewComment = editComment(Ncomment, id, commentId);
                // This is temporary, until backend POST /messages route can return the id of the new Idea so we can GET idea by id
                 _AddCommentWidget(id: id).newComment.comment = Ncomment;
                  
                schedule.submitComment = _AddCommentWidget(id: id).newComment; // submits idea and notifies list listeners
                _commentController.clear();
              }
            },
            color: Colors.blueAccent,
            child: const Text('Comment', style: TextStyle(color: Colors.white)),
          ),
        ),
      ],
    );
  }
}


