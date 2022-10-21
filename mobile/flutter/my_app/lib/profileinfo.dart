import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'routes.dart';
import 'schedule.dart';
import 'ideaobj.dart';

class AddProfileWidget extends StatefulWidget {
  const AddProfileWidget({super.key});

  @override
  State<AddProfileWidget> createState() => _AddProfileWidget();
}

class _AddProfileWidget extends State<AddProfileWidget> {
  // title and idea controller are used to manage text input in respective fields
  final _usernameController = TextEditingController();
  final _emailController = TextEditingController();
  final _siController = TextEditingController();
  final _goController = TextEditingController();
  final _noteController = TextEditingController();
  String username = '';
  String email = '';
  String SI = '';
  String GO = '';
  String note = '';

  @override
  Widget build(BuildContext context) {
    // Instantiate schedule using Provider.of, this is so we can access methods from schedule
    final schedule = Provider.of<MySchedule>(context);
    return SliverToBoxAdapter(
        child: Column(
      children: [
        TextField(
          controller: _usernameController,
          decoration: const InputDecoration(
            hintText: 'username',
            border: OutlineInputBorder(),
          ),
          maxLength: 128, // max amount of characters accepted is 128
        ),
        TextField(
          controller: _emailController,
          decoration: const InputDecoration(
            hintText: 'email',
            border: OutlineInputBorder(),
          ),
          maxLength: 128, // max amount of characters accepted is 128
        ),
        TextField(
          controller: _siController,
          decoration: const InputDecoration(
            hintText: 'Sexual Identity',
            border: OutlineInputBorder(),
          ),
          maxLength: 128, // max amount of characters accepted is 128
        ),
        TextField(
          controller: _goController,
          decoration: const InputDecoration(
            hintText: 'Gender Orientation',
            border: OutlineInputBorder(),
          ),
          maxLength: 128, // max amount of characters accepted is 128
        ),
        TextField(
          controller: _noteController,
          decoration: const InputDecoration(
            hintText: 'Note',
            border: OutlineInputBorder(),
          ),
          maxLength: 1024, // max amount of characters accepted is 128
        ),
        Align(
          alignment: Alignment.centerLeft,
          child: MaterialButton(
            onPressed: () {
              // checks if both title and idea field are filled before adding
              if (_goController.text != '' && _siController.text != '') {
                GO = _goController.text;
                SI = _siController.text;
                // Currently this string cant be used because it always returns 1. Backend needs to return id of newIdea
                
              }
               
            },
            color: Colors.blueGrey,
            child: const Text('Save', style: TextStyle(color: Colors.white)),
          ),
        ),
      ],
    ));
  }
}
