package com.milou;

import com.milou.models.Email;
import com.milou.models.User;
import com.milou.services.AuthService;
import com.milou.services.EmailService;
import com.milou.utils.CodeGenerator;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.time.LocalDateTime;

public class MilouApp {

    private static final Scanner scanner = new Scanner(System.in);
    private static final AuthService auth = new AuthService();

    public static void main(String[] args) {

        LogManager.getLogManager().reset();
        Logger.getLogger("org.hibernate").setLevel(Level.WARNING);
        Logger.getLogger("com.mysql").setLevel(Level.WARNING);
        Logger.getLogger("org.hibernate.SQL").setLevel(Level.WARNING);
        Logger.getLogger("org.hibernate.type.descriptor.sql.BasicBinder").setLevel(Level.WARNING);

        while (true) {
            System.out.println("[L]ogin, [S]ign up:");
            String input = scanner.nextLine().trim().toLowerCase();

            if (input.equals("l") || input.equals("login")) {
                login();
            } else if (input.equals("s") || input.equals("sign up") || input.equals("signup")) {
                signUp();
            } else {
                System.out.println("Invalid input. Please enter 'L' for Login or 'S' for Sign up.");
            }
        }
    }


    private static void login() {
        System.out.println("Please enter your email address (with or without \"@milou.com\"): ");
        String email = scanner.nextLine();
        System.out.println("Please enter your password: ");
        String password = scanner.nextLine();

        try {
            User user = auth.login(email, password);
            if (user == null) {
                System.out.println("Invalid email or password. Please try again.");
                main(null);
            }
            showUserCommands(user);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void signUp() {
        System.out.print("Name: (or 'B' to go back to the view login menu): ");
        String name = scanner.nextLine().trim();

        if (name.equalsIgnoreCase("b")) {
            main(null);
            return;
        }


        System.out.print("Email: (with or without \"@milou.com\") (or 'B' to go back to the view login menu): ");
        String email = scanner.nextLine().trim();

        if (email.equalsIgnoreCase("b")) {
            main(null);
            return;
        }

        System.out.print("Password: (or 'B' to go back to the view login menu): ");
        String password = scanner.nextLine().trim();

        if (password.equalsIgnoreCase("b")) {
            main(null);
            return;
        }


        try {
            auth.signUp(name, email, password);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            main(null);
        }
        System.out.println("Your new account is created.");
        System.out.println("Go ahead and login!");
    }

    private static void showUserCommands(User user) {
        System.out.println("[S]end, [V]iew, [R]eply, [F]orward, [Q]uit");
        String command = scanner.nextLine().trim().toLowerCase();

        switch (command) {
            case "s":
                sendEmail(user);
                break;
            case "v":
                viewEmail(user);
                break;
            case "r":
                replyEmail(user);
                break;
            case "q":
                main(null);
                break;
        }

    }

    private static void replyEmail(User user) {
        System.out.println("Code of the email to reply to (or 'B' to go back):");
        String code = scanner.nextLine().trim();
        if (code.equalsIgnoreCase("b")) { showUserCommands(user); return; }

        System.out.println("Body of your reply:");
        String body = scanner.nextLine();

        EmailService emailService = new EmailService();
        try {
            emailService.replyToEmail(user, code, body);
        } catch (Exception e) {
            System.out.println("Error replying to email: " + e.getMessage());
        }

        showUserCommands(user);
    }


    private static void viewEmail(User user) {
        System.out.println("[A]ll, [U]nread, [S]ent, [C]ode (or 'B' to go back to the main menu):");
        String order = scanner.nextLine().trim().toLowerCase();

        switch (order) {
            case "a":
                allMail(user);
                break;
            case "u":
                unreadEmail(user);
                break;
            case "s":
                sentEmail(user);
                break;
            case "c":
                readByCode(user);
                break;
            case "b":
                showUserCommands(user);
                break;
            default:
                System.out.println("Invalid input.");
                viewEmail(user);
        }
    }


    private static void readByCode(User user) {
        System.out.println("Code (or 'B' to go back to the view mail menu): ");
        String code = scanner.nextLine().trim();
        if (code.equalsIgnoreCase("b")) {
            viewEmail(user);
            return;
        }

        Optional<Email> email = EmailService.readByCode(user.getId(), code);
        if (email.isEmpty()) {
            System.out.println("Email not found. Please try again.");
            readByCode(user);
            return;
        }

        System.out.println(email.get());
        viewEmail(user);
    }


    private static void sentEmail(User user) {
        List<Email> emails = EmailService.sentMails(user.getId());

        if (emails.isEmpty()) {
            System.out.println("No sent emails found.");
            viewEmail(user);
        }
        System.out.println("Sent Emails:");
        for (Email email : emails) {
            System.out.println("+ " + email.getSender().getEmail() + " - " + email.getSubject() + " (" + email.getMessage_code() + ")");
        }
        viewEmail(user);
    }

    private static void allMail(User user) {

        List<Email> emails = EmailService.allMails(user.getId());

        if (emails.isEmpty()) {
            System.out.println("No received emails found.");
            viewEmail(user);
        }
        System.out.println("All Emails:");
        for (Email email : emails) {
            System.out.println("+ " + email.getSender().getEmail() + " - " + email.getSubject() + " (" + email.getMessage_code() + ")");
        }
        viewEmail(user);
    }

    private static void unreadEmail(User user) {
        List<Email> emails = EmailService.unreadMails(user.getId());
        if (emails.isEmpty()) {
            System.out.println("No Unread emails found.");
            viewEmail(user);
        }
        System.out.println("Unread Emails:");
        for (Email email : emails) {
            System.out.println("+ " + email.getSender().getEmail() + " - " + email.getSubject() + " (" + email.getMessage_code() + ")");
        }
        viewEmail(user);
    }

    private static void sendEmail(User sender) {
        System.out.println("Recipient(s) (Separate with commas, or 'B' to go back):");
        String recipientsInput = scanner.nextLine().trim();
        if (recipientsInput.equalsIgnoreCase("b")) { showUserCommands(sender); return; }

        System.out.println("Subject:");
        String subject = scanner.nextLine().trim();

        System.out.println("Body:");
        String body = scanner.nextLine().trim();

        List<String> recipientEmails = Arrays.asList(recipientsInput.split(","));

        EmailService emailService = new EmailService();
        try {
            emailService.sendEmail(sender, recipientEmails, subject, body);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        showUserCommands(sender);
    }
}
