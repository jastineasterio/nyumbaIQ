abstract class Failure {
  final String message;
  Failure(this.message);
}

class ServerFailure extends Failure {
  ServerFailure(String message) : super(message);
}

class CacheFailure extends Failure {
  CacheFailure(String message) : super(message);
}

class NetworkFailure extends Failure {
  NetworkFailure(String message) : super(message);
}

class AuthFailure extends Failure {
  AuthFailure(String message) : super(message);
}

class ValidationFailure extends Failure {
  final Map<String, String> errors;
  ValidationFailure(String message, this.errors) : super(message);
}
