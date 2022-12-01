import 'dart:typed_data';

import 'package:dio/dio.dart';
import 'package:flutter/material.dart';
//import 'package:flutter_document_picker/flutter_document_picker.dart';
import 'package:path/path.dart';
import 'package:provider/provider.dart';
import 'routes.dart';
import 'schedule.dart';
import 'ideaobj.dart';
import 'loginpage.dart';
import 'package:flutter/widgets.dart';
import 'package:file_picker/file_picker.dart';
import 'dart:io' as Io;
import 'dart:async';
import 'dart:io';
import 'dart:convert';
import 'dart:io';  
//import 'dart:math';
//import 'package:flutter/material.dart';
//import 'package:open_file/open_file.dart';

class AddIdeaWidget extends StatefulWidget {
  const AddIdeaWidget({super.key});

  @override
  State<AddIdeaWidget> createState() => _AddIdeaWidgetState();
}

class _AddIdeaWidgetState extends State<AddIdeaWidget> {
  // title and idea controller are used to manage text input in respective fields
  final _titleController = TextEditingController();
  final _ideaController = TextEditingController();
  String title = '';
  String idea = '';
  String imageEncoded = '';
  String url1 = '';

  @override
  Widget build(BuildContext context) {
    // Instantiate schedule using Provider.of, this is so we can access methods from schedule
    final schedule = Provider.of<MySchedule>(context);
    return SliverToBoxAdapter(
        child: Column(
      children: [
        TextField(
          controller: _titleController,
          decoration: const InputDecoration(
            hintText: 'Title',
            border: OutlineInputBorder(),
          ),
          maxLength: 128, // max amount of characters accepted is 128
        ),
        TextField(
          controller: _ideaController,
          decoration:  InputDecoration(
            hintText: 'What is your next big idea?',
            border: OutlineInputBorder(),
            suffixIcon: IconButton(
              onPressed: (() async {
                uploadFile().then((value) => {imageEncoded = value});
              }),  
              icon: Icon(Icons.camera_alt_outlined),
            ),
          ),
          maxLength: 1024, // max amount of characters accepted is 1024
        ),
        //MaterialButton(onPressed: onPressed)
        Align(
          alignment: Alignment.centerLeft,
          child: MaterialButton(
            onPressed: () async {
              //addIdea(title, idea);
              // checks if both title and idea field are filled before adding
              if (_titleController.text != '' && _ideaController.text != '') {
                title = _titleController.text;
                idea = _ideaController.text;
                // Currently this string cant be used because it always returns 1. Backend needs to return id of newIdea
                //Add MEDIA IDEA WITH CONDITIONAL
                //print(imageEncoded);
                if(imageEncoded != ''){
                  url1 = await addIdeaMedia(title, idea, imageEncoded);//.then((value) => {url1 = value});
                  //print(routes.link + '\n');
                  //print('\n 111');
                } else{
                  addIdea(title, idea);
                  //print('\n 222');
                }

                print(url1+ '\n');
                String newidea = idea + ' ' + url1;
                print(newidea);
                print('After adding an idea' + routes.user_id + '.');
                // This is temporary, until backend POST /messages route can return the id of the new Idea so we can GET idea by id
                IdeaObj newIdea = IdeaObj(
                    id: 0000,
                    title: title,
                    message: newidea,
                    link: url1,
                    votes: 0,
                    userId: routes.user_id,
                    userVotes: 0,
                    username: SignInToGetUsername.username.substring(
                        0, SignInToGetUsername.username.indexOf('@')));

                print('After adding an idea' + routes.user_id + '.');
                schedule.submitIdea =
                    newIdea; // submits idea and notifies list listeners
                _ideaController.clear();
                _titleController.clear();
              }
            },
            color: Color.fromARGB(255, 195, 134, 206),
            child: const Text('Add', style: TextStyle(color: Colors.white)),
          ),
        ),
      ],
    ));
  }
}

Future uploadFile() async{
  final result = await FilePicker.platform.pickFiles();
  if (result == null) return '';
  Uint8List uploadfile = result.files.single.bytes!;
  String fileName = result.files.single.name;
  String base64File = base64.encode(uploadfile);
  String ext = fileName.split(".").last;
  if(ext == 'jpeg'){
    base64File = 'image/jpeg-$fileName-$base64File';
  }
  if(ext == 'pdf'){
    base64File = 'application/pdf-$fileName-$base64File';
  }
  if(ext == 'mp3'){
    base64File = 'audio/mpeg-$fileName-$base64File';
  }
  if(ext == 'mp4'){
    base64File = 'video/mp4-$fileName-$base64File';
  }
  //print(base64File);
  return base64File;
}
