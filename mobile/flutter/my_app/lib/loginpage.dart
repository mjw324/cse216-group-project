// Copyright 2013 The Flutter Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

// ignore_for_file: public_member_api_docs

import 'dart:async';
import 'dart:convert' show base64Decode, json;
import 'ideapage.dart';
import 'schedule.dart';
import 'package:provider/provider.dart';

import 'package:flutter/material.dart';
import 'package:google_sign_in/google_sign_in.dart';
import 'package:http/http.dart' as http;
import 'routes.dart';

GoogleSignIn _googleSignIn = GoogleSignIn(

   
  // Optional clientId
  //clientId: '429689065020-h43s75d9jahb8st0jq8cieb9bctjg850.apps.googleusercontent.com',
  scopes: <String>[
    'email',
  ],
);

void main() {
  runApp(
    const MaterialApp(
      title: 'Google Sign In',
      home: SignIn(),
    ),
  );
}

class SignIn extends StatefulWidget {
  const SignIn({Key? key}) : super(key: key);

  @override
  State createState() => SignInState();
}


class SignInState extends State<SignIn> {
  GoogleSignInAccount? _currentUser;
  
  @override
  void initState() {
    super.initState();
    _googleSignIn.onCurrentUserChanged.listen((GoogleSignInAccount? account) {
      setState(() {
        _currentUser = account;
      });
      
    });
    _googleSignIn.signInSilently();
  }

  



  Future<int> _handleSignIn() async {
    
    try {
      await _googleSignIn.signIn();
    } catch (error) {
      print(error);
    }
    
    final GoogleSignInAccount? googleUser = await _googleSignIn.signIn();
    final GoogleSignInAuthentication googleAuth = await googleUser!.authentication;
    //print (googleAuth.idToken);
    Future<int> session;
    Future<Map> sessionId;
    int sessionID_int = 0;
     _googleSignIn.signIn().then((result){
          result?.authentication.then((googleKey){
              print(_googleSignIn.currentUser!.displayName);
              print(googleKey.idToken);
              //final schedule = Provider.of<MySchedule>(context,listen: true);
              print('this is before send token');
              sessionId = sendToken(googleAuth.idToken);
              print(routes.sessionId);
              sessionID_int = routes.sessionId;
              //int actualsessionId;
              //print(addIdea('title', 'message'));
             // session= sendToken(googleKey.idToken!);
             // session.then((value) => print(value));
              //sessionId.then((value) =>  {sessionID_int = value});
              //print(actualsessionId);
              
          }).catchError((err){
            print(err);
          });
      }
      ).catchError((err){
          print('error occured');
      });
      await Future.delayed(Duration(milliseconds: 300));
      return sessionID_int;
  }
  

  Future<void> _handleSignOut() => _googleSignIn.disconnect();

  Widget _buildBody(BuildContext context) {
 final schedule = Provider.of<MySchedule>(context);
    

    final GoogleSignInAccount? user = _currentUser;
    if (user != null) {
      String e = user.email;
      String? name = user.displayName;
      String? url = user.photoUrl;
      print(user.photoUrl);
      String username= e.substring(0, e.indexOf('@')) + " and Name: " + name!;
      //sendToken(user._idToken);
      return Column(
        mainAxisAlignment: MainAxisAlignment.spaceAround,
        children: <Widget>[
           ListTile(
          //   leading: GoogleUserCircleAvatar(
          //     identity: user,
          //     placeholderPhotoUrl: user.photoUrl,
          //   ),
            title: Text(user.displayName ?? ''),
            subtitle: Text(user.email),
            
          ),
          const Text('Signed in successfully.'),
          ElevatedButton(
            onPressed: _handleSignOut,
            child: const Text('SIGN OUT'),
          ),
          
          ElevatedButton(
          child: const Text('The Buzz Homepage'),
          onPressed: () {
            print('this is from the elevated button');
           routes.sessionId = schedule.sessionId; 
            print(schedule.sessionId);
            Navigator.of(context).push(
              MaterialPageRoute(
                builder: (context) =>  TabBarDemo(
                  name: username, 
                  email: user.email, 
                  session: schedule.sessionId,
                  )),
            );
          },
        ),
          
        ],
      );
    } else {
      Future<int> number;
      return Column(
        mainAxisAlignment: MainAxisAlignment.spaceAround,
        children: <Widget>[
          const Text('You are not currently signed in.'),
          ElevatedButton(
            onPressed: ()=> {
               number =  _handleSignIn(),
               number.then((value) => {schedule.sessionId = value, print(value)}),
              },

            child: const Text('SIGN IN'),
          ),
        ],
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        appBar: AppBar(
          title: const Text('The Buzz Google Sign In'),
          shadowColor: Colors.brown,
        ),
        body: ConstrainedBox(
          constraints: const BoxConstraints.expand(),
          child: _buildBody(context),
        ));
  }

  
}

