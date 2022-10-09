import 'package:flutter/material.dart';
import 'ideapage.dart';

void main() {
  runApp(const MyApp());
}
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
