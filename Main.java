import model.*;
import data.FileHandler;
import utils.InputValidator;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class Main {
    private static List<User> users = new ArrayList<>();
    private static List<Librarian> librarians = new ArrayList<>();
    private static List<LibraryItem> items = new ArrayList<>();
    private static List<BorrowRecord> borrowRecords = new ArrayList<>();
    private static List<Reservation> reservations = new ArrayList<>();
    private static List<StudyRoom> studyRooms = new ArrayList<>();
    private static List<RoomBooking> roomBookings = new ArrayList<>();
    
    private static User currentUser = null;
    private static Librarian currentLibrarian = null;
    private static Library library = new Library("Smart Library SDN BHD", "Kuala Lumpur, Malaysia");
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        loadAllData();
        mainMenu();
        saveAllData();
        System.out.println("\nThank you for using Smart Library System!");
    }

    private static void printHeader(String title, String role) {
        System.out.println("\n+======================================================================+");
        System.out.println("|                                                                      |");
        System.out.println("|                    SMART LIBRARY SYSTEM SDN BHD                      |");
        System.out.println("|                     \"Your Digital Library Partner\"                   |");
        System.out.println("|                                                                      |");
        System.out.println("+======================================================================+");
        System.out.println("|  " + String.format("%-62s", title) + "      |");
        System.out.println("|  Role: " + String.format("%-56s", role) + "      |");
        System.out.println("+======================================================================+");
    }

    // ==================== DATA LOADING & SAVING ====================
    
    private static void loadAllData() {
        loadUsers();
        loadLibrarians();
        loadItems();
        loadBorrowRecords();
        loadReservations();
        loadStudyRooms();
        loadRoomBookings();
        System.out.println("System loaded: " + users.size() + " users, " + librarians.size() + " librarians, " + items.size() + " items, " + studyRooms.size() + " rooms");
    }

    private static void saveAllData() {
        saveUsers();
        saveLibrarians();
        saveItems();
        saveBorrowRecords();
        saveReservations();
        saveStudyRooms();
        saveRoomBookings();
    }

    private static void loadUsers() {
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
    }

    private static void saveUsers() {
        List<String> lines = new ArrayList<>();
        for (User u : users) {
            String type = u instanceof Student ? "Student" : (u instanceof Faculty ? "Faculty" : "PublicMember");
            lines.add(type + "," + u.getUserId() + "," + u.getName() + "," + u.getPhone() + "," + u.getEmail() + "," + u.getPassword());
        }
        FileHandler.writeUsers(lines);
    }

    private static void loadLibrarians() {
        for (String[] data : FileHandler.readLibrarians()) {
            if (data.length >= 3) {
                librarians.add(new Librarian(data[0], data[1], data[2]));
            }
        }
    }

    private static void saveLibrarians() {
        List<String> lines = new ArrayList<>();
        for (Librarian l : librarians) {
            lines.add(l.toString());
        }
        FileHandler.writeLibrarians(lines);
    }

    private static void loadItems() {
        for (String[] data : FileHandler.readItems()) {
            if (data.length < 9) continue;
            String type = data[0];
            String isbn = data[1], title = data[2], author = data[3], publisher = data[4];
            int year = Integer.parseInt(data[5]), totalCopies = Integer.parseInt(data[6]);
            boolean isDamaged = Boolean.parseBoolean(data[8]);
            
            LibraryItem item = null;
            switch (type) {
                case "Book": 
                    item = new Book(isbn, title, author, publisher, year, totalCopies); 
                    break;
                case "Journal": 
                    item = new Journal(isbn, title, author, publisher, year, totalCopies); 
                    break;
                case "DigitalResource": 
                    item = new DigitalResource(isbn, title, author, publisher, year, totalCopies); 
                    break;
            }
            if (item != null) {
                if (isDamaged) item.setDamaged(true);
                items.add(item);
                library.addItem(item);
    }
            }
        }
    

    private static void saveItems() {
        List<String> lines = new ArrayList<>();
        for (LibraryItem i : items) {
            lines.add(i.toString());
        }
        FileHandler.writeItems(lines);
    }

    private static void loadBorrowRecords() {
        for (String[] data : FileHandler.readBorrows()) {
            if (data.length >= 8) {
                int id = Integer.parseInt(data[0]);
                double fine = Double.parseDouble(data[6]);
                borrowRecords.add(new BorrowRecord(id, data[1], data[2], data[3], data[4], data[5], fine, data[7]));
            }
        }
    }

    private static void saveBorrowRecords() {
        List<String> lines = new ArrayList<>();
        for (BorrowRecord br : borrowRecords) {
            lines.add(br.toString());
        }
        FileHandler.writeBorrows(lines);
    }

    private static void loadReservations() {
        for (String[] data : FileHandler.readReservations()) {
            if (data.length >= 5) {
                int id = Integer.parseInt(data[0]);
                reservations.add(new Reservation(id, data[1], data[2], data[3], data[4]));
            }
        }
    }

    private static void saveReservations() {
        List<String> lines = new ArrayList<>();
        for (Reservation r : reservations) {
            lines.add(r.toString());
        }
        FileHandler.writeReservations(lines);
    }

    private static void loadStudyRooms() {
        for (String[] data : FileHandler.readRooms()) {
            if (data.length >= 5) {
                StudyRoom room = new StudyRoom(data[0], data[1], Integer.parseInt(data[2]), data[3]);
                room.setAvailable(Boolean.parseBoolean(data[4]));
                studyRooms.add(room);
            }
        }
        // Create default rooms if none exist
        if (studyRooms.isEmpty()) {
            studyRooms.add(new StudyRoom("R001", "Main Study Room", 8, "Projector, Whiteboard, WiFi"));
            studyRooms.add(new StudyRoom("R002", "Group Study Room", 6, "Whiteboard, WiFi"));
            studyRooms.add(new StudyRoom("R003", "Quiet Room", 2, "Desk Lamp"));
            studyRooms.add(new StudyRoom("R004", "Premium Room", 12, "Projector, Whiteboard, WiFi, AC"));
        }
    }

    private static void saveStudyRooms() {
        List<String> lines = new ArrayList<>();
        for (StudyRoom r : studyRooms) {
            lines.add(r.toString());
        }
        FileHandler.writeRooms(lines);
    }

    private static void loadRoomBookings() {
        for (String[] data : FileHandler.readBookings()) {
            if (data.length >= 7) {
                int id = Integer.parseInt(data[0]);
                roomBookings.add(new RoomBooking(id, data[1], data[2], data[3], data[4], data[5], data[6]));
            }
        }
    }

    private static void saveRoomBookings() {
        List<String> lines = new ArrayList<>();
        for (RoomBooking rb : roomBookings) {
            lines.add(rb.toString());
        }
        FileHandler.writeBookings(lines);
    }

    // ==================== MAIN MENU ====================

    private static void mainMenu() {
        while (true) {
            printHeader("MAIN MENU", "None");
            System.out.println("|                                                                      |");
            System.out.println("|    【1】 User Login                                                  |");
            System.out.println("|    【2】 User Register                                               |");
            System.out.println("|    【3】 Librarian Login                                             |");
            System.out.println("|                                                                      |");
            System.out.println("|    【0】 Exit                                                        |");
            System.out.println("|                                                                      |");
            System.out.println("+======================================================================+");
            System.out.print("Choice: ");
            
            int choice = InputValidator.getMenuChoice(0, 3);
            
            switch (choice) {
                case 1: userLogin(); break;
                case 2: userRegister(); break;
                case 3: librarianLogin(); break;
                case 0: return;
            }
        }
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
        System.out.println("1. Student");
        System.out.println("2. Faculty");
        System.out.println("3. Public Member");
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
            String role = "User: " + currentUser.getName() + " (" + 
                (currentUser instanceof Student ? "Student" : 
                 currentUser instanceof Faculty ? "Faculty" : "Public Member") + ")";
            printHeader("USER MENU", role);
            System.out.println("|                                                                       |");
            System.out.println("|    【1】 Browse All Items                                             |");
            System.out.println("|    【2】 Search Items                                                 |");
            System.out.println("|    【3】 Borrow Item                                                  |");
            System.out.println("|    【4】 Return Item                                                  |");
            System.out.println("|    【5】 View My Borrowings                                           |");
            System.out.println("|    【6】 Reserve Item                                                 |");
            System.out.println("|    【7】 View My Reservations                                         |");
            System.out.println("|    【8】 Pay Fine                                                     |");
            System.out.println("|    【9】 My Profile                                                   |");
            System.out.println("|    【10】 Book Study Room                                             |");
            System.out.println("|    【11】 View My Room Bookings                                       |");
            System.out.println("|                                                                       |");
            System.out.println("|    【0】 Logout                                                       |");
            System.out.println("|                                                                       |");
            System.out.println("+========================================================================+");
            System.out.print("Choice: ");
            
            int choice = InputValidator.getMenuChoice(0, 11);
            
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
                case 10: bookStudyRoom(); break;
                case 11: viewMyRoomBookings(); break;
                case 0: 
                    currentUser = null;
                    System.out.println("Logged out.");
                    break;
            }
        }
    }

    private static void browseItems() {
        printHeader("BROWSE ALL ITEMS", "User: " + currentUser.getName());
        System.out.printf("%-15s %-35s %-20s %-10s%n", "ISBN", "Title", "Author", "Available");
        System.out.println("+======================================================================+");
        
        if (items.isEmpty()) {
            System.out.println("❌ No items available in the library.");
        } else {
            for (LibraryItem item : items) {
                System.out.printf("%-15s %-35s %-20s %-10d%n", 
                    item.getIsbn(), 
                    truncate(item.getTitle(), 33), 
                    truncate(item.getAuthor(), 18), 
                    item.getAvailableCopies());
            }
        }
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private static void searchItems() {
        printHeader("SEARCH ITEMS", "User: " + currentUser.getName());
        System.out.println("1. Search by ISBN");
        System.out.println("2. Search by Title");
        System.out.println("3. Search by Author");
        System.out.print("Choice: ");
        int choice = InputValidator.getMenuChoice(1, 3);
        
        System.out.print("Enter keyword: ");
        String keyword = scanner.nextLine().toLowerCase();
        
        System.out.printf("%-15s %-35s %-20s %-10s%n", "ISBN", "Title", "Author", "Available");
        System.out.println("+======================================================================+");
        
        boolean found = false;
        for (LibraryItem item : items) {
            boolean match = false;
            switch (choice) {
                case 1: match = item.getIsbn().toLowerCase().contains(keyword); break;
                case 2: match = item.getTitle().toLowerCase().contains(keyword); break;
                case 3: match = item.getAuthor().toLowerCase().contains(keyword); break;
            }
            if (match) {
                found = true;
                System.out.printf("%-15s %-35s %-20s %-10d%n", 
                    item.getIsbn(), 
                    truncate(item.getTitle(), 33), 
                    truncate(item.getAuthor(), 18), 
                    item.getAvailableCopies());
            }
        }
        
        if (!found) {
            System.out.println("❌ No items found matching your search criteria.");
        }
        
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

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
                if (br.getUserId().equals(currentUser.getUserId()) && br.isActive()) {
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
            System.out.println("Error during borrowing: " + e.getMessage());
        }
    }

    private static void returnItem() {
        printHeader("RETURN ITEM", "User: " + currentUser.getName());
        try {
            System.out.print("Enter Item ISBN to return: ");
            String isbn = scanner.nextLine();
            
            BorrowRecord activeRecord = null;
            for (BorrowRecord br : borrowRecords) {
                if (br.getIsbn().equals(isbn) && br.getUserId().equals(currentUser.getUserId()) && br.isActive()) {
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
            System.out.println("Error during return: " + e.getMessage());
        }
    }

    private static void viewMyBorrowings() {
        printHeader("MY BORROWING HISTORY", "User: " + currentUser.getName());
        System.out.printf("%-10s %-15s %-12s %-12s %-10s%n", "Record ID", "ISBN", "Due Date", "Status", "Fine");
        System.out.println("+======================================================================+");
        
        boolean hasRecords = false;
        for (BorrowRecord br : borrowRecords) {
            if (br.getUserId().equals(currentUser.getUserId())) {
                hasRecords = true;
                System.out.printf("%-10d %-15s %-12s %-12s RM %-8.2f%n", 
                    br.getRecordId(), br.getIsbn(), br.getDueDate(), br.getStatus(), br.getFine());
            }
        }
        
        if (!hasRecords) {
            System.out.println("No borrowing records found.");
        }
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private static void reserveItem() {
        printHeader("RESERVE ITEM", "User: " + currentUser.getName());
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
    }

    private static void viewMyReservations() {
        printHeader("MY RESERVATIONS", "User: " + currentUser.getName());
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
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private static void payFine() {
        printHeader("PAY FINE", "User: " + currentUser.getName());
        double totalFine = 0;
        List<BorrowRecord> fineRecords = new ArrayList<>();
        
        for (BorrowRecord br : borrowRecords) {
            if (br.getUserId().equals(currentUser.getUserId()) && br.getFine() > 0) {
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
    }

    private static void viewMyProfile() {
        printHeader("MY PROFILE", "User: " + currentUser.getName());
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

    // ==================== STUDY ROOM BOOKING ====================

    private static void bookStudyRoom() {
        printHeader("BOOK STUDY ROOM", "User: " + currentUser.getName());
        
        // Display available rooms
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
        
        // Select room
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
        
        // Select date
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
        
        // Select time slot
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
        
        // Check for existing booking
        for (RoomBooking rb : roomBookings) {
            if (rb.getRoomId().equals(roomId) && rb.getBookingDate().equals(bookingDate) && 
                rb.getStartTime().equals(startTime) && rb.isActive()) {
                System.out.println("❌ This room is already booked for this time slot.");
                System.out.println("\nPress Enter to continue...");
                scanner.nextLine();
                return;
            }
        }
        
        // Create booking
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
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private static void viewMyRoomBookings() {
        printHeader("MY ROOM BOOKINGS", "User: " + currentUser.getName());
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
        
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    // ==================== LIBRARIAN MENU ====================

    private static void librarianMenu() {
        while (currentLibrarian != null) {
            String role = "Librarian: " + currentLibrarian.getName();
            printHeader("LIBRARIAN MENU", role);
            System.out.println("|                                                                      |");
            System.out.println("|  ITEM MANAGEMENT                                                     |");
            System.out.println("|    【1】 Add New Item                                                |");
            System.out.println("|    【2】 Modify Item                                                 |");
            System.out.println("|    【3】 Remove Item (Mark as Damaged)                               |");
            System.out.println("|    【4】 View All Items                                              |");
            System.out.println("|                                                                      |");
            System.out.println("|  USER MANAGEMENT                                                     |");
            System.out.println("|    【5】 View All Users                                              |");
            System.out.println("|    【6】 Delete User                                                 |");
            System.out.println("|                                                                      |");
            System.out.println("|  REPORTS                                                             |");
            System.out.println("|    【7】 Most Borrowed Books Report                                  |");
            System.out.println("|    【8】 Revenue Report (Fines Collected)                            |");
            System.out.println("|    【9】 Overdue Items Report                                        |");
            System.out.println("|    【10】 Borrow Report (Monthly)                                    |");
            System.out.println("|    【11】 Popular Books Report (Genre-based)                         |");
            System.out.println("|                                                                      |");
            System.out.println("|    【0】 Logout                                                      |");
            System.out.println("|                                                                      |");
            System.out.println("+=======================================================================+");
            System.out.print("Choice: ");
            
            int choice = InputValidator.getMenuChoice(0, 11);
            
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
                case 0:
                    currentLibrarian = null;
                    System.out.println("Logged out.");
                    break;
            }
        }
    }

    private static void browseItemsLibrarian() {
        printHeader("BROWSE ALL ITEMS", "Librarian: " + currentLibrarian.getName());
        System.out.printf("%-15s %-35s %-20s %-10s %-10s%n", "ISBN", "Title", "Author", "Available", "Damaged");
        System.out.println("+======================================================================+");
        for (LibraryItem item : items) {
            System.out.printf("%-15s %-35s %-20s %-10d %-10s%n", 
                item.getIsbn(), 
                truncate(item.getTitle(), 33), 
                truncate(item.getAuthor(), 18), 
                item.getAvailableCopies(),
                item.isDamaged() ? "Yes" : "No");
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
            System.out.print("Choice: ");
            int type = InputValidator.getMenuChoice(1, 3);
            
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
            
            LibraryItem newItem = null;
            switch (type) {
                case 1: newItem = new Book(isbn, title, author, publisher, year, copies); break;
                case 2: newItem = new Journal(isbn, title, author, publisher, year, copies); break;
                case 3: newItem = new DigitalResource(isbn, title, author, publisher, year, copies); break;
            }
            
            items.add(newItem);
            saveItems();
            System.out.println("✅ Item added successfully!");
        } catch (Exception e) {
            System.out.println("Error adding item: " + e.getMessage());
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
            
            System.out.println("\nWhat to modify?");
            System.out.println("1. Title");
            System.out.println("2. Author");
            System.out.println("3. Publisher");
            System.out.println("4. Year");
            System.out.println("5. Total Copies");
            System.out.print("Choice: ");
            int choice = InputValidator.getMenuChoice(1, 5);
            
            switch (choice) {
                case 1: item.setTitle(InputValidator.getNonEmptyInput("New Title: ")); break;
                case 2: item.setAuthor(InputValidator.getNonEmptyInput("New Author: ")); break;
                case 3: item.setPublisher(InputValidator.getNonEmptyInput("New Publisher: ")); break;
                case 4: item.setYear(InputValidator.getPositiveInt("New Year: ")); break;
                case 5: item.setTotalCopies(InputValidator.getPositiveInt("New Total Copies: ")); break;
            }
            
            saveItems();
            System.out.println("✅ Item modified successfully!");
        } catch (Exception e) {
            System.out.println("Error modifying item: " + e.getMessage());
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
                    System.out.println("✅ Item completely removed from system.");
                } else {
                    System.out.println("✅ One copy marked as damaged. " + item.getAvailableCopies() + " copies remaining.");
                }
                saveItems();
            }
        } catch (Exception e) {
            System.out.println("Error removing item: " + e.getMessage());
        }
    }

    private static void viewAllUsers() {
        printHeader("ALL USERS", "Librarian: " + currentLibrarian.getName());
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
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private static void deleteUser() {
        printHeader("DELETE USER", "Librarian: " + currentLibrarian.getName());
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
    }

    private static void mostBorrowedReport() {
        printHeader("MOST BORROWED BOOKS REPORT", "Librarian: " + currentLibrarian.getName());
        Map<String, Integer> borrowCount = new HashMap<>();
        for (BorrowRecord br : borrowRecords) {
            borrowCount.put(br.getIsbn(), borrowCount.getOrDefault(br.getIsbn(), 0) + 1);
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
        
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private static void revenueReport() {
        printHeader("REVENUE REPORT", "Librarian: " + currentLibrarian.getName());
        double totalFines = 0;
        int paidFines = 0;
        
        for (BorrowRecord br : borrowRecords) {
            if (br.getFine() > 0) {
                totalFines += br.getFine();
                paidFines++;
            }
        }
        
        System.out.println("Total fines collected: RM " + String.format("%.2f", totalFines));
        System.out.println("Number of fine transactions: " + paidFines);
        System.out.println("Average fine per transaction: RM " + String.format("%.2f", paidFines > 0 ? totalFines / paidFines : 0));
        
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private static void overdueReport() {
        printHeader("OVERDUE ITEMS REPORT", "Librarian: " + currentLibrarian.getName());
        System.out.printf("%-10s %-15s %-20s %-12s %-10s%n", "Record ID", "User ID", "ISBN", "Due Date", "Days Late");
        System.out.println("+======================================================================+");
        
        LocalDate today = LocalDate.now();
        boolean hasOverdue = false;
        
        for (BorrowRecord br : borrowRecords) {
            if (br.getStatus().equals("OVERDUE") || (br.getStatus().equals("BORROWED") && br.getDueDate().isBefore(today))) {
                hasOverdue = true;
                long daysLate = ChronoUnit.DAYS.between(br.getDueDate(), today);
                System.out.printf("%-10d %-15s %-20s %-12s %-10d%n", 
                    br.getRecordId(), br.getUserId(), br.getIsbn(), br.getDueDate(), daysLate);
            }
        }
        
        if (!hasOverdue) {
            System.out.println("No overdue items.");
        }
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private static void borrowReport() {
        printHeader("BORROW REPORT (MONTHLY)", "Librarian: " + currentLibrarian.getName());
        
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
            if (br.getBorrowDate().getYear() == year && br.getBorrowDate().getMonthValue() == month) {
                totalBorrows++;
                System.out.printf("%-10d %-15s %-15s %-12s %-10s%n", 
                    br.getRecordId(), br.getUserId(), br.getIsbn(), br.getBorrowDate(), "BORROW");
            }
            if (br.getReturnDate() != null && br.getReturnDate().getYear() == year && 
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
        
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private static void popularBooksReport() {
        printHeader("POPULAR BOOKS REPORT (BY GENRE)", "Librarian: " + currentLibrarian.getName());
        
        Map<String, Integer> borrowCount = new HashMap<>();
        Map<String, String> bookGenre = new HashMap<>();
        
        for (BorrowRecord br : borrowRecords) {
            borrowCount.put(br.getIsbn(), borrowCount.getOrDefault(br.getIsbn(), 0) + 1);
            
            LibraryItem item = findItemByIsbn(br.getIsbn());
            if (item != null && !bookGenre.containsKey(br.getIsbn())) {
                String title = item.getTitle().toLowerCase();
                if (title.contains("java") || title.contains("python") || title.contains("programming")) {
                    bookGenre.put(br.getIsbn(), "Programming");
                } else if (title.contains("database") || title.contains("sql")) {
                    bookGenre.put(br.getIsbn(), "Database");
                } else if (title.contains("network") || title.contains("security")) {
                    bookGenre.put(br.getIsbn(), "Networking");
                } else if (title.contains("ai") || title.contains("intelligence")) {
                    bookGenre.put(br.getIsbn(), "AI");
                } else {
                    bookGenre.put(br.getIsbn(), "General");
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