import model.*;
import data.FileHandler;
import utils.InputValidator;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class Main {
    // ==================== DATA COLLECTIONS ====================
    private static List<User> users = new ArrayList<>();
    private static List<Librarian> librarians = new ArrayList<>();
    private static List<LibraryItem> items = new ArrayList<>();
    private static List<BorrowRecord> borrowRecords = new ArrayList<>();
    private static List<Reservation> reservations = new ArrayList<>();
    private static List<StudyRoom> studyRooms = new ArrayList<>();
    private static List<RoomBooking> roomBookings = new ArrayList<>();
    private static List<InterLibraryLoan> interLibraryLoans = new ArrayList<>();
    private static Library library = new Library("Smart Library SDN BHD", "Kuala Lumpur, Malaysia");
    
    // ==================== CURRENT SESSION ====================
    private static User currentUser = null;
    private static Librarian currentLibrarian = null;
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        loadAllData();
        mainMenu();
        saveAllData();
        System.out.println("\n✅ Thank you for using Smart Library System!");
    }

    private static void printHeader(String title, String role) {
        System.out.println("\n+======================================================================+");
        System.out.println("|                                                                      |");
        System.out.println("|                    SMART LIBRARY SYSTEM SDN BHD                      |");
        System.out.println("|                     \"Your Digital Library Partner\"                   |");
        System.out.println("|                                                                      |");
        System.out.println("+======================================================================+");
        System.out.println("|  " + String.format("%-62s", title) + "   |");
        System.out.println("|  Role: " + String.format("%-56s", role) + "   |");
        System.out.println("+======================================================================+");
    }

    // ==================== DATA LOADING ====================
    private static void loadAllData() {
        loadUsers();
        loadLibrarians();
        loadItems();
        loadBorrowRecords();
        loadReservations();
        loadStudyRooms();
        loadRoomBookings();
        loadInterLibraryLoans();
        System.out.println("📚 System loaded: " + users.size() + " users, " + librarians.size() 
                         + " librarians, " + items.size() + " items, " + studyRooms.size() + " rooms");
    }

    private static void saveAllData() {
        saveUsers();
        saveLibrarians();
        saveItems();
        saveBorrowRecords();
        saveReservations();
        saveStudyRooms();
        saveRoomBookings();
        saveInterLibraryLoans();
    }

    private static void loadUsers() {
        try {
            for (String[] data : FileHandler.readUsers()) {
                if (data.length < 6) continue;
                String type = data[0];
                switch (type) {
                    case "Student":
                        users.add(new Student(data[1], data[2], data[3], data[4], data[5]));
                        break;
                    case "Faculty":
                        users.add(new Faculty(data[1], data[2], data[3], data[4], data[5]));
                        break;
                    case "PublicMember":
                        users.add(new PublicMember(data[1], data[2], data[3], data[4], data[5]));
                        break;
                }
            }
        } catch (Exception e) {
            System.out.println("⚠️ Error loading users: " + e.getMessage());
        }
    }

    private static void saveUsers() {
        try {
            List<String> lines = new ArrayList<>();
            for (User u : users) {
                String type = u instanceof Student ? "Student" : (u instanceof Faculty ? "Faculty" : "PublicMember");
                lines.add(type + "," + u.getUserId() + "," + u.getName() + "," + u.getPhone() + "," + u.getEmail() + "," + u.getPassword());
            }
            FileHandler.writeUsers(lines);
        } catch (Exception e) {
            System.out.println("⚠️ Error saving users: " + e.getMessage());
        }
    }

    private static void loadLibrarians() {
        try {
            for (String[] data : FileHandler.readLibrarians()) {
                if (data.length >= 3) {
                    librarians.add(new Librarian(data[0], data[1], data[2]));
                }
            }
        } catch (Exception e) {
            System.out.println("⚠️ Error loading librarians: " + e.getMessage());
        }
        if (librarians.isEmpty()) {
            librarians.add(new Librarian("L001", "Admin Librarian", "admin123"));
        }
    }

    private static void saveLibrarians() {
        try {
            List<String> lines = new ArrayList<>();
            for (Librarian l : librarians) {
                lines.add(l.toString());
            }
            FileHandler.writeLibrarians(lines);
        } catch (Exception e) {
            System.out.println("⚠️ Error saving librarians: " + e.getMessage());
        }
    }

    private static void loadItems() {
        try {
            for (String[] data : FileHandler.readItems()) {
                if (data.length < 9) continue;
                String type = data[0];
                String isbn = data[1], title = data[2], author = data[3], publisher = data[4];
                int year = Integer.parseInt(data[5]), totalCopies = Integer.parseInt(data[6]);
                boolean isDamaged = Boolean.parseBoolean(data[7]);
                String genre = data[8];
                
                LibraryItem item = null;
                switch (type) {
                    case "Book": 
                        item = new Book(isbn, title, author, publisher, year, totalCopies, genre); 
                        break;
                    case "Journal": 
                        item = new Journal(isbn, title, author, publisher, year, totalCopies, genre); 
                        break;
                    case "DigitalResource": 
                        item = new DigitalResource(isbn, title, author, publisher, year, totalCopies, genre); 
                        break;
                    case "EBook":
                        if (data.length >= 11) {
                            int totalPages = Integer.parseInt(data[9]);
                            String format = data[10];
                            item = new EBook(isbn, title, author, publisher, year, totalCopies, genre, totalPages, format);
                        } else {
                            item = new EBook(isbn, title, author, publisher, year, totalCopies, genre, 100, "PDF");
                        }
                        break;
                }
                if (item != null) {
                    if (isDamaged) item.setDamaged(true);
                    items.add(item);
                    library.addItem(item);
                }
            }
        } catch (Exception e) {
            System.out.println("⚠️ Error loading items: " + e.getMessage());
        }
    }

    private static void saveItems() {
        try {
            List<String> lines = new ArrayList<>();
            for (LibraryItem i : items) {
                lines.add(i.toString());
            }
            FileHandler.writeItems(lines);
        } catch (Exception e) {
            System.out.println("⚠️ Error saving items: " + e.getMessage());
        }
    }

    private static void loadBorrowRecords() {
        try {
            for (String[] data : FileHandler.readBorrows()) {
                if (data.length >= 10) {
                    int id = Integer.parseInt(data[0]);
                    double fine = Double.parseDouble(data[6]);
                    boolean isLost = Boolean.parseBoolean(data[8]);
                    String lostDate = data.length > 9 ? data[9] : "null";
                    borrowRecords.add(new BorrowRecord(id, data[1], data[2], data[3], data[4], data[5], fine, data[7], isLost, lostDate));
                }
            }
        } catch (Exception e) {
            System.out.println("⚠️ Error loading borrow records: " + e.getMessage());
        }
    }

    private static void saveBorrowRecords() {
        try {
            List<String> lines = new ArrayList<>();
            for (BorrowRecord br : borrowRecords) {
                lines.add(br.toString());
            }
            FileHandler.writeBorrows(lines);
        } catch (Exception e) {
            System.out.println("⚠️ Error saving borrow records: " + e.getMessage());
        }
    }

    private static void loadReservations() {
        try {
            for (String[] data : FileHandler.readReservations()) {
                if (data.length >= 5) {
                    int id = Integer.parseInt(data[0]);
                    reservations.add(new Reservation(id, data[1], data[2], data[3], data[4]));
                }
            }
        } catch (Exception e) {
            System.out.println("⚠️ Error loading reservations: " + e.getMessage());
        }
    }

    private static void saveReservations() {
        try {
            List<String> lines = new ArrayList<>();
            for (Reservation r : reservations) {
                lines.add(r.toString());
            }
            FileHandler.writeReservations(lines);
        } catch (Exception e) {
            System.out.println("⚠️ Error saving reservations: " + e.getMessage());
        }
    }

    private static void loadStudyRooms() {
        try {
            for (String[] data : FileHandler.readRooms()) {
                if (data.length >= 5) {
                    StudyRoom room = new StudyRoom(data[0], data[1], Integer.parseInt(data[2]), data[3]);
                    room.setAvailable(Boolean.parseBoolean(data[4]));
                    studyRooms.add(room);
                }
            }
        } catch (Exception e) {
            System.out.println("⚠️ Error loading study rooms: " + e.getMessage());
        }
        if (studyRooms.isEmpty()) {
            studyRooms.add(new StudyRoom("R001", "Main Study Room", 8, "Projector, Whiteboard, WiFi"));
            studyRooms.add(new StudyRoom("R002", "Group Study Room", 6, "Whiteboard, WiFi"));
            studyRooms.add(new StudyRoom("R003", "Quiet Room", 2, "Desk Lamp"));
            studyRooms.add(new StudyRoom("R004", "Premium Room", 12, "Projector, Whiteboard, WiFi, AC"));
        }
    }

    private static void saveStudyRooms() {
        try {
            List<String> lines = new ArrayList<>();
            for (StudyRoom r : studyRooms) {
                lines.add(r.toString());
            }
            FileHandler.writeRooms(lines);
        } catch (Exception e) {
            System.out.println("⚠️ Error saving study rooms: " + e.getMessage());
        }
    }

    private static void loadRoomBookings() {
        try {
            for (String[] data : FileHandler.readBookings()) {
                if (data.length >= 7) {
                    int id = Integer.parseInt(data[0]);
                    roomBookings.add(new RoomBooking(id, data[1], data[2], data[3], data[4], data[5], data[6]));
                }
            }
        } catch (Exception e) {
            System.out.println("⚠️ Error loading room bookings: " + e.getMessage());
        }
    }

    private static void saveRoomBookings() {
        try {
            List<String> lines = new ArrayList<>();
            for (RoomBooking rb : roomBookings) {
                lines.add(rb.toString());
            }
            FileHandler.writeBookings(lines);
        } catch (Exception e) {
            System.out.println("⚠️ Error saving room bookings: " + e.getMessage());
        }
    }

    private static void loadInterLibraryLoans() {
        try {
            for (String[] data : FileHandler.readInterLibraryLoans()) {
                if (data.length >= 10) {
                    int id = Integer.parseInt(data[0]);
                    interLibraryLoans.add(new InterLibraryLoan(id, data[1], data[2], data[3], data[4], data[5], data[6], data[7], data[8], data[9]));
                }
            }
        } catch (Exception e) {
            System.out.println("⚠️ Error loading inter-library loans: " + e.getMessage());
        }
    }

    private static void saveInterLibraryLoans() {
        try {
            List<String> lines = new ArrayList<>();
            for (InterLibraryLoan loan : interLibraryLoans) {
                lines.add(loan.toString());
            }
            FileHandler.writeInterLibraryLoans(lines);
        } catch (Exception e) {
            System.out.println("⚠️ Error saving inter-library loans: " + e.getMessage());
        }
    }

    // ==================== MAIN MENU ====================
    private static void mainMenu() {
        while (true) {
            printMenuHeader();
            int choice = InputValidator.getMenuChoice(0, 3);
            
            switch (choice) {
                case 1: userLogin(); break;
                case 2: userRegister(); break;
                case 3: librarianLogin(); break;
                case 0: 
                    System.out.println("\n👋 Goodbye!");
                    return;
            }
        }
    }

    private static void printMenuHeader() {
        printHeader("MAIN MENU", "None");
        System.out.println("|                                                                      |");
        System.out.println("|    【1】 User Login                                                  |");
        System.out.println("|    【2】 User Register                                               |");
        System.out.println("|    【3】 Librarian Login                                             |");
        System.out.println("|                                                                      |");
        System.out.println("|    【0】 Exit                                                        |");
        System.out.println("|                                                                      |");
        System.out.println("+=======================================================================+");
        System.out.print("Choice: ");
    }

    // ==================== USER AUTHENTICATION ====================
    private static void userLogin() {
        printHeader("USER LOGIN", "None");
        System.out.print("Enter User ID: ");
        String userId = scanner.nextLine();
        System.out.print("Enter Password: ");
        String password = scanner.nextLine();
        
        for (User u : users) {
            if (u.getUserId().equals(userId) && u.checkPassword(password)) {
                currentUser = u;
                System.out.println("\n✅ Welcome, " + u.getName() + "!");
                userMenu();
                return;
            }
        }
        System.out.println("❌ Invalid User ID or Password.");
    }

    private static void userRegister() {
        printHeader("USER REGISTRATION", "None");
        System.out.println("User Type:");
        System.out.println("1. Student (5 books, 14 days, RM0.50/day late)");
        System.out.println("2. Faculty (12 books, 30 days, RM0.20/day late)");
        System.out.println("3. Public Member (3 books, 7 days, RM1.00/day late)");
        System.out.print("Choice: ");
        int type = InputValidator.getMenuChoice(1, 3);
        
        String userId = InputValidator.getNonEmptyInput("User ID: ");
        String name = InputValidator.getNonEmptyInput("Name: ");
        String phone = InputValidator.getNonEmptyInput("Phone: ");
        String email = InputValidator.getNonEmptyInput("Email: ");
        String password = InputValidator.getNonEmptyInput("Password: ");
        
        User newUser = null;
        switch (type) {
            case 1: newUser = new Student(userId, name, phone, email, password); break;
            case 2: newUser = new Faculty(userId, name, phone, email, password); break;
            case 3: newUser = new PublicMember(userId, name, phone, email, password); break;
        }
        
        users.add(newUser);
        saveUsers();
        System.out.println("✅ Registration successful! Please login.");
    }

    private static void librarianLogin() {
        printHeader("LIBRARIAN LOGIN", "None");
        System.out.print("Enter Staff ID: ");
        String staffId = scanner.nextLine();
        System.out.print("Enter Password: ");
        String password = scanner.nextLine();
        
        for (Librarian l : librarians) {
            if (l.getStaffId().equals(staffId) && l.checkPassword(password)) {
                currentLibrarian = l;
                System.out.println("\n✅ Welcome, Librarian " + l.getName() + "!");
                librarianMenu();
                return;
            }
        }
        System.out.println("❌ Invalid Staff ID or Password.");
    }

    // ==================== USER MENU ====================
    private static void userMenu() {
        while (currentUser != null) {
            printUserMenuHeader();
            int choice = InputValidator.getMenuChoice(0, 19);
            
            switch (choice) {
                case 1: browseItems(); break;
                case 2: searchItems(); break;
                case 3: borrowItem(); break;
                case 4: returnItem(); break;
                case 5: viewMyBorrowings(); break;
                case 6: reserveItem(); break;
                case 7: viewMyReservations(); break;
                case 8: payFine(); break;
                case 9: viewMyProfile(); break;
                case 10: updateProfile(); break;
                case 11: bookStudyRoom(); break;
                case 12: viewMyRoomBookings(); break;
                case 13: cancelRoomBooking(); break;
                case 14: cancelReservation(); break;
                case 15: viewMyEBooks(); break;
                case 16: downloadEBook(); break;
                case 17: readEBook(); break;
                case 18: requestInterLibraryLoan(); break;
                case 19: viewMyInterLibraryLoans(); break;
                case 0: 
                    currentUser = null;
                    System.out.println("Logged out.");
                    break;
            }
        }
    }

    private static void printUserMenuHeader() {
        String role = "User: " + currentUser.getName() + " (" + 
            (currentUser instanceof Student ? "Student" : 
             currentUser instanceof Faculty ? "Faculty" : "Public Member") + ")";
        printHeader("USER MENU", role);
        System.out.println("|                                                                      |");
        System.out.println("|    【1】 Browse All Items                                              |");
        System.out.println("|    【2】 Search Items                                                  |");
        System.out.println("|    【3】 Borrow Item                                                   |");
        System.out.println("|    【4】 Return Item                                                   |");
        System.out.println("|    【5】 View My Borrowings                                            |");
        System.out.println("|    【6】 Reserve Item                                                  |");
        System.out.println("|    【7】 View My Reservations                                          |");
        System.out.println("|    【8】 Pay Fine                                                      |");
        System.out.println("|    【9】 View My Profile                                               |");
        System.out.println("|    【10】 Update My Profile                                            |");
        System.out.println("|    【11】 Book Study Room                                              |");
        System.out.println("|    【12】 View My Room Bookings                                        |");
        System.out.println("|    【13】 Cancel Room Booking                                          |");
        System.out.println("|    【14】 Cancel Reservation                                           |");
        System.out.println("|                                                                        |");
        System.out.println("|  E-BOOK READER                                                         |");
        System.out.println("|    【15】 View My E-Books                                              |");
        System.out.println("|    【16】 Download E-Book                                              |");
        System.out.println("|    【17】 Read E-Book                                                  |");
        System.out.println("|                                                                       |");
        System.out.println("|  INTER-LIBRARY LOAN                                                    |");
        System.out.println("|    【18】 Request Inter-Library Loan                                   |");
        System.out.println("|    【19】 View My Inter-Library Loans                                  |");
        System.out.println("|                                                                        |");
        System.out.println("|    【0】 Logout                                                        |");
        System.out.println("|                                                                       |");
        System.out.println("+========================================================================+");
        System.out.print("Choice: ");
    }

    // ==================== BROWSE & SEARCH ====================
    private static void browseItems() {
        printHeader("BROWSE ALL ITEMS", "User: " + currentUser.getName());
        try {
            System.out.printf("%-15s %-35s %-20s %-15s %-10s%n", "ISBN", "Title", "Author", "Genre", "Available");
            System.out.println("+========================================================================================+");
            
            if (items.isEmpty()) {
                System.out.println("❌ No items available in the library.");
            } else {
                for (LibraryItem item : items) {
                    System.out.printf("%-15s %-35s %-20s %-15s %-10d%n", 
                        item.getIsbn(), 
                        truncate(item.getTitle(), 33), 
                        truncate(item.getAuthor(), 18),
                        truncate(item.getGenre(), 13),
                        item.getAvailableCopies());
                }
            }
        } catch (Exception e) {
            System.out.println("⚠️ Error displaying items: " + e.getMessage());
        }
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private static void searchItems() {
        printHeader("SEARCH ITEMS", "User: " + currentUser.getName());
        try {
            System.out.println("1. Search by ISBN");
            System.out.println("2. Search by Title");
            System.out.println("3. Search by Author");
            System.out.println("4. Search by Genre");
            System.out.print("Choice: ");
            int choice = InputValidator.getMenuChoice(1, 4);
            
            System.out.print("Enter keyword: ");
            String keyword = scanner.nextLine().toLowerCase();
            
            System.out.printf("%-15s %-35s %-20s %-15s %-10s%n", "ISBN", "Title", "Author", "Genre", "Available");
            System.out.println("+========================================================================================+");
            
            boolean found = false;
            for (LibraryItem item : items) {
                boolean match = false;
                switch (choice) {
                    case 1: match = item.getIsbn().toLowerCase().contains(keyword); break;
                    case 2: match = item.getTitle().toLowerCase().contains(keyword); break;
                    case 3: match = item.getAuthor().toLowerCase().contains(keyword); break;
                    case 4: match = item.getGenre().toLowerCase().contains(keyword); break;
                }
                if (match) {
                    found = true;
                    System.out.printf("%-15s %-35s %-20s %-15s %-10d%n", 
                        item.getIsbn(), 
                        truncate(item.getTitle(), 33), 
                        truncate(item.getAuthor(), 18),
                        truncate(item.getGenre(), 13),
                        item.getAvailableCopies());
                }
            }
            
            if (!found) {
                System.out.println("❌ No items found matching your search criteria.");
            }
        } catch (Exception e) {
            System.out.println("⚠️ Error during search: " + e.getMessage());
        }
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    // ==================== BORROW & RETURN ====================
    private static void borrowItem() {
        printHeader("BORROW ITEM", "User: " + currentUser.getName());
        try {
            System.out.print("Enter Item ISBN to borrow: ");
            String isbn = scanner.nextLine();
            
            LibraryItem target = findItemByIsbn(isbn);
            if (target == null) {
                System.out.println("❌ Item not found.");
                return;
            }
            
            if (!target.isAvailable()) {
                System.out.println("❌ No copies available. You can reserve it.");
                return;
            }
            
            int activeBorrows = 0;
            for (BorrowRecord br : borrowRecords) {
                if (br.getUserId().equals(currentUser.getUserId()) && br.isActive() && !br.isLost()) {
                    activeBorrows++;
                }
            }
            
            if (activeBorrows >= currentUser.getBorrowLimit()) {
                System.out.println("❌ You have reached your borrow limit: " + currentUser.getBorrowLimit());
                return;
            }
            
            target.borrowCopy();
            BorrowRecord record = new BorrowRecord(currentUser.getUserId(), isbn, currentUser.getLoanDays());
            borrowRecords.add(record);
            saveItems();
            saveBorrowRecords();
            
            System.out.println("✅ Borrowed successfully!");
            System.out.println("   Due date: " + record.getDueDate());
            System.out.println("   Fine per day late: RM " + currentUser.getFinePerDay());
        } catch (Exception e) {
            System.out.println("⚠️ Error during borrowing: " + e.getMessage());
        }
    }

    private static void returnItem() {
        printHeader("RETURN ITEM", "User: " + currentUser.getName());
        try {
            System.out.print("Enter Item ISBN to return: ");
            String isbn = scanner.nextLine();
            
            BorrowRecord activeRecord = null;
            for (BorrowRecord br : borrowRecords) {
                if (br.getIsbn().equals(isbn) && br.getUserId().equals(currentUser.getUserId()) && br.isActive() && !br.isLost()) {
                    activeRecord = br;
                    break;
                }
            }
            
            if (activeRecord == null) {
                System.out.println("❌ No active borrowing found for this ISBN.");
                return;
            }
            
            LibraryItem item = findItemByIsbn(isbn);
            if (item != null) {
                item.returnCopy();
            }
            
            double fine = activeRecord.returnItem(currentUser.getFinePerDay());
            saveItems();
            saveBorrowRecords();
            
            if (fine > 0) {
                System.out.println("⚠️ Item returned LATE. Fine: RM " + String.format("%.2f", fine));
            } else {
                System.out.println("✅ Item returned on time. No fine.");
            }
        } catch (Exception e) {
            System.out.println("⚠️ Error during return: " + e.getMessage());
        }
    }

    private static void viewMyBorrowings() {
        printHeader("MY BORROWING HISTORY", "User: " + currentUser.getName());
        try {
            System.out.printf("%-10s %-15s %-12s %-12s %-10s %-10s%n", 
                "Record ID", "ISBN", "Due Date", "Status", "Fine", "Lost");
            System.out.println("+========================================================================================+");
            
            boolean hasRecords = false;
            for (BorrowRecord br : borrowRecords) {
                if (br.getUserId().equals(currentUser.getUserId())) {
                    hasRecords = true;
                    System.out.printf("%-10d %-15s %-12s %-12s RM %-8.2f %-10s%n", 
                        br.getRecordId(), br.getIsbn(), br.getDueDate(), br.getStatus(), br.getFine(),
                        br.isLost() ? "YES" : "NO");
                }
            }
            
            if (!hasRecords) {
                System.out.println("No borrowing records found.");
            }
        } catch (Exception e) {
            System.out.println("⚠️ Error displaying borrowings: " + e.getMessage());
        }
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private static void payFine() {
        printHeader("PAY FINE", "User: " + currentUser.getName());
        try {
            double totalFine = 0;
            List<BorrowRecord> fineRecords = new ArrayList<>();
            
            for (BorrowRecord br : borrowRecords) {
                if (br.getUserId().equals(currentUser.getUserId()) && br.getFine() > 0 && !br.isLost()) {
                    totalFine += br.getFine();
                    fineRecords.add(br);
                }
            }
            
            if (totalFine == 0) {
                System.out.println("✅ You have no fines to pay.");
                return;
            }
            
            System.out.println("\n=== FINES SUMMARY ===");
            for (BorrowRecord br : fineRecords) {
                System.out.printf("Record %d: RM %.2f%n", br.getRecordId(), br.getFine());
            }
            System.out.println("Total fine: RM " + String.format("%.2f", totalFine));
            
            if (InputValidator.getConfirmation("Pay all fines now?")) {
                for (BorrowRecord br : fineRecords) {
                    br.payFine();
                }
                saveBorrowRecords();
                System.out.println("✅ Fines paid. Thank you!");
            }
        } catch (Exception e) {
            System.out.println("⚠️ Error processing fine payment: " + e.getMessage());
        }
    }

    // ==================== PROFILE ====================
    private static void viewMyProfile() {
        printHeader("VIEW PROFILE", "User: " + currentUser.getName());
        System.out.println("User ID     : " + currentUser.getUserId());
        System.out.println("Name        : " + currentUser.getName());
        System.out.println("Phone       : " + currentUser.getPhone());
        System.out.println("Email       : " + currentUser.getEmail());
        System.out.println("Borrow Limit: " + currentUser.getBorrowLimit());
        System.out.println("Loan Days   : " + currentUser.getLoanDays());
        System.out.println("Fine per day: RM " + currentUser.getFinePerDay());
        
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private static void updateProfile() {
        printHeader("UPDATE PROFILE", "User: " + currentUser.getName());
        System.out.println("Current Information:");
        System.out.println("Name : " + currentUser.getName());
        System.out.println("Phone: " + currentUser.getPhone());
        System.out.println("Email: " + currentUser.getEmail());
        System.out.println();
        
        System.out.print("New Name (Enter to keep): ");
        String name = scanner.nextLine();
        System.out.print("New Phone (Enter to keep): ");
        String phone = scanner.nextLine();
        System.out.print("New Email (Enter to keep): ");
        String email = scanner.nextLine();
        
        if (!name.trim().isEmpty()) currentUser.setName(name);
        if (!phone.trim().isEmpty()) currentUser.setPhone(phone);
        if (!email.trim().isEmpty()) currentUser.setEmail(email);
        
        saveUsers();
        System.out.println("✅ Profile updated successfully!");
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    // ==================== RESERVATION ====================
    private static void reserveItem() {
        printHeader("RESERVE ITEM", "User: " + currentUser.getName());
        try {
            System.out.print("Enter Item ISBN to reserve: ");
            String isbn = scanner.nextLine();
            
            LibraryItem item = findItemByIsbn(isbn);
            if (item == null) {
                System.out.println("❌ Item not found.");
                return;
            }
            
            if (item.isAvailable()) {
                System.out.println("Item is currently available. You can borrow it directly.");
                return;
            }
            
            for (Reservation r : reservations) {
                if (r.getIsbn().equals(isbn) && r.getUserId().equals(currentUser.getUserId()) && r.isPending()) {
                    System.out.println("❌ You already have a pending reservation for this item.");
                    return;
                }
            }
            
            Reservation reservation = new Reservation(currentUser.getUserId(), isbn);
            reservations.add(reservation);
            saveReservations();
            System.out.println("✅ Reservation placed successfully. Reservation ID: " + reservation.getReservationId());
        } catch (Exception e) {
            System.out.println("⚠️ Error during reservation: " + e.getMessage());
        }
    }

    private static void viewMyReservations() {
        printHeader("MY RESERVATIONS", "User: " + currentUser.getName());
        try {
            System.out.printf("%-12s %-15s %-12s %-10s%n", "Reserve ID", "ISBN", "Date", "Status");
            System.out.println("+======================================================================+");
            
            boolean hasReservations = false;
            for (Reservation r : reservations) {
                if (r.getUserId().equals(currentUser.getUserId())) {
                    hasReservations = true;
                    System.out.printf("%-12d %-15s %-12s %-10s%n", 
                        r.getReservationId(), r.getIsbn(), r.getReservationDate(), r.getStatus());
                }
            }
            
            if (!hasReservations) {
                System.out.println("No reservations found.");
            }
        } catch (Exception e) {
            System.out.println("⚠️ Error displaying reservations: " + e.getMessage());
        }
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private static void cancelReservation() {
        printHeader("CANCEL RESERVATION", "User: " + currentUser.getName());
        try {
            System.out.print("Enter Reservation ID to cancel: ");
            int reservationId = InputValidator.getPositiveInt("");
            
            for (Reservation r : reservations) {
                if (r.getReservationId() == reservationId && r.getUserId().equals(currentUser.getUserId()) && r.isPending()) {
                    r.cancel();
                    saveReservations();
                    System.out.println("✅ Reservation cancelled successfully.");
                    System.out.println("\nPress Enter to continue...");
                    scanner.nextLine();
                    return;
                }
            }
            System.out.println("❌ Reservation not found or already fulfilled/cancelled.");
        } catch (Exception e) {
            System.out.println("⚠️ Error cancelling reservation: " + e.getMessage());
        }
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    // ==================== STUDY ROOM BOOKING ====================
    private static void bookStudyRoom() {
        printHeader("BOOK STUDY ROOM", "User: " + currentUser.getName());
        try {
            System.out.println("Available Study Rooms:");
            System.out.printf("%-10s %-25s %-10s %-35s%n", "Room ID", "Room Name", "Capacity", "Equipment");
            System.out.println("+--------------------------------------------------------------------------------+");
            
            boolean hasAvailable = false;
            for (StudyRoom room : studyRooms) {
                if (room.isAvailable()) {
                    hasAvailable = true;
                    System.out.printf("%-10s %-25s %-10d %-35s%n", 
                        room.getRoomId(), room.getRoomName(), room.getCapacity(), room.getEquipment());
                }
            }
            
            if (!hasAvailable) {
                System.out.println("❌ No study rooms available at the moment.");
                System.out.println("\nPress Enter to continue...");
                scanner.nextLine();
                return;
            }
            
            System.out.print("\nEnter Room ID to book: ");
            String roomId = scanner.nextLine();
            
            StudyRoom selectedRoom = null;
            for (StudyRoom room : studyRooms) {
                if (room.getRoomId().equalsIgnoreCase(roomId) && room.isAvailable()) {
                    selectedRoom = room;
                    break;
                }
            }
            
            if (selectedRoom == null) {
                System.out.println("❌ Invalid room ID or room not available.");
                System.out.println("\nPress Enter to continue...");
                scanner.nextLine();
                return;
            }
            
            System.out.print("Enter booking date (YYYY-MM-DD): ");
            LocalDate bookingDate;
            try {
                bookingDate = LocalDate.parse(scanner.nextLine());
                if (bookingDate.isBefore(LocalDate.now())) {
                    System.out.println("❌ Cannot book a past date.");
                    System.out.println("\nPress Enter to continue...");
                    scanner.nextLine();
                    return;
                }
            } catch (Exception e) {
                System.out.println("❌ Invalid date format. Use YYYY-MM-DD");
                System.out.println("\nPress Enter to continue...");
                scanner.nextLine();
                return;
            }
            
            System.out.println("\nAvailable Time Slots:");
            System.out.println("1. 09:00 - 11:00");
            System.out.println("2. 11:00 - 13:00");
            System.out.println("3. 13:00 - 15:00");
            System.out.println("4. 15:00 - 17:00");
            System.out.print("Choose time slot (1-4): ");
            int slot = InputValidator.getMenuChoice(1, 4);
            
            LocalTime startTime, endTime;
            switch (slot) {
                case 1: startTime = LocalTime.of(9, 0); endTime = LocalTime.of(11, 0); break;
                case 2: startTime = LocalTime.of(11, 0); endTime = LocalTime.of(13, 0); break;
                case 3: startTime = LocalTime.of(13, 0); endTime = LocalTime.of(15, 0); break;
                default: startTime = LocalTime.of(15, 0); endTime = LocalTime.of(17, 0); break;
            }
            
            for (RoomBooking rb : roomBookings) {
                if (rb.getRoomId().equals(roomId) && rb.getBookingDate().equals(bookingDate) && 
                    rb.getStartTime().equals(startTime) && rb.isActive()) {
                    System.out.println("❌ This room is already booked for this time slot.");
                    System.out.println("\nPress Enter to continue...");
                    scanner.nextLine();
                    return;
                }
            }
            
            selectedRoom.setAvailable(false);
            RoomBooking booking = new RoomBooking(currentUser.getUserId(), roomId, bookingDate, startTime, endTime);
            roomBookings.add(booking);
            saveStudyRooms();
            saveRoomBookings();
            
            System.out.println("\n✅ Room booked successfully!");
            System.out.println("   Booking ID: " + booking.getBookingId());
            System.out.println("   Room: " + selectedRoom.getRoomName());
            System.out.println("   Date: " + bookingDate);
            System.out.println("   Time: " + startTime + " - " + endTime);
        } catch (Exception e) {
            System.out.println("⚠️ Error booking room: " + e.getMessage());
        }
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private static void viewMyRoomBookings() {
        printHeader("MY ROOM BOOKINGS", "User: " + currentUser.getName());
        try {
            System.out.printf("%-12s %-10s %-15s %-12s %-12s %-10s%n", 
                "Booking ID", "Room ID", "Date", "Start", "End", "Status");
            System.out.println("+--------------------------------------------------------------------------------+");
            
            boolean hasBookings = false;
            for (RoomBooking rb : roomBookings) {
                if (rb.getUserId().equals(currentUser.getUserId())) {
                    hasBookings = true;
                    System.out.printf("%-12d %-10s %-15s %-12s %-12s %-10s%n", 
                        rb.getBookingId(), rb.getRoomId(), rb.getBookingDate(), 
                        rb.getStartTime(), rb.getEndTime(), rb.getStatus());
                }
            }
            
            if (!hasBookings) {
                System.out.println("No room bookings found.");
            }
        } catch (Exception e) {
            System.out.println("⚠️ Error displaying bookings: " + e.getMessage());
        }
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private static void cancelRoomBooking() {
        printHeader("CANCEL ROOM BOOKING", "User: " + currentUser.getName());
        try {
            System.out.print("Enter Booking ID to cancel: ");
            int bookingId = InputValidator.getPositiveInt("");
            
            for (RoomBooking rb : roomBookings) {
                if (rb.getBookingId() == bookingId && rb.getUserId().equals(currentUser.getUserId()) && rb.isActive()) {
                    rb.cancel();
                    for (StudyRoom room : studyRooms) {
                        if (room.getRoomId().equals(rb.getRoomId())) {
                            room.setAvailable(true);
                            break;
                        }
                    }
                    saveRoomBookings();
                    saveStudyRooms();
                    System.out.println("✅ Room booking cancelled successfully.");
                    System.out.println("\nPress Enter to continue...");
                    scanner.nextLine();
                    return;
                }
            }
            System.out.println("❌ Booking not found or already cancelled/completed.");
        } catch (Exception e) {
            System.out.println("⚠️ Error cancelling booking: " + e.getMessage());
        }
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    // ==================== E-BOOK READER ====================
    private static void viewMyEBooks() {
        printHeader("MY E-BOOKS", "User: " + currentUser.getName());
        try {
            System.out.printf("%-15s %-35s %-15s %-10s %-15s%n", "ISBN", "Title", "Author", "Format", "Downloaded");
            System.out.println("+========================================================================================+");
            
            boolean hasEBooks = false;
            for (LibraryItem item : items) {
                if (item instanceof EBook) {
                    EBook eBook = (EBook) item;
                    hasEBooks = true;
                    System.out.printf("%-15s %-35s %-15s %-10s %-15s%n", 
                        eBook.getIsbn(), 
                        truncate(eBook.getTitle(), 33),
                        truncate(eBook.getAuthor(), 13),
                        eBook.getFormat(),
                        eBook.isDownloaded() ? "Yes" : "No");
                }
            }
            
            if (!hasEBooks) {
                System.out.println("No e-books available in the library.");
            }
        } catch (Exception e) {
            System.out.println("⚠️ Error displaying e-books: " + e.getMessage());
        }
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private static void downloadEBook() {
        printHeader("DOWNLOAD E-BOOK", "User: " + currentUser.getName());
        try {
            System.out.print("Enter E-Book ISBN to download: ");
            String isbn = scanner.nextLine();
            
            LibraryItem item = findItemByIsbn(isbn);
            if (item == null || !(item instanceof EBook)) {
                System.out.println("❌ E-Book not found.");
                return;
            }
            
            EBook eBook = (EBook) item;
            
            boolean hasBorrowed = false;
            for (BorrowRecord br : borrowRecords) {
                if (br.getIsbn().equals(isbn) && br.getUserId().equals(currentUser.getUserId()) && br.isActive()) {
                    hasBorrowed = true;
                    break;
                }
            }
            
            if (!hasBorrowed) {
                System.out.println("❌ You need to borrow this e-book first.");
                return;
            }
            
            eBook.download(currentUser.getUserId());
            saveItems();
            System.out.println("✅ E-book downloaded successfully!");
        } catch (Exception e) {
            System.out.println("⚠️ Error downloading e-book: " + e.getMessage());
        }
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private static void readEBook() {
        printHeader("READ E-BOOK", "User: " + currentUser.getName());
        try {
            System.out.print("Enter E-Book ISBN to read: ");
            String isbn = scanner.nextLine();
            
            LibraryItem item = findItemByIsbn(isbn);
            if (item == null || !(item instanceof EBook)) {
                System.out.println("❌ E-Book not found.");
                return;
            }
            
            EBook eBook = (EBook) item;
            
            if (!eBook.isDownloaded()) {
                System.out.println("❌ You need to download this e-book first.");
                return;
            }
            
            boolean reading = true;
            while (reading) {
                System.out.println("\n📖 Reading: " + eBook.getTitle());
                System.out.println("   Author: " + eBook.getAuthor());
                System.out.println("   Format: " + eBook.getFormat());
                System.out.println("   Total Pages: " + eBook.getTotalPages());
                eBook.showReadingProgress();
                
                System.out.println("\nOptions:");
                System.out.println("1. Read next page");
                System.out.println("2. Go to specific page");
                System.out.println("3. Add bookmark");
                System.out.println("4. Exit reader");
                System.out.print("Choice: ");
                int choice = InputValidator.getMenuChoice(1, 4);
                
                switch (choice) {
                    case 1:
                        if (eBook.getCurrentPage() < eBook.getTotalPages()) {
                            eBook.read(eBook.getCurrentPage() + 1);
                        } else {
                            System.out.println("📖 You have finished this book!");
                        }
                        break;
                    case 2:
                        System.out.print("Enter page number (1-" + eBook.getTotalPages() + "): ");
                        int page = InputValidator.getPositiveInt("");
                        eBook.read(page);
                        break;
                    case 3:
                        eBook.addBookmark();
                        break;
                    case 4:
                        reading = false;
                        System.out.println("Exiting reader...");
                        break;
                }
                saveItems();
            }
        } catch (Exception e) {
            System.out.println("⚠️ Error reading e-book: " + e.getMessage());
        }
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    // ==================== INTER-LIBRARY LOAN ====================
    private static void requestInterLibraryLoan() {
        printHeader("REQUEST INTER-LIBRARY LOAN", "User: " + currentUser.getName());
        try {
            System.out.println("📚 Request books from other libraries (delivery 5-7 days)");
            System.out.println();
            
            System.out.print("Enter Book ISBN: ");
            String isbn = scanner.nextLine();
            
            LibraryItem localItem = findItemByIsbn(isbn);
            if (localItem != null && localItem.isAvailable()) {
                System.out.println("⚠️ This book is available in our library.");
                if (!InputValidator.getConfirmation("Still request from other library?")) {
                    return;
                }
            }
            
            System.out.print("Enter Book Title: ");
            String title = scanner.nextLine();
            
            System.out.println("\nPartner Libraries:");
            System.out.println("1. University of Malaya Library");
            System.out.println("2. National Library of Malaysia");
            System.out.println("3. UTAR Library");
            System.out.println("4. Sunway University Library");
            System.out.print("Choose (1-4): ");
            int libChoice = InputValidator.getMenuChoice(1, 4);
            
            String owningLibrary = "";
            switch (libChoice) {
                case 1: owningLibrary = "University of Malaya Library"; break;
                case 2: owningLibrary = "National Library of Malaysia"; break;
                case 3: owningLibrary = "UTAR Library"; break;
                case 4: owningLibrary = "Sunway University Library"; break;
            }
            
            InterLibraryLoan loan = new InterLibraryLoan(
                currentUser.getUserId(), isbn, title, "Smart Library SDN BHD", owningLibrary
            );
            interLibraryLoans.add(loan);
            saveInterLibraryLoans();
            
            System.out.println("\n✅ Request submitted! ID: " + loan.getRequestId());
            System.out.println("   Expected arrival: " + loan.getExpectedArrivalDate());
        } catch (Exception e) {
            System.out.println("⚠️ Error: " + e.getMessage());
        }
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private static void viewMyInterLibraryLoans() {
        printHeader("MY INTER-LIBRARY LOANS", "User: " + currentUser.getName());
        try {
            System.out.printf("%-12s %-15s %-30s %-20s %-12s%n", 
                "Request ID", "ISBN", "Title", "From Library", "Status");
            System.out.println("+========================================================================================+");
            
            boolean hasLoans = false;
            for (InterLibraryLoan loan : interLibraryLoans) {
                if (loan.getUserId().equals(currentUser.getUserId())) {
                    hasLoans = true;
                    System.out.printf("%-12d %-15s %-30s %-20s %-12s%n", 
                        loan.getRequestId(), loan.getIsbn(), 
                        truncate(loan.getBookTitle(), 28), 
                        truncate(loan.getOwningLibrary(), 18), 
                        loan.getStatus());
                }
            }
            
            if (!hasLoans) {
                System.out.println("No inter-library loan requests.");
            }
        } catch (Exception e) {
            System.out.println("⚠️ Error: " + e.getMessage());
        }
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    // ==================== LIBRARIAN MENU ====================
    private static void librarianMenu() {
        while (currentLibrarian != null) {
            printLibrarianMenuHeader();
            int choice = InputValidator.getMenuChoice(0, 20);
            
            switch (choice) {
                case 1: addNewItem(); break;
                case 2: modifyItem(); break;
                case 3: removeItem(); break;
                case 4: browseItemsLibrarian(); break;
                case 5: viewAllUsers(); break;
                case 6: deleteUser(); break;
                case 7: mostBorrowedReport(); break;
                case 8: revenueReport(); break;
                case 9: overdueReport(); break;
                case 10: borrowReport(); break;
                case 11: popularBooksReport(); break;
                case 12: markItemAsLost(); break;
                case 13: viewLostBooksReport(); break;
                case 14: markLostBookAsFound(); break;
                case 15: viewAllInterLibraryRequests(); break;
                case 16: approveInterLibraryRequest(); break;
                case 17: rejectInterLibraryRequest(); break;
                case 0:
                    currentLibrarian = null;
                    System.out.println("Logged out.");
                    break;
            }
        }
    }

    private static void printLibrarianMenuHeader() {
        String role = "Librarian: " + currentLibrarian.getName();
        printHeader("LIBRARIAN MENU", role);
        System.out.println("|                                                                      |");
        System.out.println("|  ITEM MANAGEMENT                                                     |");
        System.out.println("|    【1】 Add New Item                                                 |");
        System.out.println("|    【2】 Modify Item                                                  |");
        System.out.println("|    【3】 Remove Item (Mark as Damaged)                                |");
        System.out.println("|    【4】 View All Items                                               |");
        System.out.println("|                                                                      |");
        System.out.println("|  USER MANAGEMENT                                                     |");
        System.out.println("|    【5】 View All Users                                               |");
        System.out.println("|    【6】 Delete User                                                  |");
        System.out.println("|                                                                      |");
        System.out.println("|  REPORTS                                                             |");
        System.out.println("|    【7】 Most Borrowed Books Report                                   |");
        System.out.println("|    【8】 Revenue Report (Fines Collected)                             |");
        System.out.println("|    【9】 Overdue Items Report                                         |");
        System.out.println("|    【10】 Borrow Report (Monthly)                                     |");
        System.out.println("|    【11】 Popular Books Report (Genre-based)                          |");
        System.out.println("|                                                                      |");
        System.out.println("|  LOST BOOKS MANAGEMENT                                               |");
        System.out.println("|    【12】 Mark Item as Lost                                           |");
        System.out.println("|    【13】 View Lost Books Report                                      |");
        System.out.println("|    【14】 Mark Lost Book as Found                                     |");
        System.out.println("|                                                                      |");
        System.out.println("|  INTER-LIBRARY LOAN MANAGEMENT                                       |");
        System.out.println("|    【15】 View All Inter-Library Requests                             |");
        System.out.println("|    【16】 Approve Inter-Library Request                               |");
        System.out.println("|    【17】 Reject Inter-Library Request                                |");
        System.out.println("|                                                                      |");
        System.out.println("|    【0】 Logout                                                       |");
        System.out.println("|                                                                      |");
        System.out.println("+======================================================================+");
        System.out.print("Choice: ");
    }

    // ==================== ITEM MANAGEMENT (LIBRARIAN) ====================
    private static void browseItemsLibrarian() {
        printHeader("BROWSE ALL ITEMS", "Librarian: " + currentLibrarian.getName());
        try {
            System.out.printf("%-15s %-35s %-20s %-15s %-10s %-10s%n", 
                "ISBN", "Title", "Author", "Genre", "Available", "Damaged");
            System.out.println("+================================================================================================+");
            for (LibraryItem item : items) {
                System.out.printf("%-15s %-35s %-20s %-15s %-10d %-10s%n", 
                    item.getIsbn(), 
                    truncate(item.getTitle(), 33), 
                    truncate(item.getAuthor(), 18),
                    truncate(item.getGenre(), 13),
                    item.getAvailableCopies(),
                    item.isDamaged() ? "Yes" : "No");
            }
        } catch (Exception e) {
            System.out.println("⚠️ Error displaying items: " + e.getMessage());
        }
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private static void addNewItem() {
        printHeader("ADD NEW ITEM", "Librarian: " + currentLibrarian.getName());
        try {
            System.out.println("Item Type:");
            System.out.println("1. Book");
            System.out.println("2. Journal");
            System.out.println("3. Digital Resource");
            System.out.println("4. E-Book");
            System.out.print("Choice: ");
            int type = InputValidator.getMenuChoice(1, 4);
            
            String isbn = InputValidator.getValidISBN("ISBN (10 or 13 digits): ");
            
            if (findItemByIsbn(isbn) != null) {
                System.out.println("❌ Item with this ISBN already exists.");
                return;
            }
            
            String title = InputValidator.getNonEmptyInput("Title: ");
            String author = InputValidator.getNonEmptyInput("Author: ");
            String publisher = InputValidator.getNonEmptyInput("Publisher: ");
            int year = InputValidator.getPositiveInt("Year: ");
            int copies = InputValidator.getPositiveInt("Number of copies: ");
            String genre = InputValidator.getNonEmptyInput("Genre: ");
            
            LibraryItem newItem = null;
            switch (type) {
                case 1:
                    newItem = new Book(isbn, title, author, publisher, year, copies, genre);
                    break;
                case 2:
                    newItem = new Journal(isbn, title, author, publisher, year, copies, genre);
                    break;
                case 3:
                    newItem = new DigitalResource(isbn, title, author, publisher, year, copies, genre);
                    break;
                case 4:
                    int totalPages = InputValidator.getPositiveInt("Total Pages: ");
                    String format = InputValidator.getNonEmptyInput("Format (PDF/EPUB): ");
                    newItem = new EBook(isbn, title, author, publisher, year, copies, genre, totalPages, format);
                    break;
            }
            
            items.add(newItem);
            library.addItem(newItem);
            saveItems();
            System.out.println("✅ Item added successfully!");
        } catch (Exception e) {
            System.out.println("⚠️ Error adding item: " + e.getMessage());
        }
    }

    private static void modifyItem() {
        printHeader("MODIFY ITEM", "Librarian: " + currentLibrarian.getName());
        try {
            System.out.print("Enter Item ISBN to modify: ");
            String isbn = scanner.nextLine();
            LibraryItem item = findItemByIsbn(isbn);
            
            if (item == null) {
                System.out.println("❌ Item not found.");
                return;
            }
            
            System.out.println("\nCurrent details:");
            System.out.println("Title: " + item.getTitle());
            System.out.println("Author: " + item.getAuthor());
            System.out.println("Publisher: " + item.getPublisher());
            System.out.println("Year: " + item.getYear());
            System.out.println("Total Copies: " + item.getTotalCopies());
            System.out.println("Genre: " + item.getGenre());
            
            System.out.println("\nWhat to modify?");
            System.out.println("1. Title");
            System.out.println("2. Author");
            System.out.println("3. Publisher");
            System.out.println("4. Year");
            System.out.println("5. Total Copies");
            System.out.println("6. Genre");
            System.out.print("Choice: ");
            int choice = InputValidator.getMenuChoice(1, 6);
            
            switch (choice) {
                case 1: item.setTitle(InputValidator.getNonEmptyInput("New Title: ")); break;
                case 2: item.setAuthor(InputValidator.getNonEmptyInput("New Author: ")); break;
                case 3: item.setPublisher(InputValidator.getNonEmptyInput("New Publisher: ")); break;
                case 4: item.setYear(InputValidator.getPositiveInt("New Year: ")); break;
                case 5: item.setTotalCopies(InputValidator.getPositiveInt("New Total Copies: ")); break;
                case 6: item.setGenre(InputValidator.getNonEmptyInput("New Genre: ")); break;
            }
            
            saveItems();
            System.out.println("✅ Item modified successfully!");
        } catch (Exception e) {
            System.out.println("⚠️ Error modifying item: " + e.getMessage());
        }
    }

    private static void removeItem() {
        printHeader("REMOVE ITEM", "Librarian: " + currentLibrarian.getName());
        try {
            System.out.print("Enter Item ISBN to mark as damaged/remove: ");
            String isbn = scanner.nextLine();
            LibraryItem item = findItemByIsbn(isbn);
            
            if (item == null) {
                System.out.println("❌ Item not found.");
                return;
            }
            
            System.out.println("Item: " + item.getTitle());
            if (InputValidator.getConfirmation("Mark this item as damaged and remove from circulation?")) {
                item.removeDamagedCopy();
                if (item.getTotalCopies() == 0) {
                    items.remove(item);
                    library.removeItem(item);
                    System.out.println("✅ Item completely removed from system.");
                } else {
                    System.out.println("✅ One copy marked as damaged. " + item.getAvailableCopies() + " copies remaining.");
                }
                saveItems();
            }
        } catch (Exception e) {
            System.out.println("⚠️ Error removing item: " + e.getMessage());
        }
    }

    // ==================== USER MANAGEMENT (LIBRARIAN) ====================
    private static void viewAllUsers() {
        printHeader("ALL USERS", "Librarian: " + currentLibrarian.getName());
        try {
            System.out.printf("%-12s %-20s %-15s %-25s %-10s%n", "User ID", "Name", "Phone", "Email", "Type");
            System.out.println("+======================================================================+");
            
            for (User u : users) {
                String type = u instanceof Student ? "Student" : (u instanceof Faculty ? "Faculty" : "Public");
                System.out.printf("%-12s %-20s %-15s %-25s %-10s%n", 
                    u.getUserId(), 
                    truncate(u.getName(), 18), 
                    u.getPhone(), 
                    truncate(u.getEmail(), 23), 
                    type);
            }
        } catch (Exception e) {
            System.out.println("⚠️ Error displaying users: " + e.getMessage());
        }
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private static void deleteUser() {
        printHeader("DELETE USER", "Librarian: " + currentLibrarian.getName());
        try {
            System.out.print("Enter User ID to delete: ");
            String userId = scanner.nextLine();
            
            User target = null;
            for (User u : users) {
                if (u.getUserId().equals(userId)) {
                    target = u;
                    break;
                }
            }
            
            if (target == null) {
                System.out.println("❌ User not found.");
                return;
            }
            
            for (BorrowRecord br : borrowRecords) {
                if (br.getUserId().equals(userId) && br.isActive()) {
                    System.out.println("❌ Cannot delete user with active borrowings. Please return items first.");
                    return;
                }
            }
            
            System.out.println("User: " + target.getName() + " (" + target.getUserId() + ")");
            if (InputValidator.getConfirmation("Delete this user?")) {
                users.remove(target);
                saveUsers();
                System.out.println("✅ User deleted successfully.");
            }
        } catch (Exception e) {
            System.out.println("⚠️ Error deleting user: " + e.getMessage());
        }
    }

    // ==================== LOST BOOKS MANAGEMENT ====================
    private static void markItemAsLost() {
        printHeader("MARK ITEM AS LOST", "Librarian: " + currentLibrarian.getName());
        try {
            System.out.println("⚠️ Lost Book Fine: RM 50.00");
            System.out.println();
            
            System.out.print("Enter Borrow Record ID: ");
            int recordId = InputValidator.getPositiveInt("");
            
            for (BorrowRecord br : borrowRecords) {
                if (br.getRecordId() == recordId && !br.isLost() && br.isActive()) {
                    System.out.println("\nBorrow Record Details:");
                    System.out.println("  User ID: " + br.getUserId());
                    System.out.println("  ISBN: " + br.getIsbn());
                    System.out.println("  Borrow Date: " + br.getBorrowDate());
                    System.out.println("  Due Date: " + br.getDueDate());
                    
                    if (InputValidator.getConfirmation("\nMark this item as lost?")) {
                        double lostBookFine = 50.00;
                        br.markAsLost(lostBookFine);
                        
                        LibraryItem item = findItemByIsbn(br.getIsbn());
                        if (item != null) {
                            item.removeDamagedCopy();
                            if (item.getTotalCopies() == 0) {
                                items.remove(item);
                                library.removeItem(item);
                                System.out.println("   📚 Last copy removed from inventory.");
                            } else {
                                System.out.println("   📚 " + item.getAvailableCopies() + " copies remaining.");
                            }
                            saveItems();
                        }
                        
                        saveBorrowRecords();
                        System.out.println("\n✅ Item marked as lost. Fine applied: RM 50.00");
                    }
                    System.out.println("\nPress Enter to continue...");
                    scanner.nextLine();
                    return;
                }
            }
            System.out.println("❌ No active borrow record found with ID: " + recordId);
        } catch (Exception e) {
            System.out.println("⚠️ Error: " + e.getMessage());
        }
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private static void viewLostBooksReport() {
        printHeader("LOST BOOKS REPORT", "Librarian: " + currentLibrarian.getName());
        try {
            System.out.printf("%-10s %-15s %-20s %-12s %-12s %-10s%n", 
                "Record ID", "User ID", "ISBN", "Borrow Date", "Lost Date", "Fine");
            System.out.println("+========================================================================================+");
            
            boolean hasLost = false;
            for (BorrowRecord br : borrowRecords) {
                if (br.isLost()) {
                    hasLost = true;
                    System.out.printf("%-10d %-15s %-20s %-12s %-12s RM %-8.2f%n", 
                        br.getRecordId(), br.getUserId(), br.getIsbn(), 
                        br.getBorrowDate(), br.getLostDate(), br.getFine());
                }
            }
            
            if (!hasLost) {
                System.out.println("No lost books found.");
            }
        } catch (Exception e) {
            System.out.println("⚠️ Error: " + e.getMessage());
        }
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private static void markLostBookAsFound() {
        printHeader("MARK LOST BOOK AS FOUND", "Librarian: " + currentLibrarian.getName());
        try {
            System.out.print("Enter Borrow Record ID: ");
            int recordId = InputValidator.getPositiveInt("");
            
            for (BorrowRecord br : borrowRecords) {
                if (br.getRecordId() == recordId && br.isLost()) {
                    System.out.println("\nLost Book Details:");
                    System.out.println("  User ID: " + br.getUserId());
                    System.out.println("  ISBN: " + br.getIsbn());
                    System.out.println("  Lost Date: " + br.getLostDate());
                    System.out.println("  Fine Applied: RM " + br.getFine());
                    
                    if (InputValidator.getConfirmation("\nMark this book as found?")) {
                        br.markAsFound();
                        
                        LibraryItem item = findItemByIsbn(br.getIsbn());
                        if (item != null) {
                            item.setDamaged(false);
                            saveItems();
                        }
                        
                        saveBorrowRecords();
                        System.out.println("\n✅ Book marked as found and returned. Fine waived.");
                    }
                    System.out.println("\nPress Enter to continue...");
                    scanner.nextLine();
                    return;
                }
            }
            System.out.println("❌ No lost book found with Record ID: " + recordId);
        } catch (Exception e) {
            System.out.println("⚠️ Error: " + e.getMessage());
        }
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    // ==================== INTER-LIBRARY LOAN MANAGEMENT (LIBRARIAN) ====================
    private static void viewAllInterLibraryRequests() {
        printHeader("ALL INTER-LIBRARY REQUESTS", "Librarian: " + currentLibrarian.getName());
        try {
            System.out.printf("%-12s %-12s %-15s %-30s %-20s %-12s%n", 
                "Request ID", "User ID", "ISBN", "Title", "From Library", "Status");
            System.out.println("+========================================================================================================+");
            
            if (interLibraryLoans.isEmpty()) {
                System.out.println("No inter-library loan requests found.");
            } else {
                for (InterLibraryLoan loan : interLibraryLoans) {
                    System.out.printf("%-12d %-12s %-15s %-30s %-20s %-12s%n", 
                        loan.getRequestId(), loan.getUserId(), loan.getIsbn(),
                        truncate(loan.getBookTitle(), 28), truncate(loan.getOwningLibrary(), 18), loan.getStatus());
                }
            }
        } catch (Exception e) {
            System.out.println("⚠️ Error: " + e.getMessage());
        }
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private static void approveInterLibraryRequest() {
        printHeader("APPROVE INTER-LIBRARY REQUEST", "Librarian: " + currentLibrarian.getName());
        try {
            System.out.print("Enter Request ID: ");
            int id = InputValidator.getPositiveInt("");
            
            for (InterLibraryLoan loan : interLibraryLoans) {
                if (loan.getRequestId() == id && loan.isPending()) {
                    loan.approve();
                    saveInterLibraryLoans();
                    System.out.println("✅ Request approved. The book will be shipped.");
                    System.out.println("\nPress Enter to continue...");
                    scanner.nextLine();
                    return;
                }
            }
            System.out.println("❌ Request not found or already processed.");
        } catch (Exception e) {
            System.out.println("⚠️ Error: " + e.getMessage());
        }
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private static void rejectInterLibraryRequest() {
        printHeader("REJECT INTER-LIBRARY REQUEST", "Librarian: " + currentLibrarian.getName());
        try {
            System.out.print("Enter Request ID: ");
            int id = InputValidator.getPositiveInt("");
            
            for (InterLibraryLoan loan : interLibraryLoans) {
                if (loan.getRequestId() == id && loan.isPending()) {
                    System.out.print("Reason: ");
                    String reason = scanner.nextLine();
                    loan.reject(reason);
                    saveInterLibraryLoans();
                    System.out.println("✅ Request rejected.");
                    System.out.println("\nPress Enter to continue...");
                    scanner.nextLine();
                    return;
                }
            }
            System.out.println("❌ Request not found or already processed.");
        } catch (Exception e) {
            System.out.println("⚠️ Error: " + e.getMessage());
        }
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    // ==================== REPORTS ====================
    private static void mostBorrowedReport() {
        printHeader("MOST BORROWED BOOKS REPORT", "Librarian: " + currentLibrarian.getName());
        try {
            Map<String, Integer> borrowCount = new HashMap<>();
            for (BorrowRecord br : borrowRecords) {
                if (!br.isLost()) {
                    borrowCount.put(br.getIsbn(), borrowCount.getOrDefault(br.getIsbn(), 0) + 1);
                }
            }
            
            List<Map.Entry<String, Integer>> sorted = new ArrayList<>(borrowCount.entrySet());
            sorted.sort((a, b) -> b.getValue().compareTo(a.getValue()));
            
            System.out.printf("%-15s %-45s %-10s%n", "ISBN", "Title", "Times Borrowed");
            System.out.println("+======================================================================+");
            
            int count = 0;
            for (Map.Entry<String, Integer> entry : sorted) {
                if (count++ >= 10) break;
                LibraryItem item = findItemByIsbn(entry.getKey());
                String title = (item != null) ? item.getTitle() : "Unknown";
                System.out.printf("%-15s %-45s %-10d%n", entry.getKey(), truncate(title, 43), entry.getValue());
            }
        } catch (Exception e) {
            System.out.println("⚠️ Error generating report: " + e.getMessage());
        }
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private static void revenueReport() {
        printHeader("REVENUE REPORT", "Librarian: " + currentLibrarian.getName());
        try {
            double totalFines = 0;
            int paidFines = 0;
            
            for (BorrowRecord br : borrowRecords) {
                if (br.getFine() > 0 && !br.isLost()) {
                    totalFines += br.getFine();
                    paidFines++;
                }
            }
            
            System.out.println("Total fines collected: RM " + String.format("%.2f", totalFines));
            System.out.println("Number of fine transactions: " + paidFines);
            System.out.println("Average fine per transaction: RM " + String.format("%.2f", paidFines > 0 ? totalFines / paidFines : 0));
        } catch (Exception e) {
            System.out.println("⚠️ Error generating report: " + e.getMessage());
        }
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private static void overdueReport() {
        printHeader("OVERDUE ITEMS REPORT", "Librarian: " + currentLibrarian.getName());
        try {
            System.out.printf("%-10s %-15s %-20s %-12s %-10s%n", "Record ID", "User ID", "ISBN", "Due Date", "Days Late");
            System.out.println("+======================================================================+");
            
            LocalDate today = LocalDate.now();
            boolean hasOverdue = false;
            
            for (BorrowRecord br : borrowRecords) {
                if (!br.isLost() && (br.getStatus().equals("OVERDUE") || (br.getStatus().equals("BORROWED") && br.getDueDate().isBefore(today)))) {
                    hasOverdue = true;
                    long daysLate = ChronoUnit.DAYS.between(br.getDueDate(), today);
                    System.out.printf("%-10d %-15s %-20s %-12s %-10d%n", 
                        br.getRecordId(), br.getUserId(), br.getIsbn(), br.getDueDate(), daysLate);
                }
            }
            
            if (!hasOverdue) {
                System.out.println("No overdue items.");
            }
        } catch (Exception e) {
            System.out.println("⚠️ Error generating report: " + e.getMessage());
        }
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private static void borrowReport() {
        printHeader("BORROW REPORT (MONTHLY)", "Librarian: " + currentLibrarian.getName());
        try {
            System.out.print("Enter year (e.g., 2024): ");
            int year = InputValidator.getPositiveInt("");
            System.out.print("Enter month (1-12): ");
            int month = InputValidator.getMenuChoice(1, 12);
            
            int totalBorrows = 0;
            int totalReturns = 0;
            double totalFines = 0;
            
            System.out.println("\n=== BORROW REPORT for " + year + "-" + String.format("%02d", month) + " ===");
            System.out.printf("%-10s %-15s %-15s %-12s %-10s%n", "Record ID", "User ID", "ISBN", "Date", "Type");
            System.out.println("+--------------------------------------------------------------------------------+");
            
            for (BorrowRecord br : borrowRecords) {
                if (!br.isLost() && br.getBorrowDate().getYear() == year && br.getBorrowDate().getMonthValue() == month) {
                    totalBorrows++;
                    System.out.printf("%-10d %-15s %-15s %-12s %-10s%n", 
                        br.getRecordId(), br.getUserId(), br.getIsbn(), br.getBorrowDate(), "BORROW");
                }
                if (!br.isLost() && br.getReturnDate() != null && br.getReturnDate().getYear() == year && 
                    br.getReturnDate().getMonthValue() == month) {
                    totalReturns++;
                    totalFines += br.getFine();
                    System.out.printf("%-10d %-15s %-15s %-12s %-10s%n", 
                        br.getRecordId(), br.getUserId(), br.getIsbn(), br.getReturnDate(), "RETURN");
                }
            }
            
            System.out.println("\n=== SUMMARY ===");
            System.out.println("Total Borrows: " + totalBorrows);
            System.out.println("Total Returns: " + totalReturns);
            System.out.println("Total Fines Collected: RM " + String.format("%.2f", totalFines));
        } catch (Exception e) {
            System.out.println("⚠️ Error generating report: " + e.getMessage());
        }
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private static void popularBooksReport() {
        printHeader("POPULAR BOOKS REPORT (BY GENRE)", "Librarian: " + currentLibrarian.getName());
        try {
            Map<String, Integer> borrowCount = new HashMap<>();
            Map<String, String> bookGenre = new HashMap<>();
            
            for (BorrowRecord br : borrowRecords) {
                if (!br.isLost()) {
                    borrowCount.put(br.getIsbn(), borrowCount.getOrDefault(br.getIsbn(), 0) + 1);
                    
                    LibraryItem item = findItemByIsbn(br.getIsbn());
                    if (item != null && !bookGenre.containsKey(br.getIsbn())) {
                        bookGenre.put(br.getIsbn(), item.getGenre());
                    }
                }
            }
            
            List<Map.Entry<String, Integer>> sorted = new ArrayList<>(borrowCount.entrySet());
            sorted.sort((a, b) -> b.getValue().compareTo(a.getValue()));
            
            System.out.printf("%-15s %-35s %-15s %-10s%n", "ISBN", "Title", "Genre", "Times Borrowed");
            System.out.println("+--------------------------------------------------------------------------------+");
            
            int count = 0;
            for (Map.Entry<String, Integer> entry : sorted) {
                if (count++ >= 15) break;
                LibraryItem item = findItemByIsbn(entry.getKey());
                String title = (item != null) ? item.getTitle() : "Unknown";
                String genre = bookGenre.getOrDefault(entry.getKey(), "General");
                System.out.printf("%-15s %-35s %-15s %-10d%n", 
                    entry.getKey(), truncate(title, 33), genre, entry.getValue());
            }
        } catch (Exception e) {
            System.out.println("⚠️ Error generating report: " + e.getMessage());
        }
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    // ==================== UTILITY METHODS ====================
    private static LibraryItem findItemByIsbn(String isbn) {
        for (LibraryItem item : items) {
            if (item.getIsbn().equals(isbn)) {
                return item;
            }
        }
        return null;
    }

    private static String truncate(String str, int maxLen) {
        if (str == null) return "";
        if (str.length() <= maxLen) return str;
        return str.substring(0, maxLen - 3) + "...";
    }
}