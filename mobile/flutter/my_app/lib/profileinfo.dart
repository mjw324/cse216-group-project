import 'dart:html';

import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'routes.dart';
import 'schedule.dart';
import 'ideaobj.dart';
import 'profileobj.dart';

class AddProfileWidget extends StatefulWidget {
  String name, email;
  AddProfileWidget({super.key, required this.name, required this.email});

  @override
  State<AddProfileWidget> createState() => _AddProfileWidget(name: name, userEmail: email);
}

class _AddProfileWidget extends State<AddProfileWidget> {
  late Future<List<ProfileObj>> _listProfile;
  String name;
  String userEmail; 
  _AddProfileWidget({required this.name, required this.userEmail});
  final _siController = TextEditingController();
  final _goController = TextEditingController();
  final _noteController = TextEditingController();
     

 
  // List of items in our dropdown menu
  var items = [  
    '',
    'Heterosexual or Straight',
    'Lesbian or gay',
    'Bisexual',
    'Queer',
    'Pansexual',
    'Questioning',
    'Something else',
    'Don’t know',
    'Decline to answer',];
  
  var genderIdentity = [
    '',  
    'Male',
    'Female',
    'Transgender man/Trans man',
    'Transgender woman/trans woman',
    'Genderqueer/gender nonconforming neither exclusively male nor female',
    'Additional gender category (or other)',
    'Decline to answer',
    ];

  @override
 
  Widget build(BuildContext context) 
  {
     final schedule1 = Provider.of<MySchedule>(context);
    // Creates scheduleList to access current ideas list stored in schedule
    List<ProfileObj> scheduleList = schedule1.profile;
     return FutureBuilder<List<ProfileObj>>(
              future: _listProfile= routes.fetchProfile(),
              builder: ((BuildContext context,
                  AsyncSnapshot<List<ProfileObj>> snapshot) {
                Widget child;
                  if (snapshot.hasData) {
                    List<ProfileObj> list = snapshot.data!;
                  schedule1.profileList = list;
                      ProfileObj prof = list[0];

                      //Intializers-> this is what is going to be shown on the dropdown
                      String value1 = prof.GI;
                      String value2 = prof.SO;
                      //Username and email are set and the user can not change it
                      String username = name;
                      String email = userEmail;
                      //Below are values the user can change and is what is sent to backend to change in the database
                      String note = 'Note';
                      String sexualOrientationVal = 'Sexual Orientation';  
                      String genderIdentityVal = 'Gender Identity';
                  return Scaffold(

                    appBar: AppBar(title: const Text(
                        "Edit your Profile!",
                      ),
                      backgroundColor: Colors.brown,
                      centerTitle: true,
                    ),
                    body: Center(
                      child: Column(
                        mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                        children: <Widget>[
                          Text(
                            style: const TextStyle(height: 2, fontSize: 20),
                            'Username: $username'
                          ),
                          Text(
                              style: const TextStyle(height: 2, fontSize: 20),
                            'email: $email'
                          ),
                        DropdownButtonFormField(
                        value: prof.GI,
                            icon: const Icon(Icons.keyboard_arrow_down),
                            items: genderIdentity.map((String items) {
                              return DropdownMenuItem(
                                value: items,
                                child: Text(items),
                              );
                            }).toList(),
                            onChanged: (val) {
                              genderIdentityVal = val as String;
                              print(genderIdentityVal);
                              value1 = val;
                              (value1) =>val;
                              },
                              dropdownColor: Colors.blue,
                              decoration: const InputDecoration(labelText: "Gender Identity"),
                            ),
                      DropdownButtonFormField(
                        value: value2,
                            icon: const Icon(Icons.keyboard_arrow_down),
                            items: items.map((String items) {
                              return DropdownMenuItem(
                                value: items,
                                child: Text(items),
                              );
                            }).toList(),
                            onChanged: (val) {
                              sexualOrientationVal = val as String;
                              value2 = val;
                              sexualOrientationVal = val as String;
                                (value2) => val;
                              },
                              dropdownColor: Colors.blue,
                              decoration: const InputDecoration(labelText: "Sexual Orientation"),
                            ),
                        TextField(
                        controller: _noteController,
                        decoration: InputDecoration(
                          hintText: prof.note,
                          border: const OutlineInputBorder(),
                        ),
                        maxLength: 1024, // max amount of characters accepted is 128
                      ),
                    Align(
                        alignment: Alignment.centerLeft,
                        child: MaterialButton(
                          onPressed: () {
                            // checks if both title and idea field are filled before adding
                            if (genderIdentityVal != '' && sexualOrientationVal != '') {
                              print(genderIdentityVal);
                              print(sexualOrientationVal);
                              note = _noteController.text;
                              print(note);
                              sendProfile(username, sexualOrientationVal, genderIdentityVal, note);
                              print(sendProfile(username, sexualOrientationVal, genderIdentityVal, note));
                              // Currently this string cant be used because it always returns 1. Backend needs to return id of newIdea
                              
                            }
                          },
                          color: Colors.blueGrey,
                          child: const Text('Save', style: TextStyle(color: Colors.white)),
                        ),
                      ),
                    ],
                  )
                      
                        
                  ));

                        }else if (snapshot.hasError) {
                        child =  Text('${snapshot.error}');
                      } else {
                        child = const Center(
                                child: SizedBox(
                                    width: 30,
                                    height: 30,
                                    child: CircularProgressIndicator()));
                      }
                      return child;
                } 
                )
                );
                  
   
}

}
