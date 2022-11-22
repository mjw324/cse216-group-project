import 'package:flutter/material.dart';
import 'package:my_app/schedule.dart';
import 'package:provider/provider.dart';
import 'loginpage.dart';
import 'package:flutter/material.dart';
//import 'package:firebase_core/firebase_core.dart';
//import 'firebase_options.dart';  
//import 'screens/homepage.dart';  
//import 'package:firebase_core/firebase_core.dart';  

void main() async {
  //runApp(const MyApp());
  //WidgetsFlutterBinding.ensureInitialized(); 
  runApp(
     ChangeNotifierProvider(
      create: (_) => MySchedule(),
      child:
      const MaterialApp(
      title: 'Google Sign In',
      home: SignIn(),
    ),
      )
    
  );
}

// TO RUN: flutter run -d chrome --web-port=7357
// Press R to reload
class MyApp extends StatelessWidget {
  const MyApp({super.key});

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'The Buzz',
      // this is to hide the pesky-no-good debug banner. Not very tasteful
      debugShowCheckedModeBanner: false,
      theme: ThemeData(
        // This is the theme of the application.
        primarySwatch: Colors.brown,
      ),
      // home is the default route of the app, consider it '/' in web routes
      home: const SignIn(),
    );
  }
}
  
/*void main() {  
  WidgetsFlutterBinding.ensureInitialized();  
  runApp(App());  
}  */
  
/*class App1 extends StatelessWidget {  
  // Create the initialization Future outside of `build`:  
  final Future<FirebaseApp> _initialization = Firebase.initializeApp();  
  
  @override  
  Widget build(BuildContext context) {  
    return FutureBuilder(  
      // Initialize FlutterFire:  
  future: _initialization,  
      builder: (context, snapshot) {  
        // Check for errors  
  if (snapshot.hasError) {  
          return Text('Error in Firebase Initilisation');  
        }  
        // Once complete, show your application  
  if (snapshot.connectionState == ConnectionState.done) {  
          return MaterialApp(  
            title: 'file',  
            home: const SignIn(),  
          );  
        }  
        // Otherwise, show something whilst waiting for initialization to complete  
  return CircularProgressIndicator();  
      },  
    );  
  }  
}*/