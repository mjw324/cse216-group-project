import 'package:flutter/foundation.dart';
import 'package:my_app/userobj.dart';
import 'ideaobj.dart';

// Resource for schedule functionality:
// https://brewyourtech.com/complete-guide-to-changenotifier-in-flutter/
class MySchedule with ChangeNotifier {
  List<IdeaObj> _ideas = <IdeaObj>[];
  int sessionId = 1;
  List<IdeaObj> get ideas => _ideas;
  IdeaObj getIdea(int idx) {
    for (var idea in _ideas) {
      if (idea.id == idx) {
        return idea;
      }
    }
    throw Exception('Not a valid id');
  }
 
  // addIdea used during construction
  void addIdea(IdeaObj idea) {
    _ideas.add(idea);
  }

  // Used when addIdeaWidget submits idea, updates state
  set submitIdea(IdeaObj idea) {
    _ideas.add(idea);
    notifyListeners();
  }
  void setVotes(int idx, int voteChange, int userVote) {
    for (var idea in _ideas) {
      if (idea.id == idx) {
        idea.votes += voteChange;
        // voteChange can't be equal to userVotes. Ex: voteChange is -2 from selecting upvote to downvote, but userVotes is -1 (downvote)
        idea.userVotes = userVote;
      }
    }
    notifyListeners(); // notify has all widgets who consume the schedule update their state
  }

  // setter
  set ideasList(List<IdeaObj> list) {
    _ideas = list;
  }
}
