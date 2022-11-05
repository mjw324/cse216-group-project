import 'package:flutter/material.dart';
import 'routes.dart'; // is this necessary?
import 'package:provider/provider.dart';
import 'schedule.dart';

// ignore: must_be_immutable
class VoteButtonWidget extends StatefulWidget {
  VoteButtonWidget({super.key, required this.idx, required this.liked});
  // liked is the value of current user votes. -1 is downvote, 1 is upvote, 0 is no votes
  // Currently needed when user adds another idea, the vote status on each idea remain the same when list is updated
  // With addition of multiple users, this needs refactored
  int liked;

  // idx is the idea id tied to the respective VoteButtonWidget instance
  final int idx;

  @override
  State<VoteButtonWidget> createState() => _VoteButtonWidgetState();
}

class _VoteButtonWidgetState extends State<VoteButtonWidget> {
  @override
  Widget build(BuildContext context) {
    return Consumer<MySchedule>(
        builder: (context, schedule, _) => SizedBox(
            width: 100,
            child: Row(
              children: [
                SizedBox(
                    width: 30,
                    child: Center(
                        child: Text(
                            schedule.getIdea(widget.idx).votes.toString(),
                            style: const TextStyle(
                                fontSize: 18, fontWeight: FontWeight.bold)))),
                Column(
                  children:[
                    Flexible(
                    child: MaterialButton(
                  minWidth: 10,
                  height: 20,
                  color: widget.liked == 1 ? Colors.red[300] : Colors.grey[850],
                  child: const Icon(Icons.arrow_upward_outlined, size: 30, color: Colors.white),
                  onPressed: () => {
                    print(widget.liked),
                    setState(() => {
                          if (widget.liked == -1)
                            {
                              // Will have to upvote twice to make up for previous downvote
                              widget.liked = 1,
                              // voteIdea() is from routes.dart, where we send the vote POST to the Heroku server
                              voteIdea(widget.idx, true, 2),
                              // this updates the Votes on the schedule idea list
                              schedule.setVotes(widget.idx, 2,widget.liked),
                              
                              print(widget.liked),
                            }
                          else if (widget.liked == 1)
                            {
                              // Will remove upvote if already upvoted
                              widget.liked = 0,
                              voteIdea(widget.idx, false, 1),
                              schedule.setVotes(widget.idx, -1,widget.liked),
                              print(widget.liked),
                            }
                          else
                            {
                              // Will upvote if it hasn't been downvoted or upvoted
                              widget.liked = 1,
                              voteIdea(widget.idx, true, 1),
                              schedule.setVotes(widget.idx,
                                  1,widget.liked),
                                print(widget.liked), // updates schedule idea list so count updates
                            }
                        })
                  },
                )
                    ),
                //const SizedBox(
                  //  width: 4), // Invis. Box between Upvote and Downvote
                  Flexible(
                child: MaterialButton(
                  minWidth: 10,
                  height: 20,
                  color: widget.liked == -1
                      ? Colors.indigo[300]
                      : Colors.grey[850],
                  child: const Icon(Icons.arrow_downward_outlined, size: 30, color: Colors.white),
                  onPressed: () => {
                    print(widget.liked),
                    setState(() => {
                          if (widget.liked == 1)
                            {
                              // Will have to downvote twice to make up for previous upvote
                              widget.liked = -1,
                              voteIdea(widget.idx, false, 2),
                              schedule.setVotes(widget.idx, -2, widget.liked),
                              
                    print(widget.liked),
                            }
                          else if (widget.liked == -1)
                            {
                              // Will remove downvote if already downvoted
                              widget.liked = 0,
                              voteIdea(widget.idx, true, 0),
                              schedule.setVotes(widget.idx, 1,widget.liked),
                              
                    print(widget.liked),
                            }
                          else
                            {
                              // Will downvote if it hasn't been downvoted or upvoted
                              widget.liked = -1,
                              voteIdea(widget.idx, false, 1),
                              schedule.setVotes(widget.idx, -1,widget.liked),
                              
                    print(widget.liked),
                            }
                        })
                  },
                ),
                    )
                  ]
                ),
              ],
            )));
  }
}
