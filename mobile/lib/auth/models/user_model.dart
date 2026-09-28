class UserModel {
  final String id;
  final String firstName;
  final String? middleName;
  final String lastName;
  final String email;
  final String phone;
  final String? username;
  final String role;
  final String status;
  final String? profilePhotoUrl;
  final DateTime? lastLogin;
  final DateTime createdAt;

  UserModel({
    required this.id,
    required this.firstName,
    this.middleName,
    required this.lastName,
    required this.email,
    required this.phone,
    this.username,
    required this.role,
    required this.status,
    this.profilePhotoUrl,
    this.lastLogin,
    required this.createdAt,
  });

  String get fullName => '$firstName ${middleName ?? ''} $lastName'.trim();

  factory UserModel.fromJson(Map<String, dynamic> json) {
    return UserModel(
      id: json['id'] as String,
      firstName: json['firstName'] as String,
      middleName: json['middleName'] as String?,
      lastName: json['lastName'] as String,
      email: json['email'] as String,
      phone: json['phone'] as String,
      username: json['username'] as String?,
      role: json['role'] as String,
      status: json['status'] as String,
      profilePhotoUrl: json['profilePhotoUrl'] as String?,
      lastLogin: json['lastLogin'] != null
          ? DateTime.tryParse(json['lastLogin'])
          : null,
      createdAt: DateTime.parse(json['createdAt'] as String),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'firstName': firstName,
      'middleName': middleName,
      'lastName': lastName,
      'email': email,
      'phone': phone,
      'username': username,
      'role': role,
      'status': status,
      'profilePhotoUrl': profilePhotoUrl,
      'lastLogin': lastLogin?.toIso8601String(),
      'createdAt': createdAt.toIso8601String(),
    };
  }
}
