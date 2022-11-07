import 'package:flutter/material.dart';
import 'package:my_app/schedule.dart';
import 'package:provider/provider.dart';
import 'loginpage.dart';

void main() async {
  //runApp(const MyApp());
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