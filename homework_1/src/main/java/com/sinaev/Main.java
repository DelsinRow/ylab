package com.sinaev;

import com.sinaev.models.Booking;
import com.sinaev.models.Room;
import com.sinaev.models.RoomType;
import com.sinaev.models.User;
import com.sinaev.services.BookingService;
import com.sinaev.services.RoomService;
import com.sinaev.services.UserService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

public class Main {
    private static User currentUser = null;
    private static User admin = new User("admin", "admin", true);
    private static Room room1 = new Room("first", RoomType.MEETING_ROOM);
    private static Room room2 = new Room("second", RoomType.WORKSPACE);
    private static Room room3 = new Room("third", RoomType.MEETING_ROOM);

    public static void main(String[] args) {
        List<Room> rooms = new ArrayList<>();
        List<Booking> bookings = new ArrayList<>();

        UserService userService = new UserService();
        RoomService roomService = new RoomService(rooms);
        BookingService bookingService = new BookingService(bookings, rooms);

        Scanner scanner = new Scanner(System.in);
        Map<String, Runnable> commands = new HashMap<>();

        uploadInitData(userService, roomService, bookingService);

        commands.put("info", () -> {
            System.out.println(ConstantValues.INFO);
        });

        commands.put("register", () -> {
            System.out.print("Enter username: ");
            String username = scanner.nextLine();
            System.out.print("Enter password: ");
            String password = scanner.nextLine();
            User user = new User(username, password);
            userService.register(user);
        });
        commands.put("login", () -> {
            System.out.print("Enter username: ");
            String username = scanner.nextLine();
            System.out.print("Enter password: ");
            String password = scanner.nextLine();
            User user = userService.login(username, password);
            if (user.isLoggedIn()) {
                currentUser = user;
            } else {
                System.out.println("The login or password is incorrect. Check the entered data");
            }
        });

        commands.put("room -c", () -> {
            if (currentUser == null) {
                System.out.println("You need to login first.");
                return;
            }
            System.out.print("Enter room name: ");
            String resourceName = scanner.nextLine();
            System.out.print("Enter room type. Choose digit: \n1 - WORKSPACE, 2 - MEETING_ROOM: ");
            int typeInput;
            try {
                typeInput = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                return;
            }

            RoomType roomType;
            switch (typeInput) {
                case 1 -> roomType = RoomType.WORKSPACE;
                case 2 -> roomType = RoomType.MEETING_ROOM;
                default -> {
                    System.out.println("Invalid input. Please enter 1 or 2.");
                    return;
                }
            }


            Room room = new Room(resourceName, roomType);
            roomService.createRoom(currentUser, room);
        });
        commands.put("room -r", () -> {
            List<Room> roomList = roomService.getRooms();
            if (roomList.isEmpty()) {
                System.out.println("No rooms available.");
            } else {
                roomList.forEach(room -> System.out.println("Room: " + room.getName() + ", Type: " + room.getType().getType()));
            }
        });
        commands.put("room -u", () -> {
            if (currentUser == null) {
                System.out.println("You need to login first.");
                return;
            }
            System.out.print("Enter room name to update: ");
            String roomName = scanner.nextLine();
            System.out.print("Enter new room name: ");
            String newRoomName = scanner.nextLine();

            System.out.print("Enter room type. Choose digit: \n1 - WORKSPACE, 2 - MEETING_ROOM: ");
            int typeInput;
            try {
                typeInput = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                return;
            }
            RoomType roomType;
            switch (typeInput) {
                case 1 -> roomType = RoomType.WORKSPACE;
                case 2 -> roomType = RoomType.MEETING_ROOM;
                default -> {
                    System.out.println("Invalid input. Please enter 1 or 2.");
                    return;
                }
            }

            Room updatedRoom = new Room(newRoomName, roomType);
            roomService.updateRoom(currentUser, roomName, updatedRoom);
        });
        commands.put("room -d", () -> {
            if (currentUser == null) {
                System.out.println("You need to login first.");
                return;
            }
            System.out.print("Enter room name to delete: ");
            String roomName = scanner.nextLine();
            roomService.deleteRoom(currentUser, roomName);
        });

        commands.put("booking -c", () -> {
            if (currentUser == null) {
                System.out.println("You need to login first.");
                return;
            }
            System.out.print("Enter room name: ");
            String roomName = scanner.nextLine();
            System.out.print("Enter start time (yyyy-MM-dd'T'HH): ");
            String startTime = scanner.nextLine();
            System.out.print("Enter end time (yyyy-MM-dd'T'HH): ");
            String endTime = scanner.nextLine();

            Optional<Room> room = rooms.stream().filter(r -> r.getName().equals(roomName)).findFirst();
            if (room.isPresent()) {
                bookingService.createBooking(currentUser, room.get(), startTime, endTime);
            } else {
                System.out.println("Room not found.");
            }
        });
        commands.put("slots -r", () -> {
            System.out.print("Enter date (yyyy-MM-dd): ");
            String dateInput = scanner.nextLine();
            LocalDate date = LocalDate.parse(dateInput);

            System.out.print("Enter room name: ");
            String roomName = scanner.nextLine();

            Optional<Room> room = rooms.stream().filter(r -> r.getName().equals(roomName)).findFirst();
            if (room.isPresent()) {
                List<LocalTime> availableHours = bookingService.getAvailableHours(date, room.get());
                System.out.println("Available hours on " + date + " for room " + roomName + ":");
                availableHours.forEach(hour -> System.out.println(hour));
            } else {
                System.out.println("Room not found.");
            }
        });
        commands.put("booking -u", () -> {
            if (currentUser == null) {
                System.out.println("You need to login first.");
                return;
            }
            System.out.print("Enter original start time (yyyy-MM-dd'T'HH): ");
            String originalStartTime = scanner.nextLine();
            System.out.print("Enter new start time (yyyy-MM-dd'T'HH): ");
            String newStartTime = scanner.nextLine();
            System.out.print("Enter new end time (yyyy-MM-dd'T'HH): ");
            String newEndTime = scanner.nextLine();
            System.out.print("Enter new room name: ");
            String newRoomName = scanner.nextLine();

            Optional<Room> newRoom = rooms.stream().filter(r -> r.getName().equals(newRoomName)).findFirst();
            if (newRoom.isPresent()) {
                bookingService.updateBooking(currentUser, originalStartTime, newStartTime, newEndTime, newRoom.get());
            } else {
                System.out.println("Room not found.");
            }
        });
        commands.put("booking -d", () -> {
            if (currentUser == null) {
                System.out.println("You need to login first.");
                return;
            }
            System.out.print("Enter room name: ");
            String roomName = scanner.nextLine();
            System.out.print("Enter start time of the booking to delete (yyyy-MM-dd'T'HH): ");
            String startTimeInput = scanner.nextLine();
            LocalDateTime startTime = LocalDateTime.parse(startTimeInput, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH"));

            Optional<Room> room = rooms.stream().filter(r -> r.getName().equals(roomName)).findFirst();
            if (room.isPresent()) {
                bookingService.deleteBooking(currentUser, room.get(), startTime);
            } else {
                System.out.println("Room not found.");
            }
        });
        commands.put("booking -f", () -> {
            System.out.print("Filters: \n-Date\n-User\n-Room");
            System.out.print("\nEnter filter:");
            String filterType = scanner.nextLine().toLowerCase();

            switch (filterType) {
                case "date":
                    System.out.print("Enter date (yyyy-MM-dd'T'HH): ");
                    String dateInput = scanner.nextLine();
                    LocalDateTime date = LocalDateTime.parse(dateInput, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH"));
                    List<Booking> filteredByDate = bookingService.filterBookings(date, null, null);
                    System.out.println("Bookings on " + dateInput + ":");
                    filteredByDate.forEach(System.out::println);
                    break;

                case "user":
                    System.out.print("Enter username: ");
                    String username = scanner.nextLine();
                    Optional<User> user = userService.getUserByUsername(username);
                    if (user.isPresent()) {
                        List<Booking> filteredByUser = bookingService.filterBookings(null, user.get(), null);
                        System.out.println("Bookings for user " + username + ":");
                        filteredByUser.forEach(System.out::println);
                    } else {
                        System.out.println("User not found.");
                    }
                    break;

                case "room":
                    System.out.print("Enter room name: ");
                    String roomName = scanner.nextLine();
                    Optional<Room> room = rooms.stream().filter(r -> r.getName().equals(roomName)).findFirst();
                    if (room.isPresent()) {
                        List<Booking> filteredByRoom = bookingService.filterBookings(null, null, room.get());
                        System.out.println("Bookings for room " + roomName + ":");
                        filteredByRoom.forEach(System.out::println);
                    } else {
                        System.out.println("Room not found.");
                    }
                    break;

                default:
                    System.out.println("Invalid filter type. Please enter 'date', 'user', or 'room'.");
                    break;
            }
        });

        commands.put("exit", () -> System.exit(0));

        while (true) {
            System.out.println(ConstantValues.GREEN + "Use command \"info\" to view all commands" + ConstantValues.RESET);
            System.out.print("\nEnter command: ");
            String command = scanner.nextLine();
            Runnable action = commands.get(command);
            if (action != null) {
                action.run();
            } else {
                System.out.println("Unknown command: " + command);
            }

        }
    }
    static void uploadInitData(UserService userService, RoomService roomService, BookingService bookingService) {
        System.out.println(ConstantValues.GREEN + "Uploading initial data for demonstration of functionality" + ConstantValues.RESET);

        userService.register(admin);
        roomService.createRoom(admin, room1);
        roomService.createRoom(admin, room2);
        roomService.createRoom(admin, room3);

        bookingService.createBooking(admin, room1, "2023-06-18T15", "2023-06-18T18");
        bookingService.createBooking(admin, room1, "2023-06-17T19", "2023-06-17T20");

        System.out.println(ConstantValues.GREEN + "Upload complete" + ConstantValues.RESET);
    }
}

