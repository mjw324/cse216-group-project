import 'package:flutter/foundation.dart';
import 'ideaobj.dart';
import 'routes.dart';

// Resource for schedule functionality: 
// https://brewyourtech.com/complete-guide-to-changenotifier-in-flutter/
class MySchedule with ChangeNotifier {
  List<IdeaObj> _ideas = <IdeaObj>[];

  // getter
  List<IdeaObj> get ideas => _ideas;

  // setter
  set ideasList(List<IdeaObj> list) {
    _ideas = list;
    notifyListeners(); // Notifies widgets affected by state change
  }
}
