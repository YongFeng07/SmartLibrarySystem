package data;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class FileHandler {
    private static final String DATA_DIR = "data_files/";
    private static final String USER_FILE = DATA_DIR + "users.txt";
    private static final String LIBRARIAN_FILE = DATA_DIR + "librarians.txt";
    private static final String ITEM_FILE = DATA_DIR + "items.txt";
    private static final String BORROW_FILE = DATA_DIR + "borrows.txt";
    private static final String RESERVATION_FILE = DATA_DIR + "reservations.txt";
    private static final String ROOM_FILE = DATA_DIR + "rooms.txt";
    private static final String BOOKING_FILE = DATA_DIR + "bookings.txt";
    private static final String INTERLIBRARY_FILE = DATA_DIR + "interlibrary.txt";

    static {
        new File(DATA_DIR).mkdirs();
        initializeDefaultData();
    }

    private static void initializeDefaultData() {
        // 1. Default Librarian
        File librarianFile = new File(LIBRARIAN_FILE);
        if (!librarianFile.exists() || librarianFile.length() == 0) {
            appendLine(LIBRARIAN_FILE, "L001,Admin Librarian,admin123");
            System.out.println("✓ Default librarian: L001 / admin123");
        }
        
        // 2. Default Users (Password: 1234)
        File userFile = new File(USER_FILE);
        if (!userFile.exists() || userFile.length() == 0) {
            appendLine(USER_FILE, "Student,S001,Alex Tan,0123456789,alex@email.com,1234");
            appendLine(USER_FILE, "Student,S002,Ben Lim,0123456788,ben@email.com,1234");
            appendLine(USER_FILE, "Student,S003,Carol Wong,0123456787,carol@email.com,1234");
            appendLine(USER_FILE, "Faculty,F001,Dr. Smith,0198765432,smith@email.com,1234");
            appendLine(USER_FILE, "Faculty,F002,Prof. Lee,0198765431,lee@email.com,1234");
            appendLine(USER_FILE, "PublicMember,P001,David Chan,0112233445,david@email.com,1234");
            appendLine(USER_FILE, "PublicMember,P002,Emma Goh,0112233446,emma@email.com,1234");
            appendLine(USER_FILE, "PublicMember,P003,Fiona Ng,0112233447,fiona@email.com,1234");
            System.out.println("✓ 8 users created (password: 1234)");
        }
        
        // 3. Default Items (with Genre and EBook)
        File itemFile = new File(ITEM_FILE);
        if (!itemFile.exists() || itemFile.length() == 0) {
            appendLine(ITEM_FILE, "Book,1111111111,Java Programming,Alex Smith,Tech Press,2024,10,false,Programming");
            appendLine(ITEM_FILE, "Book,2222222222,Python Programming,Jane Doe,Code Press,2024,8,false,Programming");
            appendLine(ITEM_FILE, "Book,3333333333,Database Management,Bob Johnson,Data Press,2023,6,false,Database");
            appendLine(ITEM_FILE, "Book,4444444444,Web Development,Alice Brown,Web Press,2024,5,false,Web");
            appendLine(ITEM_FILE, "Book,5555555555,Data Structures,Charlie Kim,Algo Press,2023,4,false,CS");
            appendLine(ITEM_FILE, "Book,6666666666,Algorithms,David Park,Algo Press,2024,7,false,CS");
            appendLine(ITEM_FILE, "Book,7777777777,Software Engineering,Eva Chen,Eng Press,2024,3,false,Engineering");
            appendLine(ITEM_FILE, "Book,8888888888,Network Security,Frank Lin,Security Press,2023,5,false,Networking");
            appendLine(ITEM_FILE, "Book,9999999999,Artificial Intelligence,Grace Ho,AI Press,2024,6,false,AI");
            appendLine(ITEM_FILE, "Journal,1010101010,Science Today,Editors,Sci Press,2024,4,false,Science");
            appendLine(ITEM_FILE, "Journal,2020202020,Tech Review,Editors,Tech Press,2024,3,false,Tech");
            appendLine(ITEM_FILE, "DigitalResource,1212121212,Java API Docs,Oracle,Oracle,2024,20,false,Documentation");
            appendLine(ITEM_FILE, "DigitalResource,1313131313,Python Docs,Python Org,Python,2024,15,false,Documentation");
            // EBooks
            appendLine(ITEM_FILE, "EBook,1414141414,Clean Code,Robert Martin,Prentice Hall,2008,5,false,Programming,464,PDF");
            appendLine(ITEM_FILE, "EBook,1515151515,The Pragmatic Programmer,David Thomas,Addison-Wesley,1999,5,false,Programming,352,EPUB");
            System.out.println("✓ 15 items created (including 2 E-Books)");
        }
        
        // 4. Default Borrow Records (with lost book example)
        File borrowFile = new File(BORROW_FILE);
        if (!borrowFile.exists() || borrowFile.length() == 0) {
            LocalDate today = LocalDate.now();
            LocalDate dueDate1 = today.minusDays(5);
            LocalDate dueDate2 = today.plusDays(10);
            LocalDate dueDate3 = today.minusDays(3);
            LocalDate borrowDate1 = today.minusDays(15);
            LocalDate borrowDate2 = today.minusDays(5);
            LocalDate borrowDate3 = today.minusDays(10);
            LocalDate returnDate = today.minusDays(2);

            appendLine(BORROW_FILE, "1001,S001,1111111111," + borrowDate1 + "," + dueDate1 + ",null,2.50,OVERDUE,false,null");
            appendLine(BORROW_FILE, "1002,P001,3333333333," + borrowDate3 + "," + dueDate3 + ",null,3.00,OVERDUE,false,null");
            appendLine(BORROW_FILE, "1003,S002,2222222222," + borrowDate2 + "," + dueDate2 + ",null,0.00,BORROWED,false,null");
            appendLine(BORROW_FILE, "1004,F001,4444444444," + borrowDate2 + "," + dueDate2 + ",null,0.00,BORROWED,false,null");
            appendLine(BORROW_FILE, "1005,P002,5555555555," + borrowDate2 + "," + dueDate2 + ",null,0.00,BORROWED,false,null");
            appendLine(BORROW_FILE, "1006,S003,6666666666," + borrowDate1 + "," + dueDate1 + "," + returnDate + ",2.50,RETURNED,false,null");
            appendLine(BORROW_FILE, "1007,F002,7777777777," + borrowDate3 + "," + dueDate3 + "," + returnDate + ",3.00,RETURNED,false,null");
            // Lost book example
            appendLine(BORROW_FILE, "1008,P003,8888888888,2026-03-01,2026-03-22,null,50.00,LOST,true,2026-03-25");
            
            System.out.println("✓ 8 borrow records created (including 1 lost book)");
        }
        
        // 5. Default Reservations
        File reservationFile = new File(RESERVATION_FILE);
        if (!reservationFile.exists() || reservationFile.length() == 0) {
            appendLine(RESERVATION_FILE, "101,P001,1111111111," + LocalDate.now() + ",PENDING");
            appendLine(RESERVATION_FILE, "102,S002,3333333333," + LocalDate.now() + ",PENDING");
            appendLine(RESERVATION_FILE, "103,F002,8888888888," + LocalDate.now() + ",PENDING");
            System.out.println("✓ 3 reservations created");
        }
        
        // 6. Default Study Rooms
        File roomFile = new File(ROOM_FILE);
        if (!roomFile.exists() || roomFile.length() == 0) {
            appendLine(ROOM_FILE, "R001,Main Study Room,8,Projector Whiteboard WiFi,true");
            appendLine(ROOM_FILE, "R002,Group Study Room,6,Whiteboard WiFi,true");
            appendLine(ROOM_FILE, "R003,Quiet Room,2,Desk Lamp,true");
            appendLine(ROOM_FILE, "R004,Premium Room,12,Projector Whiteboard WiFi AC,true");
            System.out.println("✓ 4 study rooms created");
        }
        
        // 7. Default Inter-library Loans
        File interlibraryFile = new File(INTERLIBRARY_FILE);
        if (!interlibraryFile.exists() || interlibraryFile.length() == 0) {
            appendLine(INTERLIBRARY_FILE, "201,P001,9999999999,Clean Code,Smart Library SDN BHD,National Library of Malaysia,2026-04-20,2026-04-27,PENDING,");
            System.out.println("✓ 1 inter-library loan record created");
        }
    }

    public static List<String[]> readAllLines(String filePath) {
        List<String[]> records = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    records.add(line.split(","));
                }
            }
        } catch (FileNotFoundException e) {
            // File doesn't exist - return empty list
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
        return records;
    }

    public static void writeAllLines(String filePath, List<String> lines) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            for (String line : lines) {
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error writing file: " + e.getMessage());
        }
    }

    public static void appendLine(String filePath, String line) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true))) {
            bw.write(line);
            bw.newLine();
        } catch (IOException e) {
            System.out.println("Error appending to file: " + e.getMessage());
        }
    }

    // Convenience methods - READ
    public static List<String[]> readUsers() { return readAllLines(USER_FILE); }
    public static List<String[]> readLibrarians() { return readAllLines(LIBRARIAN_FILE); }
    public static List<String[]> readItems() { return readAllLines(ITEM_FILE); }
    public static List<String[]> readBorrows() { return readAllLines(BORROW_FILE); }
    public static List<String[]> readReservations() { return readAllLines(RESERVATION_FILE); }
    public static List<String[]> readRooms() { return readAllLines(ROOM_FILE); }
    public static List<String[]> readBookings() { return readAllLines(BOOKING_FILE); }
    public static List<String[]> readInterLibraryLoans() { return readAllLines(INTERLIBRARY_FILE); }

    // Convenience methods - WRITE
    public static void writeUsers(List<String> lines) { writeAllLines(USER_FILE, lines); }
    public static void writeLibrarians(List<String> lines) { writeAllLines(LIBRARIAN_FILE, lines); }
    public static void writeItems(List<String> lines) { writeAllLines(ITEM_FILE, lines); }
    public static void writeBorrows(List<String> lines) { writeAllLines(BORROW_FILE, lines); }
    public static void writeReservations(List<String> lines) { writeAllLines(RESERVATION_FILE, lines); }
    public static void writeRooms(List<String> lines) { writeAllLines(ROOM_FILE, lines); }
    public static void writeBookings(List<String> lines) { writeAllLines(BOOKING_FILE, lines); }
    public static void writeInterLibraryLoans(List<String> lines) { writeAllLines(INTERLIBRARY_FILE, lines); }
}