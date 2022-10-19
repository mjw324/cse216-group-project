// Copyright 2013 The Flutter Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

// ignore_for_file: public_member_api_docs

import 'dart:async';
import 'dart:convert' show json;
import 'ideapage.dart';
import 'loginpage.dart';

import 'package:flutter/material.dart';
import 'package:google_sign_in/google_sign_in.dart';
import 'package:http/http.dart' as http;
import 'routes.dart';

GoogleSignIn _googleSignIn = GoogleSignIn(

   
  // Optional clientId
  //clientId: '429689065020-0gt8auic7gbs1jrl4kq24v77al4fqtuk.apps.googleusercontent.com',
  scopes: <String>[
    'email',
  ],
);

void main() {
  runApp(
    const MaterialApp(
      title: 'Google Sign In',
      home: SignInDemo(),
    ),
  );
}

class SignInDemo extends StatefulWidget {
  const SignInDemo({Key? key}) : super(key: key);

  @override
  State createState() => SignInDemoState();
}

class SignInDemoState extends State<SignInDemo> {
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

  

 

  Future<void> _handleSignIn() async {
    try {
      await _googleSignIn.signIn();
    } catch (error) {
      print(error);
    }
     _googleSignIn.signIn().then((result){
          result?.authentication.then((googleKey){
              print(googleKey.accessToken);
              //sendToken(googleKey.accessToken);
              print(googleKey.idToken);
              print(_googleSignIn.currentUser!.displayName);
          }).catchError((err){
            print('inner error');
          });
      }).catchError((err){
          print('error occured');
      });
  }
  

  Future<void> _handleSignOut() => _googleSignIn.disconnect();

  Widget _buildBody() {
    

    final GoogleSignInAccount? user = _currentUser;
    if (user != null) {
      //sendToken(user._idToken);
      return Column(
        mainAxisAlignment: MainAxisAlignment.spaceAround,
        children: <Widget>[
          ListTile(
            leading: GoogleUserCircleAvatar(
              identity: user,
            ),
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
            Navigator.push(
              context,
              MaterialPageRoute(builder: (context) => const MyHomePage(title: 'The Buzz Idea Page')),
            );
          },
        ),
          
        ],
      );
    } else {
      return Column(
        mainAxisAlignment: MainAxisAlignment.spaceAround,
        children: <Widget>[
          const Text('You are not currently signed in.'),
          ElevatedButton(
            onPressed: _handleSignIn,

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
          child: _buildBody(),
        ));
  }
}