import 'package:google_sign_in/google_sign_in.dart';

class GoogleSignInApi{
  static final _clientIdWeb = '429689065020-0gt8auic7gbs1jrl4kq24v77al4fqtuk.apps.googleusercontent.com';
  static final _googleSignIn = GoogleSignIn(clientId: _clientIdWeb);

  static Future<GoogleSignInAccount?> login() => _googleSignIn.signIn();
}
