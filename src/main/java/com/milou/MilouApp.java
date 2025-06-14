package com.milou;

import com.milou.models.Email;
import com.milou.models.User;
import com.milou.services.AuthService;
import com.milou.services.EmailService;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class MilouApp {

    private static final Scanner scanner = new Scanner(System.in);
    private static final AuthService authService = new AuthService();
    private static final EmailService emailService = new EmailService();

    public static void main(String[] args) {
        LogManager.getLogManager().reset();
        Logger.getLogger("org.hibernate").setLevel(Level.WARNING);

        while (true) {
            System.out.println("\n--- Milou Email Service ---");
            System.out.println("[L]ogin, [S]ign up, [E]xit:");
            String input = scanner.nextLine().trim().toLowerCase();

            switch (input) {
                case "l", "login" -> login();
                case "s", "signup" -> signUp();
                case "e", "exit" -> {
                    System.out.println("Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid input.");
            }
        }
    }

    private static void login() {
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        User user = authService.login(email, password);
        if (user != null) {
            System.out.println("\nWelcome back, " + user.getName() + "!");
            showUserCommands(user);
        } else {
            System.out.println("Invalid email or password. Please try again.");
        }
    }

    private static void signUp() {
        System.out.print("Name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Email (without @milou.com): ");
        String email = scanner.nextLine().trim();

        System.out.print("Password (min 8 characters): ");
        String password = scanner.nextLine().trim();

        try {
            authService.signUp(name, email, password);
            System.out.println("Your new account is created. Go ahead and login!");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void showUserCommands(User user) {
        while (true) {
            System.out.println("\n--- Main Menu ---");
            System.out.println("[S]end, [V]iew Emails, [R]eply, [F]orward, [L]ogout");
            String command = scanner.nextLine().trim().toLowerCase();

            switch (command) {
                case "s" -> sendEmail(user);
                case "v" -> viewEmailMenu(user);
                case "r" -> replyEmail(user);
                // case "f" -> forwardEmail(user); // برای پیاده‌سازی در آینده
                case "l" -> {
                    System.out.println("Logging out...");
                    return;
                }
                default -> System.out.println("Invalid command.");
            }
        }
    }

    private static void viewEmailMenu(User user) {
        while (true) {
            System.out.println("\n--- View Emails Menu ---");
            System.out.println("[A]ll, [U]nread, [S]ent, [C]ode, [B]ack to Main Menu");
            String choice = scanner.nextLine().trim().toLowerCase();

            switch (choice) {
                case "a" -> showEmailList("All Emails:", emailService.allMails(user.getId()));
                case "u" -> showEmailList("Unread Emails:", emailService.unreadMails(user.getId()));
                case "s" -> showEmailList("Sent Emails:", emailService.sentMails(user.getId()));
                case "c" -> readByCode(user);
                case "b" -> {
                    return;
                }
                default -> System.out.println("Invalid input.");
            }
        }
    }

    private static void showEmailList(String title, List<Email> emails) {
        System.out.println("\n--- " + title + " ---");
        if (emails.isEmpty()) {
            System.out.println("No emails to show.");
        } else {
            for (Email email : emails) {
                System.out.println("+ " + email.getSender().getEmail() + " - " + email.getSubject() + " (" + email.getMessage_code() + ")");
            }
        }
    }

    private static void readByCode(User user) {
        System.out.print("Enter Code: ");
        String code = scanner.nextLine().trim();
        if (code.isEmpty()) return;

        Optional<Email> emailOptional = emailService.readByCode(user.getId(), code);

        if (emailOptional.isPresent()) {
            System.out.println("\n--- Email Details ---");
            System.out.println(emailOptional.get());
        } else {
            System.out.println("Email not found or you don't have permission.");
        }
    }

    private static void sendEmail(User sender) {
        System.out.print("Recipient(s) (separate with comma): ");
        String recipientsInput = scanner.nextLine().trim();
        if (recipientsInput.isEmpty()) return;

        System.out.print("Subject: ");
        String subject = scanner.nextLine().trim();

        System.out.print("Body: ");
        String body = scanner.nextLine().trim();

        List<String> recipientEmails = Arrays.asList(recipientsInput.split("\\s*,\\s*"));
        try {
            emailService.sendEmail(sender, recipientEmails, subject, body);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void replyEmail(User user) {
        System.out.print("Code of the email to reply to: ");
        String code = scanner.nextLine().trim();
        if (code.isEmpty()) return;

        System.out.print("Body of your reply: ");
        String body = scanner.nextLine().trim();

        try {
            emailService.replyToEmail(user, code, body);
            System.out.println("Reply sent successfully.");
        } catch (Exception e) {
            System.out.println("Error replying to email: " + e.getMessage());
        }
    }
}