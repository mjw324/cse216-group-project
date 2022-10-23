import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'routes.dart';
import 'schedule.dart';
import 'ideaobj.dart';

class AddProfileWidget extends StatefulWidget {
  String name, email;
  AddProfileWidget({super.key, required this.name, required this.email});

  @override
  State<AddProfileWidget> createState() => _AddProfileWidget(name: name, userEmail: email);
}

class _AddProfileWidget extends State<AddProfileWidget> {
  String name;
  String userEmail; 
  _AddProfileWidget({required this.name, required this.userEmail});
  // title and idea controller are used to manage text input in respective fields
  final _usernameController = TextEditingController();
  final _emailController = TextEditingController();
  final _siController = TextEditingController();
  final _goController = TextEditingController();
  final _noteController = TextEditingController();
  

  @override
  Widget build(BuildContext context) {
    String username = name;
    String email = userEmail;
    String SI = '';
    String GO = '';
    String note = '';
    // Instantiate schedule using Provider.of, this is so we can access methods from schedule
    final schedule = Provider.of<MySchedule>(context);
    return SliverToBoxAdapter(
        child: Column(
      children: [
        Text(
          style: const TextStyle(height: 2, fontSize: 20),
          'Username: $username'
        ),
        Text(
          style: const TextStyle(height: 2, fontSize: 20),
          'email: $email'
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
