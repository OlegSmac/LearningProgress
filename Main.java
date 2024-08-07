package tracker;

import static tracker.ProgressTracker.scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("Learning Progress Tracker");

        ProgressTracker pt = new ProgressTracker();
        String command;
        while (true) {
            command = scanner.nextLine();

            if (command.isBlank()) System.out.println("No input.");
            else if (command.equals("add students")) pt.addingStudent();
            else if (command.equals("add points")) pt.addingPoints();
            else if (command.equals("find")) pt.findingStudent();
            else if (command.equals("list")) pt.listStudents();
            else if (command.equals("statistics")) pt.viewStatistics();
            else if (command.equals("notify")) pt.getNotifications();
            else if (command.equals("back")) System.out.println("Enter 'exit' to exit the program.");
            else if (command.equals("exit")) {
                pt.exit();
                break;
            }
            else System.out.println("Error: unknown command!");
        }
    }
}
