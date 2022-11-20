import 'dart:io';
import 'package:file_picker/file_picker.dart';
import 'package:dio/dio.dart';
import 'package:flutter/material.dart';
import 'package:path_provider/path_provider.dart';

/*void main() {
  runApp(MyApp());
}*/

class FilePickerWidget extends StatelessWidget {
  const FilePickerWidget({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        body: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              TextButton(
                  onPressed: () {
                    uploadFile();
                  },
                  child: const Text("Upload File")),
            ],
          ),
        ),
      ),
    );
  }
}

Future uploadFile() async {
  var dio = Dio();

  FilePickerResult? result = await FilePicker.platform.pickFiles();

  if(result!=null){
    File file = File(result.files.single.path ?? " ");

    String filename = file.path.split('/').last;

    String filepath = file.path;

  FormData data = FormData.fromMap({
    'key': 'your key here',
    'image': await MultipartFile.fromFile(filepath, filename: filename)
  });

  var response = await dio.post("https://api.imgbb.com/1/upload", data: data,
  onSendProgress:(int sent, int total){
    print('$sent, $total');
  } );

  print(response.data);


  }else {
    print("Result is null");
  }
}