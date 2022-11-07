import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'routes.dart';
import 'schedule.dart';
import 'ideaobj.dart';
import 'loginpage.dart';

class AddIdeaWidget extends StatefulWidget {
  const AddIdeaWidget({super.key});

  @override
  State<AddIdeaWidget> createState() => _AddIdeaWidgetState();
}

class _AddIdeaWidgetState extends State<AddIdeaWidget> {
  // title and idea controller are used to manage text input in respective fields
  final _titleController = TextEditingController();
  final _ideaController = TextEditingController();
  String title = '';
  String idea = '';

  @override
  Widget build(BuildContext context) {
    // Instantiate schedule using Provider.of, this is so we can access methods from schedule
    final schedule = Provider.of<MySchedule>(context);
    return SliverToBoxAdapter(
        child: Column(
      children: [
        TextField(
          controller: _titleController,
          decoration: const InputDecoration(
            hintText: 'Title',
            border: OutlineInputBorder(),
          ),
          maxLength: 128, // max amount of characters accepted is 128
        ),
        TextField(
          controller: _ideaController,
          decoration: const InputDecoration(
            hintText: 'What is your next big idea?',
            border: OutlineInputBorder(),
          ),
          maxLength: 1024, // max amount of characters accepted is 1024
        ),
        Align(
          alignment: Alignment.centerLeft,
          child: MaterialButton(
            onPressed: () {
              addIdea(title, idea);
              // checks if both title and idea field are filled before adding
              if (_titleController.text != '' && _ideaController.text != '') {
                title = _titleController.text;
                idea = _ideaController.text;
                // Currently this string cant be used because it always returns 1. Backend needs to return id of newIdea
           
                addIdea(title, idea);
                print('After adding an idea'+ routes.user_id + '.');
                // This is temporary, until backend POST /messages route can return the id of the new Idea so we can GET idea by id
                IdeaObj newIdea = IdeaObj(
                    id: 0000,
                    title: title,
                    message: idea,
                    votes: 0,
                    userId: routes.user_id,
                    userVotes: 0,
                    username: SignInToGetUsername.username.substring(0,SignInToGetUsername.username.indexOf('@') ));
                    
                print('After adding an idea'+ routes.user_id + '.');
                schedule.submitIdea = newIdea; // submits idea and notifies list listeners
                _ideaController.clear();
                _titleController.clear();
              }
            },
            
            color: Colors.brown,
            child: const Text('Add', style: TextStyle(color: Colors.white)),
          ),
        ),
      ],
    ));
  }
}
