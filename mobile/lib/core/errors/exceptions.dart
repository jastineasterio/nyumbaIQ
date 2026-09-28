class ServerException implements Exception {
  final String message;
  final int? statusCode;
  ServerException(this.message, [this.statusCode]);

  @override
  String toString() => 'ServerException: $message (status: $statusCode)';
}

class CacheException implements Exception {
  final String message;
  CacheException(this.message);

  @override
  String toString() => 'CacheException: $message';
}

class NetworkException implements Exception {
  final String message;
  NetworkException(this.message);

  @override
  String toString() => 'NetworkException: $message';
}

class AuthException implements Exception {
  final String message;
  AuthException(this.message);

  @override
  String toString() => 'AuthException: $message';
}

class ValidationException implements Exception {
  final Map<String, String> errors;
  ValidationException(this.errors);

  @override
  String toString() => 'ValidationException: $errors';
}

class ApiError {
  final int status;
  final String error;
  final String message;
  final String path;
  final Map<String, String>? validationErrors;

  ApiError({
    required this.status,
    required this.error,
    required this.message,
    required this.path,
    this.validationErrors,
  });

  factory ApiError.fromJson(Map<String, dynamic> json) {
    return ApiError(
      status: json['status'] ?? 0,
      error: json['error'] ?? 'Unknown Error',
      message: json['message'] ?? 'An unexpected error occurred',
      path: json['path'] ?? '',
      validationErrors: json['validationErrors'] != null
          ? Map<String, String>.from(json['validationErrors'])
          : null,
    );
  }
}
