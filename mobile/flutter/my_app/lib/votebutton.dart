import 'package:flutter/material.dart';
import 'routes.dart'; // is this necessary?

class VoteButtonWidget extends StatefulWidget {
  const VoteButtonWidget({super.key, required this.idx, required this.votes});
  final int idx;
  final int votes;
  // Constructor that requires widget instance to pass through idea id (needed for vote routes)

  @override
  State<VoteButtonWidget> createState() => _VoteButtonWidgetState();
}

class _VoteButtonWidgetState extends State<VoteButtonWidget> {
  //String _votes = "-";
  // Both of these boolean values are used to determine state of vote buttons
  bool _upvotePressed = false;
  bool _downvotePressed = false;
  _VoteButtonWidgetState() {
    // voteCounter(widget.idx).then((val) => setState((() {
    //       _votes = val;
    //     })));
  }

  @override
  Widget build(BuildContext context) {
    int votes = widget.votes;
    // Refactor this with MySchedule
    // voteCounter(widget.idx).then((val) => setState((() {
    //       _votes = val;
    //     })));
    return SizedBox(
        width: 100,
        child: Row(
          children: [
            SizedBox(
                width: 30,
                child: Center(
                    child: Text(votes.toString(),
                        style: const TextStyle(
                            fontSize: 18, fontWeight: FontWeight.bold)))),
            MaterialButton(
              minWidth: 40,
              height: 50,
              color: _upvotePressed ? Colors.red[300] : Colors.grey[850],
              child: const Text(
                '↑',
                style: TextStyle(fontSize: 15, color: Colors.white),
              ),
              onPressed: () => {
                setState(() => {
                      if (_downvotePressed)
                        {
                          // Will have to upvote twice to make up for previous downvote
                          _downvotePressed = false,
                          _upvotePressed = true,
                          voteIdea(widget.idx, true, 2),
                          votes = votes + 2,
                        }
                      else if (_upvotePressed)
                        {
                          // Will remove upvote if already upvoted
                          _upvotePressed = false,
                          voteIdea(widget.idx, false, 1),
                          votes--,
                        }
                      else
                        {
                          // Will upvote if it hasn't been downvoted or upvoted
                          _upvotePressed = true,
                          voteIdea(widget.idx, true, 1),
                          votes++,
                        }
                    })
              },
            ),
            const SizedBox(width: 4), // Invis. Box between Upvote and Downvote
            MaterialButton(
              minWidth: 40,
              height: 50,
              color: _downvotePressed ? Colors.indigo[300] : Colors.grey[850],
              child: const Text(
                '↓',
                style: TextStyle(fontSize: 15, color: Colors.white),
              ),
              onPressed: () => {
                setState(() => {
                      if (_upvotePressed)
                        {
                          // Will have to downvote twice to make up for previous upvote
                          _upvotePressed = false,
                          _downvotePressed = true,
                          voteIdea(widget.idx, false, 2),
                          votes = votes - 2,
                        }
                      else if (_downvotePressed)
                        {
                          // Will remove downvote if already downvoted
                          _downvotePressed = false,
                          voteIdea(widget.idx, true, 1),
                          votes++,
                        }
                      else
                        {
                          // Will downvote if it hasn't been downvoted or upvoted
                          _downvotePressed = true,
                          voteIdea(widget.idx, false, 1),
                          votes--,
                        }
                    })
              },
            ),
          ],
        ));
  }
}