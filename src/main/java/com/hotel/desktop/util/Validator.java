package com.hotel.desktop.util;

import java.util.ArrayList;
import java.util.List;

public class Validator {

    public static List<String> validateLogin(String login) {
        List<String> errors = new ArrayList<>();
        if (login == null || login.isBlank()) {
            errors.add("Логин обязателен для заполнения");
            return errors;
        }
        if (login.length() < 4 || login.length() > 20) {
            errors.add("Логин должен содержать от 4 до 20 символов");
        }
        if (!login.matches("^[A-Za-z][A-Za-z0-9]{3,19}$")) {
            errors.add("Логин должен начинаться с буквы и содержать только латинские буквы");
        }
        return errors;
    }

    public static List<String> validatePhone(String phone) {
        List<String> errors = new ArrayList<>();
        if (phone == null || phone.isBlank()) {
            errors.add("Телефон обязателен для заполнения");
            return errors;
        }
        String cleaned = phone.replaceAll("[\\s-]", "");
        if (!cleaned.matches("^(\\+7|8)\\d{10}$")) {
            errors.add("Телефон должен начинаться с 8 или +7 и содержать 11 цифр");
        }
        return errors;
    }

    public static List<String> validateEmail(String email) {
        List<String> errors = new ArrayList<>();
        if (email == null || email.isBlank()) {
            errors.add("Почта обязательна для заполнения");
            return errors;
        }
        if (!email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            errors.add("Неверный формат почты");
        }
        return errors;
    }

    public static List<String> validatePassword(String password) {
        List<String> errors = new ArrayList<>();
        if (password == null || password.isBlank()) {
            errors.add("Пароль обязателен для заполнения");
            return errors;
        }
        if (password.length() < 8 || password.length() > 128) {
            errors.add("Пароль должен быть от 8 до 128 символов");
        }
        if (!password.matches(".*[0-9].*")) {
            errors.add("Пароль должен содержать хотя бы одну цифру");
        }
        if (!password.matches(".*[a-z].*")) {
            errors.add("Пароль должен содержать хотя бы одну строчную латинскую букву");
        }
        if (!password.matches(".*[A-Z].*")) {
            errors.add("Пароль должен содержать хотя бы одну заглавную латинскую букву");
        }
        if (!password.matches(".*[~!?@#$%^&*_\\-+()\\[\\]{}></\\\\|\"'.,:;].*")) {
            errors.add("Пароль должен содержать хотя бы один спецсимвол");
        }
        if (password.contains(" ")) {
            errors.add("Пароль не должен содержать пробелы");
        }
        return errors;
    }

    public static List<String> validateName(String name, String fieldName, int min, int max) {
        List<String> errors = new ArrayList<>();
        if (name == null || name.isBlank()) {
            errors.add(fieldName + " обязательно для заполнения");
            return errors;
        }
        if (!name.matches("^[A-Za-z]{" + min + "," + max + "}$")) {
            errors.add(fieldName + " должно содержать от " + min + " до " + max + " латинских букв");
        }
        return errors;
    }

    public static List<String> validateRegister(
            String lastName, String firstName, String middleName,
            String email, String phone, String login,
            String password, String confirmPassword) {

        List<String> errors = new ArrayList<>();
        errors.addAll(validateName(lastName, "Фамилия", 3, 25));
        errors.addAll(validateName(firstName, "Имя", 3, 20));

        if (middleName != null && !middleName.isBlank()) {
            if (!middleName.matches("^[A-Za-z]{10,30}$")) {
                errors.add("Отчество должно содержать от 10 до 30 латинских букв");
            }
        }

        errors.addAll(validateEmail(email));
        errors.addAll(validatePhone(phone));
        errors.addAll(validateLogin(login));
        errors.addAll(validatePassword(password));

        if (password != null && confirmPassword != null && !password.equals(confirmPassword)) {
            errors.add("Введенные пароли не совпадают");
        }
        return errors;
    }
}
