import 'package:flutter/material.dart';
import 'ideapage.dart';

void main() async {
  runApp(const MyApp());
}

// TODO:
// Update addidea.dart once backend fixes POST /messages
// ideaslist.dart was hard to optimize, it needs some more work
// Convert stateful widgets to stateless (except for votebutton), since MySchedule takes care of the state.
// Simple app state management https://docs.flutter.dev/development/data-and-backend/state-mgmt/simple
// Debug Build with F5
// Try running your application with "flutter run"
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
      home: const MyHomePage(title: 'The Buzz Idea Page'),
    );
  }
}
