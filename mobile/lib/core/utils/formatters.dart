import 'package:intl/intl.dart';

class Formatters {
  static final currencyFormat = NumberFormat.simpleCurrency(locale: 'en_KE');
  static final numberFormat = NumberFormat.decimalPattern('en_KE');
  static final phoneFormat = NumberFormat.simpleCurrency(locale: 'en_KE');

  static String formatCurrency(double amount) {
    return currencyFormat.format(amount);
  }

  static String formatNumber(num number) {
    return numberFormat.format(number);
  }

  static String formatDate(DateTime date) {
    return DateFormat('dd MMM yyyy').format(date);
  }

  static String formatDateTime(DateTime date) {
    return DateFormat('dd MMM yyyy, hh:mm a').format(date);
  }

  static String formatPhone(String phone) {
    final digits = phone.replaceAll(RegExp(r'[^0-9]'), '');
    if (digits.startsWith('254')) {
      return '+${digits.substring(0, 3)} ${digits.substring(3, 6)} ${digits.substring(6)}';
    }
    if (digits.startsWith('0')) {
      return '+254 ${digits.substring(1, 4)} ${digits.substring(4)}';
    }
    return phone;
  }

  static String capitalize(String text) {
    if (text.isEmpty) return text;
    return text[0].toUpperCase() + text.substring(1).toLowerCase();
  }
}
