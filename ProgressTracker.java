package tracker;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProgressTracker {
    public static Scanner scanner = new Scanner(System.in);

    private final String[] courses = {"Java", "DSA", "Databases", "Spring"};
    private final int[] maxPointsOfCourses = {600, 400, 480, 550}; //(Java, DSA, Databases, Spring)

    private final Map<Integer, String> names; //id, name
    private final Map<Integer, String> emails; //id, email
    private final Map<Integer, int[]> notes; //id, notes (Java, DSA, Databases, Spring)
    private final List<String> notifications;
    private final List<Integer> notificatedStudents;
    private final int[] submitions = new int[4];
    private int lastId;

    ProgressTracker() {
        names = new HashMap<>();
        emails = new HashMap<>();
        notes = new HashMap<>();
        notifications = new ArrayList<>();
        notificatedStudents = new ArrayList<>();
        lastId = 0;
    }

    boolean isPositiveInteger(String s) {
        String regex = "[0-9]+";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(s);

        return m.matches();
    }

    boolean isCorrectName(String name) {
        String re = "[A-Za-z]([ '-]?[A-Za-z]+)+";
        Pattern pattern = Pattern.compile(re);
        Matcher matcher = pattern.matcher(name);

        return matcher.matches();
    }

    boolean isCorrectEmail(String email) {
        String re = "[a-z0-9.]+@[a-z0-9]+\\.[a-z0-9]+";
        Pattern pattern = Pattern.compile(re);
        Matcher matcher = pattern.matcher(email);

        return matcher.matches();
    }

    void addStudent(String firstName, String lastName, String email) {
        if (emails.containsValue(email)) {
            System.out.println("This email is already taken.");
            return;
        }

        names.put(lastId, firstName + " " + lastName);
        emails.put(lastId, email);
        notes.put(lastId, new int[]{0, 0, 0, 0});
        System.out.println("The student has been added.");

        lastId++;
    }

    String isCorrectCredentials(String firstName, String lastName, String email) {
        boolean fN = isCorrectName(firstName);
        boolean lN = isCorrectName(lastName);
        boolean e = isCorrectEmail(email);

        if (!fN) return "Incorrect first name.";
        if (!lN) return "Incorrect last name.";
        if (!e) return "Incorrect email.";

        return "correct";
    }

    List<String> divideInputStudent(String student) { //firstName, lastName, email or Incorrect credentials
        int idxFirstBlank = 0, idxLastBlank = student.length() - 1;
        while (idxFirstBlank < idxLastBlank && student.charAt(idxFirstBlank) != ' ') idxFirstBlank++;
        while (idxFirstBlank < idxLastBlank && student.charAt(idxLastBlank) != ' ') idxLastBlank--;

        if (student.isBlank() || idxFirstBlank == idxLastBlank) return new ArrayList<>(List.of("Incorrect credentials"));

        String firstName = student.substring(0, idxFirstBlank);
        String lastName = student.substring(idxFirstBlank + 1, idxLastBlank);
        String email = student.substring(idxLastBlank + 1);

        return new ArrayList<>(Arrays.asList(firstName, lastName, email));
    }

    void addingStudent() {
        System.out.println("Enter student credentials or 'back' to return:");
        String student = "";

        while (true) {
            student = scanner.nextLine();
            if (student.equals("back")) {
                System.out.println("Total " + names.size() + " students have been added.");
                return;
            }

            List<String> studentData = divideInputStudent(student);
            if (studentData.get(0).equals("Incorrect credentials")) {
                System.out.println(studentData.get(0));
                continue;
            }

            String check = isCorrectCredentials(studentData.get(0), studentData.get(1), studentData.get(2));
            if (check.equals("correct")) {
                addStudent(studentData.get(0), studentData.get(1), studentData.get(2));
            }
            else {
                System.out.println(check);
            }
        }
    }

    String createNotification(int studentId, String course) {
        return "To: " + emails.get(studentId) + "\nRe: Your Learning Progress\nHello, " + names.get(studentId) + "! You have accomplished our " + course + " course!";

    }

    void sendNotification(int studentId, int courseId) {
        String notification = createNotification(studentId, courses[courseId]);
        notifications.add(notification);
        if (!notificatedStudents.contains(studentId)) notificatedStudents.add(studentId);
    }

    void addingPoints() {
        System.out.println("Enter an id and points or 'back' to return:");

        while (true) {
            String[] input = scanner.nextLine().split(" ");
            if (input[0].equals("back")) return;

            if (input.length != 5 || !isPositiveInteger(input[1]) || !isPositiveInteger(input[2]) || !isPositiveInteger(input[3]) || !isPositiveInteger(input[4])) {
                System.out.println("Incorrect points format.");
                continue;
            }
            if (!isPositiveInteger(input[0]) || !notes.containsKey(Integer.parseInt(input[0]))) {
                System.out.printf("No student is found for id = %s.\n", input[0]);
                continue;
            }

            int id = Integer.parseInt(input[0]);
            int[] points = new int[4];
            points[0] = Integer.parseInt(input[1]);
            points[1] = Integer.parseInt(input[2]);
            points[2] = Integer.parseInt(input[3]);
            points[3] = Integer.parseInt(input[4]);

            for (int i = 0; i < points.length; i++) {
                if (points[i] > 0) submitions[i]++;
                if (points[i] + notes.get(id)[i] >= maxPointsOfCourses[i] &&
                    notes.get(id)[i] < maxPointsOfCourses[i]) {
                    sendNotification(id, i);
                }
            }

            notes.compute(id, (k, noteList) -> new int[]{points[0] + noteList[0], points[1] + noteList[1], points[2] + noteList[2], points[3] + noteList[3]});
            System.out.println("Points updated");
        }
    }

    void findingStudent() {
        System.out.println("Enter an id or 'back' to return:");

        while (true) {
            String input = scanner.nextLine();
            if (input.equals("back")) return;

            int id = Integer.parseInt(input);
            if (!notes.containsKey(id)) {
                System.out.printf("No student is found for id=%d\n", id);
            }

            int[] noteList = notes.get(id);
            System.out.printf("%d points: Java=%d; DSA=%d; Databases=%d; Spring=%d;\n", id, noteList[0], noteList[1], noteList[2], noteList[3]);
        }
    }

    void listStudents() {
        if (names.isEmpty()) {
            System.out.println("No students found.");
            return;
        }

        System.out.println("Students:");
        for (Integer id : names.keySet()) {
            System.out.println(id);
        }
    }

    List<String> getMostPopularCourses() {
        int[] res = new int[4];
        for (int[] noteList : notes.values()) {
            for (int i = 0; i < 4; i++) {
                if (noteList[i] > 0) res[i]++;
            }
        }

        int max = res[0];
        for (int i = 1; i < 4; i++) if (res[i] > max) max = res[i];

        if (max == 0) return new ArrayList<>();

        List<String> mostPopular = new ArrayList<>();
        for (int i = 0; i < 4; i++) if (res[i] == max) mostPopular.add(courses[i]);

        return mostPopular;
    }

    List<String> getLeastPopularCourses() {
        boolean isEnrolledStudent = false;
        int[] res = new int[4];
        for (int[] noteList : notes.values()) {
            for (int i = 0; i < 4; i++) {
                if (noteList[i] == 0) res[i]++;
                if (noteList[i] > 0) isEnrolledStudent = true;
            }
        }

        int max = res[0];
        for (int i = 1; i < 4; i++) if (res[i] > max) max = res[i];

        if (!isEnrolledStudent) return new ArrayList<>();

        List<String> leastPopular = new ArrayList<>();
        for (int i = 0; i < 4; i++) if (res[i] == max) leastPopular.add(courses[i]);

        return (leastPopular.size() < 4 ? leastPopular : new ArrayList<>());
    }

    List<String> getHighestActivityCourses() {
        int max = submitions[0];
        for (int i = 1; i < 4; i++) if (submitions[i] > max) max = submitions[i];

        if (max == 0) return new ArrayList<>();

        List<String> highestActivity = new ArrayList<>();
        for (int i = 0; i < 4; i++) if (submitions[i] == max) highestActivity.add(courses[i]);

        return highestActivity;
    }

    List<String> getLowestActivityCourses() {
        boolean wasSubmition = false;
        int min = submitions[0];
        for (int i = 1; i < 4; i++) {
            if (submitions[i] < min) min = submitions[i];
            if (submitions[i] > 0) wasSubmition = true;
        }

        if (!wasSubmition) return new ArrayList<>();

        List<String> lowestActivity = new ArrayList<>();
        for (int i = 0; i < 4; i++) if (submitions[i] == min) lowestActivity.add(courses[i]);

        return (lowestActivity.size() < 4 ? lowestActivity : new ArrayList<>());
    }

    void calculateAveragePoints(int[] res, int[] enrolledStudents) {
        for (int[] noteList : notes.values()) {
            for (int i = 0; i < 4; i++) {
                if (noteList[i] > 0) {
                    enrolledStudents[i]++;
                    res[i] += noteList[i];
                }
            }
        }
    }

    List<String> getEasiestCourses() {
        int[] res = new int[4];
        int[] enrolledStudents = new int[4];
        calculateAveragePoints(res, enrolledStudents);

        float max = (float) res[0] / enrolledStudents[0];
        for (int i = 1; i < 4; i++) {
            float now = (float) res[i] / enrolledStudents[i];
            if (now > max) max = now;
        }

        List<String> easiest = new ArrayList<>();
        for (int i = 0; i < 4; i++) if ((float) res[i] / enrolledStudents[i] == max) easiest.add(courses[i]);

        return easiest;
    }

    List<String> getHardestCourses() {
        int[] res = new int[4];
        int[] enrolledStudents = new int[4];
        calculateAveragePoints(res, enrolledStudents);

        float min = (float) res[0] / enrolledStudents[0];
        for (int i = 1; i < 4; i++) {
            float now = (float) res[i] / enrolledStudents[i];
            if (now < min) min = now;
        }

        List<String> hardest = new ArrayList<>();
        for (int i = 0; i < 4; i++) if ((float) res[i] / enrolledStudents[i] == min) hardest.add(courses[i]);

        return hardest;
    }

    void printListOfCourses(List<String> courses) {
        if (courses.isEmpty()) {
            System.out.println("n/a");
            return;
        }

        System.out.print(courses.get(0));
        for (int i = 1; i < courses.size(); i++) {
            System.out.print(", " + courses.get(i));
        }
        System.out.println();
    }

    void printCourseStatistics(String course) {
        int courseId;
        if (course.equals("Java")) courseId = 0;
        else if (course.equals("DSA")) courseId = 1;
        else if (course.equals("Databases")) courseId = 2;
        else if (course.equals("Spring")) courseId = 3;
        else return;

        List<Map.Entry<Integer, int[]>> list = new ArrayList<>(notes.entrySet());
        list.removeIf(entry -> entry.getValue()[courseId] == 0);
        list.sort((entry1, entry2) -> {
            int compare = Integer.compare(entry2.getValue()[courseId], entry1.getValue()[courseId]);
            if (compare == 0) {
                return Integer.compare(entry1.getKey(), entry2.getKey());
            }
            return compare;
        });

        System.out.println(course);
        System.out.println("id \t points \t completed");
        for (Map.Entry<Integer, int[]> entry : list) {
            Integer studentId = entry.getKey();
            int[] studentNotes = entry.getValue();

            float percent = (float) studentNotes[courseId] / maxPointsOfCourses[courseId] * 100;
            BigDecimal bd = new BigDecimal(Float.toString(percent));
            bd = bd.setScale(1, RoundingMode.HALF_UP);
            percent = bd.floatValue();
            System.out.printf("%-4d \t %-6d \t %.1f%%\n", studentId, studentNotes[courseId], percent);
        }
    }

    void printJavaStatistics() {
        printCourseStatistics("Java");
    }

    void printDSAStatistics() {
        printCourseStatistics("DSA");
    }

    void printDatabasesStatistics() {
        printCourseStatistics("Databases");
    }

    void printSpringStatistics() {
        printCourseStatistics("Spring");
    }

    void viewStatistics() {
        System.out.println("Type the name of a course to see details or 'back' to quit:");

        System.out.print("Most popular: "); printListOfCourses(getMostPopularCourses());
        System.out.print("Least popular: "); printListOfCourses(getLeastPopularCourses());
        System.out.print("Highest activity: "); printListOfCourses(getHighestActivityCourses());
        System.out.print("Lowest activity: "); printListOfCourses(getLowestActivityCourses());
        System.out.print("Easiest course: "); printListOfCourses(getEasiestCourses());
        System.out.print("Hardest course: "); printListOfCourses(getHardestCourses());

        while (true) {
            String course = scanner.nextLine();
            if (course.equals("back")) break;
            else if (course.equalsIgnoreCase("java")) printJavaStatistics();
            else if (course.equalsIgnoreCase("dsa")) printDSAStatistics();
            else if (course.equalsIgnoreCase("databases")) printDatabasesStatistics();
            else if (course.equalsIgnoreCase("spring")) printSpringStatistics();
            else System.out.println("Unknown course.");
        }
    }

    void getNotifications() {
        for (String notification : notifications) System.out.println(notification);
        System.out.println("Total " + notificatedStudents.size() + " students have been notified.");
        notifications.clear();
        notificatedStudents.clear();
    }

    void exit() {
        System.out.println("Bye!");
    }
}
