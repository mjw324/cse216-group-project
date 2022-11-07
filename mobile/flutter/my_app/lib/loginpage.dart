import 'dart:async';
import 'dart:convert' show base64Decode, json;
import 'ideapage.dart';
import 'schedule.dart';
import 'package:provider/provider.dart';

import 'package:flutter/material.dart';
import 'package:google_sign_in/google_sign_in.dart';
import 'package:http/http.dart' as http;
import 'routes.dart';
class SignInToGetUsername{
  static String username = ''; 
}
// This dart file takes care of the login page with Google
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
    Map sessionId;
    int sessionID_int = 0;
     _googleSignIn.signIn().then((result){
          result?.authentication.then((googleKey) async {
              print(_googleSignIn.currentUser!.displayName);
              print(googleKey.idToken);
              //Need to include the term await to wait for the server's response
              sessionId = await sendToken(googleAuth.idToken);
              sessionID_int = routes.sessionId;
              
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
  
  // Handles sign out of the user
  Future<void> _handleSignOut() => _googleSignIn.disconnect();

  Widget _buildBody(BuildContext context) {
 final schedule = Provider.of<MySchedule>(context);
    

    final GoogleSignInAccount? user = _currentUser;
    if (user != null) {
      //Once the user has signed in, the email and display name is shown
      // Was getting errors with the image
      String e = user.email;
      SignInToGetUsername.username = e;
      String? name = user.displayName;
      String? url = user.photoUrl;
      String username= e.substring(0, e.indexOf('@')) + " and Name: " + name!;

      return Column(
        mainAxisAlignment: MainAxisAlignment.spaceAround,
        children: <Widget>[
          //the user profile gave errors thus it is commented out
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
            style: ElevatedButton.styleFrom( backgroundColor: Colors.brown.shade600),
            child: const Text('SIGN OUT')
            
          ),
          //Once signed in, there is a button that takes the user to the 
          //home page of the The Buzz. Send the username, email, and sessionId
          ElevatedButton(
            style: ElevatedButton.styleFrom( backgroundColor: Colors.brown.shade600),
          child: const Text('The Buzz Homepage'),
          onPressed: () {
            schedule.sessionId= routes.sessionId; 
            //print(schedule.sessionId);
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
            style: ElevatedButton.styleFrom( backgroundColor: Colors.brown),
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
          backgroundColor: Colors.brown.shade600,
        ),
        body: ConstrainedBox(
          constraints: const BoxConstraints.expand(),
          child: _buildBody(context),
        ));
  }

  
}

