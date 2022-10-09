import 'package:flutter/foundation.dart';
import 'ideaobj.dart';

// Resource for schedule functionality:
// https://brewyourtech.com/complete-guide-to-changenotifier-in-flutter/
class MySchedule with ChangeNotifier {
  List<IdeaObj> _ideas = <IdeaObj>[];

  // getter
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

  void setVotes(int idx, int voteChange) {
    for (var idea in _ideas) {
      if (idea.id == idx) {
        idea.votes += voteChange;
        // voteChange can't be equal to userVotes. Ex: voteChange is -2 from selecting upvote to downvote, but userVotes is -1 (downvote)
        idea.userVotes = voteChange == 0 ? 0 : (voteChange < 0 ? -1 : 1);
      }
    }
    notifyListeners(); // notify has all widgets who consume the schedule update their state
  }

  // setter
  set ideasList(List<IdeaObj> list) {
    _ideas = list;
  }
}
