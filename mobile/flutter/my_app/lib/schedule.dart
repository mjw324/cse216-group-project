import 'package:flutter/foundation.dart';

class MySchedule with ChangeNotifier {
  double _stateManagementTime = 0.5;
  // getter
  double get stateManagementTime => _stateManagementTime;

  // setter
  set stateManagementTime(double newValue) {
    _stateManagementTime = newValue;
    notifyListeners(); // Notifies widgets affected by state change
  }
}
