import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'routes.dart';
import 'schedule.dart';

class AddIdeaWidget extends StatefulWidget {
  const AddIdeaWidget({super.key});

  @override
  State<AddIdeaWidget> createState() => _AddIdeaWidgetState();
}

class _AddIdeaWidgetState extends State<AddIdeaWidget> {
  final _titleController = TextEditingController();
  final _ideaController = TextEditingController();
  String title = '';
  String idea = '';
  bool changesMade = false;

  @override
  Widget build(BuildContext context) {
    return Column(children: [
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
              // checks if both title and idea field are filled before adding
              if (_titleController.text != '' && _ideaController.text != '') {
                title = _titleController.text;
                idea = _ideaController.text;
                // Currently mMessage isn't being used, but will be useful in future for error handling
                Future<String> mMessage = addIdea(title, idea);
                _ideaController.clear();
                _titleController.clear();
              }
            },
            color: Colors.brown,
            child: const Text('Add', style: TextStyle(color: Colors.white)),
          ),
        ),
    ],);
  }
}