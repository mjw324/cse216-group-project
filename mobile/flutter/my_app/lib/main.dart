import 'package:flutter/material.dart';
import 'ideapage.dart';
import 'package:english_words/english_words.dart';
import 'dart:developer' as developer;
import 'package:http/http.dart' as http;
import 'dart:convert';

void main() {
  runApp(const MyApp());
}

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
        // This is the theme of your application.
        //
        // Try running your application with "flutter run". You'll see the
        // application has a blue toolbar. Then, without quitting the app, try
        // changing the primarySwatch below to Colors.green and then invoke
        // "hot reload" (press "r" in the console where you ran "flutter run",
        // or simply save your changes to "hot reload" in a Flutter IDE).
        // Notice that the counter didn't reset back to zero; the application
        // is not restarted.
        primarySwatch: Colors.brown,
      ),
      home: const MyHomePage(title: 'The Buzz Idea Page'),
    );
  }
}



// class HttpReqWords extends StatefulWidget {
//   const HttpReqWords({Key? key}) : super(key: key);

//   @override
//   State<HttpReqWords> createState() => _HttpReqWordsState();
// }

// class _HttpReqWordsState extends State<HttpReqWords> {
//   late Future<List<NumberWordPair>> _future_list_numword_pairs;

//   final _biggerFont = const TextStyle(fontSize: 18);

//   @override
//   void initState() {
//     super.initState();
//     _future_list_numword_pairs = fetchNumberWordPairs();
//   }

//   void _retry() {
//     setState(() {
//       _future_list_numword_pairs = fetchNumberWordPairs();
//     });
//   }

//   @override
//   Widget build(BuildContext context) {
//     var fb = FutureBuilder<List<NumberWordPair>>(
//       future: _future_list_numword_pairs,
//       builder:
//           (BuildContext context, AsyncSnapshot<List<NumberWordPair>> snapshot) {
//         Widget child;

//         if (snapshot.hasData) {
//           // developer.log('`using` ${snapshot.data}', name: 'my.app.category');
//           // create  listview to show one row per array element of json response
//           child = ListView.builder(
//               //shrinkWrap: true, //expensive! consider refactoring. https://api.flutter.dev/flutter/widgets/ScrollView/shrinkWrap.html
//               padding: const EdgeInsets.all(16.0),
//               itemCount: snapshot.data!.length,
//               itemBuilder: /*1*/ (context, i) {
//                 return Column(
//                   children: <Widget>[
//                     ListTile(
//                       title: Text(
//                         "row ${i}: num=${snapshot.data![i].num} str=${snapshot.data![i].str}",
//                         // snapshot.data![i].str,
//                         style: _biggerFont,
//                       ),
//                     ),
//                     Divider(height: 1.0),
//                   ],
//                 );
//               });
//         } else if (snapshot.hasError) {
//           // newly added
//           child = Text('${snapshot.error}');
//         } else {
//           // awaiting snapshot data, return simple text widget
//           // child = Text('Calculating answer...');
//           child = const CircularProgressIndicator(); //show a loading spinner.
//         }
//         return child;
//       },
//     );

//     return fb;
//   }
// }

// // method for trying out a long-running calculation
// Future<List<String>> doSomeLongRunningCalculation() async {
//   return getWebData();
// }

// Future<List<String>> getWebData() async {
//   developer.log('Making web request...');
//   // var url = Uri.http('www.cse.lehigh.edu', '~spear/courses.json');
//   var url = Uri.parse(
//       'http://www.cse.lehigh.edu/~spear/courses.json'); // list of strings
//   // var url = Uri.parse('http://www.cse.lehigh.edu/~spear/5k.json');      // list of maps
//   // var url = Uri.parse('https://jsonplaceholder.typicode.com/albums/1'); // single map
//   var headers = {"Accept": "application/json"}; // <String,String>{};

//   var response = await http.get(url, headers: headers);

//   developer.log('Response status: ${response.statusCode}');
//   developer.log('Response headers: ${response.headers}');
//   developer.log('Response body: ${response.body}');

//   final List<String> returnData;
//   if (response.statusCode == 200) {
//     // If the server did return a 200 OK response, then parse the JSON.
//     var res = jsonDecode(response.body);
//     print('json decode: $res');

//     if (res is List) {
//       returnData = (res as List<dynamic>).map((x) => x.toString()).toList();
//     } else if (res is Map) {
//       returnData = <String>[(res as Map<String, dynamic>).toString()];
//     } else {
//       developer
//           .log('ERROR: Unexpected json response type (was not a List or Map).');
//       returnData = List.empty();
//     }
//   } else {
//     throw Exception(
//         'Failed to retrieve web data (server returned ${response.statusCode})');
//   }

//   return returnData;
// }

/// Create object from data like: http://www.cse.lehigh.edu/~spear/5k.json
// class NumberWordPair {
//   /// The string representation of the number
//   final String str;

//   /// The int representation of the number
//   final int num;

//   const NumberWordPair({
//     required this.str,
//     required this.num,
//   });

//   factory NumberWordPair.fromJson(Map<String, dynamic> json) {
//     return NumberWordPair(
//       str: json['str'],
//       num: json['num'],
//     );
//   }
// }

// Future<List<NumberWordPair>> fetchNumberWordPairs() async {
//   final response =
//       await http.get(Uri.parse('http://www.cse.lehigh.edu/~spear/5k.json'));

//   if (response.statusCode == 200) {
//     // If the server did return a 200 OK response, then parse the JSON.
//     final List<NumberWordPair> returnData;
//     var res = jsonDecode(response.body);
//     print('json decode: $res');

//     if (res is List) {
//       returnData = (res as List<dynamic>)
//           .map((x) => NumberWordPair.fromJson(x))
//           .toList();
//     } else if (res is Map) {
//       returnData = <NumberWordPair>[
//         NumberWordPair.fromJson(res as Map<String, dynamic>)
//       ];
//     } else {
//       developer
//           .log('ERROR: Unexpected json response type (was not a List or Map).');
//       returnData = List.empty();
//     }
//     return returnData;
//   } else {
//     // If the server did not return a 200 OK response,
//     // then throw an exception.
//     throw Exception('Did not receive success status code from request.');
//   }
// }


