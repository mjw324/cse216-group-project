import 'package:flutter/foundation.dart';
import 'package:my_app/userobj.dart';
import 'ideaobj.dart';
import 'commentobj.dart';
import 'routes.dart';
import 'profileobj.dart';

// Resource for schedule functionality:
// https://brewyourtech.com/complete-guide-to-changenotifier-in-flutter/
class MySchedule with ChangeNotifier {
  List<IdeaObj> _ideas = <IdeaObj>[];
  List<CommentObj> _comment = <CommentObj>[];
  List<ProfileObj> _profile = <ProfileObj>[];
  int sessionId = routes.sessionId;
  List<IdeaObj> get ideas => _ideas;
  List<CommentObj> get comment => _comment;
  List<ProfileObj> get profile => _profile;
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

  void addComment(CommentObj comment){
    _comment.add(comment);
  }
  // Used when addIdeaWidget submits idea, updates state
  set submitIdea(IdeaObj idea) {
    _ideas.add(idea);
    notifyListeners();
  }

  set submitComment(CommentObj comment){
    _comment.add(comment);
    notifyListeners();
  }

  set editComment(CommentObj commentId){
    _comment.add(commentId);
    _comment.clear();
    notifyListeners();
  }
  void setVotes(int idx, int voteChange, int userVote) {
    for (var idea in _ideas) {
      if (idea.id == idx) {
        idea.votes += voteChange;
        // voteChange can't be equal to userVotes. Ex: voteChange is -2 from selecting upvote to downvote, but userVotes is -1 (downvote)
        idea.userVotes = userVote;
        if(idea.votes < 0){
          idea.votes = 0;
        }
      }
    }
    notifyListeners(); // notify has all widgets who consume the schedule update their state
  }

  // setter
  set ideasList(List<IdeaObj> list) {
    _ideas = list;
  }
  set commentList(List<CommentObj> comment){
    _comment = comment;
  }
  set profileList(List<ProfileObj> profile){
    _profile = profile;
  }

}
