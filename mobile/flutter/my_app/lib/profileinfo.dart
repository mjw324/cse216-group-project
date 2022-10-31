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
    String SO = 'Sexual Orientation';
  String GI = 'Gender Identity';
  String note = 'Note';
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
          decoration: InputDecoration(
            hintText: SO,
            border: const OutlineInputBorder(),
          ),
          maxLength: 128, // max amount of characters accepted is 128
        ),
        TextField(
          controller: _goController,
          decoration: InputDecoration(
            hintText: GI,
            border: const OutlineInputBorder(),
          ),
          maxLength: 128, // max amount of characters accepted is 128
        ),
        TextField(
          controller: _noteController,
          decoration: InputDecoration(
            hintText: note,
            border: const OutlineInputBorder(),
          ),
          maxLength: 1024, // max amount of characters accepted is 128
        ),
        Align(
          alignment: Alignment.centerLeft,
          child: MaterialButton(
            onPressed: () {
              // checks if both title and idea field are filled before adding
              if (_goController.text != '' && _siController.text != '') {
                GI = _goController.text;
                print(GI);
                SO = _siController.text;
                print(SO);
                note = _noteController.text;
                print(note);
                sendProfile(username, SO, GI, note);
                print(sendProfile(username, SO, GI, note));
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
