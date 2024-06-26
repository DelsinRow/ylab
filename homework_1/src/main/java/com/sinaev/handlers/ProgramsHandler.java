package com.sinaev.handlers;

import com.sinaev.ConstantValues;
import com.sinaev.models.Booking;
import com.sinaev.models.Room;
import com.sinaev.models.RoomType;
import com.sinaev.models.User;
import com.sinaev.repositories.BookingRepository;
import com.sinaev.repositories.RoomRepository;
import com.sinaev.repositories.UserRepository;
import com.sinaev.services.BookingService;
import com.sinaev.services.RoomService;
import com.sinaev.services.UserService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
/**
 * This class handles the overall program flow, including user authentication, room management, and booking management.
 * It initializes the required repositories and services and provides a set of commands for interaction.
 */
public class ProgramsHandler {
    User currentUser;
    UserRepository userRepository = new UserRepository();
    RoomRepository roomRepository = new RoomRepository();
    BookingRepository bookingRepository = new BookingRepository();

    UserService userService = new UserService(userRepository);
    RoomService roomService = new RoomService(roomRepository);
    BookingService bookingService = new BookingService(bookingRepository);
    Scanner scanner = new Scanner(System.in);

    /**
     * Initializes the commands map with various commands related to users, rooms, bookings, and others.
     *
     * @return a map containing all available commands and their associated actions.
     */
    public Map<String, Runnable> commands (){
        Map<String, Runnable> commands = new HashMap<>();
        addUserCommands(commands);
        addRoomCommands(commands);
        addBookingCommands(commands);
        addOtherCommands(commands);

        return commands;
    }

    /**
     * Uploads initial data for demonstration purposes. Registers an admin user and creates some initial rooms and bookings.
     */
    public void uploadInitData() {
        User admin = new User("admin", "admin", true);
        Room room1 = new Room("first", RoomType.MEETING_ROOM);
        Room room2 = new Room("second", RoomType.WORKSPACE);
        Room room3 = new Room("third", RoomType.MEETING_ROOM);

        System.out.println(ConstantValues.GREEN + "Uploading initial data for demonstration of functionality" + ConstantValues.RESET);

        userService.register(admin);
        roomService.createRoom(admin, room1);
        roomService.createRoom(admin, room2);
        roomService.createRoom(admin, room3);

        bookingService.createBooking(admin, room1, "2023-06-18T15", "2023-06-18T18");
        bookingService.createBooking(admin, room1, "2023-06-17T19", "2023-06-17T20");

        System.out.println(ConstantValues.GREEN + "Upload complete" + ConstantValues.RESET);
    }

    /**
     * Starts the main program loop, allowing users to enter commands to interact with the system.
     *
     * @param commands the map of available commands.
     */
    public void startProgram(Map<String, Runnable> commands) {
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

    /**
     * Adds user-related commands to the commands map.
     *
     * @param commands the map of available commands.
     */
    private void addUserCommands(Map<String, Runnable> commands){
        addCommandRegister(commands);
        addCommandLogin(commands);
    }

    /**
     * Adds room-related commands to the commands map.
     *
     * @param commands the map of available commands.
     */
    private void addRoomCommands(Map<String, Runnable> commands) {
        addCommandRoomCreate(commands);
        addCommandRoomRead(commands);
        addCommandRoomUpdate(commands);
        addCommandRoomDelete(commands);
    }

    /**
     * Adds booking-related commands to the commands map.
     *
     * @param commands the map of available commands.
     */
    private void addBookingCommands(Map<String, Runnable> commands){
        addCommandBookingCreate(commands);
        addCommandSlotsRead(commands);
        addCommandBookingUpdate(commands);
        addCommandBookingDelete(commands);
        addCommandBookingFilter(commands);
    }

    /**
     * Adds other miscellaneous commands to the commands map.
     *
     * @param commands the map of available commands.
     */
    private void addOtherCommands(Map<String, Runnable> commands){
        addCommandInfo(commands);
        addCommandExit(commands);
    }

    /**
     * Adds the "info" command to the provided commands map. The command prints out information about all available commands.
     *
     * @param commands the map to which the "info" command is added
     */
    private void addCommandInfo(Map<String, Runnable> commands) {
        commands.put("info", () -> {
            System.out.println(ConstantValues.INFO);
        });
    }

    /**
     * Adds the "register" command to the provided commands map. The command prompts the user to enter a username and password,
     * then registers a new user with the provided credentials.
     *
     * @param commands the map to which the "register" command is added
     */
    private void addCommandRegister(Map<String, Runnable> commands) {
        commands.put("register", () -> {
            System.out.print("Enter username: ");
            String username = scanner.nextLine();
            System.out.print("Enter password: ");
            String password = scanner.nextLine();
            User user = new User(username, password);
            userService.register(user);
        });
    }

    /**
     * Adds the "login" command to the provided commands map. The command prompts the user to enter a username and password,
     * then attempts to log in with the provided credentials.
     *
     * @param commands the map to which the "login" command is added
     */
    private void addCommandLogin(Map<String, Runnable> commands) {
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
    }

    /**
     * Adds the "room -c" command to the provided commands map. The command prompts the user to enter a room name and type,
     * then creates a new room with the provided details.
     *
     * @param commands the map to which the "room -c" command is added
     */
    private void addCommandRoomCreate(Map<String, Runnable> commands) {
        commands.put("room -c", () -> {
            if (!isUserLoggedIn()) {
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
    }

    /**
     * Adds the "room -r" command to the provided commands map. The command retrieves and displays the list of available rooms.
     *
     * @param commands the map to which the "room -r" command is added
     */
    private void addCommandRoomRead(Map<String, Runnable> commands) {
        commands.put("room -r", () -> {
            List<Room> roomList = roomService.getRooms();
            if (roomList.isEmpty()) {
                System.out.println("No rooms available.");
            } else {
                roomList.forEach(room -> System.out.println("Room: " + room.getName() + ", Type: " + room.getType().getType()));
            }
        });
    }

    /**
     * Adds the "room -u" command to the provided commands map. The command prompts the user to enter the name of the room to be updated,
     * the new room name, and the new room type, then updates the room with the provided details.
     *
     * @param commands the map to which the "room -u" command is added
     */
    private void addCommandRoomUpdate(Map<String, Runnable> commands) {
        commands.put("room -u", () -> {
            if (!isUserLoggedIn()) {
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
    }

    /**
     * Adds the "room -d" command to the provided commands map. The command prompts the user to enter the name of the room to be deleted,
     * then deletes the specified room.
     *
     * @param commands the map to which the "room -d" command is added
     */
    private void addCommandRoomDelete(Map<String, Runnable> commands) {
        commands.put("room -d", () -> {
            if (!isUserLoggedIn()) {
                System.out.println("You need to login first.");
                return;
            }
            System.out.print("Enter room name to delete: ");
            String roomName = scanner.nextLine();
            roomService.deleteRoom(currentUser, roomName);
        });
    }

    /**
     * Adds the "booking -c" command to the provided commands map. The command prompts the user to enter a room name,
     * start time, and end time, then creates a new booking with the provided details.
     *
     * @param commands the map to which the "booking -c" command is added
     */
    private void addCommandBookingCreate(Map<String, Runnable> commands) {
        commands.put("booking -c", () -> {
            if (!isUserLoggedIn()) {
                System.out.println("You need to login first.");
                return;
            }
            System.out.print("Enter room name: ");
            String roomName = scanner.nextLine();
            System.out.print("Enter start time (yyyy-MM-dd'T'HH): ");
            String startTime = scanner.nextLine();
            System.out.print("Enter end time (yyyy-MM-dd'T'HH): ");
            String endTime = scanner.nextLine();

            Optional<Room> room = roomRepository.findByName(roomName);
            if (room.isPresent()) {
                bookingService.createBooking(currentUser, room.get(), startTime, endTime);
            } else {
                System.out.println("Room not found.");
            }
        });
    }

    /**
     * Adds the "slots -r" command to the provided commands map. The command prompts the user to enter a date and room name,
     * then retrieves and displays the available booking hours for the specified room on the given date.
     *
     * @param commands the map to which the "slots -r" command is added
     */
    private void addCommandSlotsRead(Map<String, Runnable> commands) {
        commands.put("slots -r", () -> {
            System.out.print("Enter date (yyyy-MM-dd): ");
            String dateInput = scanner.nextLine();
            LocalDate date = LocalDate.parse(dateInput);

            System.out.print("Enter room name: ");
            String roomName = scanner.nextLine();

            Optional<Room> room = roomRepository.findByName(roomName);
            if (room.isPresent()) {
                List<LocalTime> availableHours = bookingService.getAvailableHours(date, room.get());
                System.out.println("Available hours on " + date + " for room " + roomName + ":");
                availableHours.forEach(hour -> System.out.println(hour));
            } else {
                System.out.println("Room not found.");
            }
        });
    }

    /**
     * Adds the "booking -u" command to the provided commands map. The command prompts the user to enter the room name,
     * original start time, new room name, new start time, and new end time, then updates the booking with the provided details.
     *
     * @param commands the map to which the "booking -u" command is added
     */
    private void addCommandBookingUpdate(Map<String, Runnable> commands) {
        commands.put("booking -u", () -> {
            if (!isUserLoggedIn()) {
                System.out.println("You need to login first.");
                return;
            }
            System.out.print("Enter room name: ");
            String roomName = scanner.nextLine();
            System.out.print("Enter original start time (yyyy-MM-dd'T'HH): ");
            String originalStartTime = scanner.nextLine();
            System.out.print("Enter new room name: ");
            String newRoomName = scanner.nextLine();
            System.out.print("Enter new start time (yyyy-MM-dd'T'HH): ");
            String newStartTime = scanner.nextLine();
            System.out.print("Enter new end time (yyyy-MM-dd'T'HH): ");
            String newEndTime = scanner.nextLine();


            Optional<Room> room = roomRepository.findByName(roomName);
            Optional<Room> newRoom = roomRepository.findByName(newRoomName);
            if (newRoom.isEmpty() || room.isEmpty()) {
                System.out.println("Room not found.");
            } else {
                bookingService.updateBooking(currentUser, room.get(), originalStartTime, newRoom.get(), newStartTime, newEndTime);
            }
        });
    }

    /**
     * Adds the "booking -d" command to the provided commands map. The command prompts the user to enter the room name
     * and start time of the booking to be deleted, then deletes the specified booking.
     *
     * @param commands the map to which the "booking -d" command is added
     */
    private void addCommandBookingDelete(Map<String, Runnable> commands) {
        commands.put("booking -d", () -> {
            if (!isUserLoggedIn()) {
                System.out.println("You need to login first.");
                return;
            }
            System.out.print("Enter room name: ");
            String roomName = scanner.nextLine();
            System.out.print("Enter start time of the booking to delete (yyyy-MM-dd'T'HH): ");
            String startTimeInput = scanner.nextLine();
            LocalDateTime startTime = LocalDateTime.parse(startTimeInput, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH"));

            Optional<Room> room = roomRepository.findByName(roomName);
            if (room.isPresent()) {
                bookingService.deleteBooking(currentUser, room.get(), startTime);
            } else {
                System.out.println("Room not found.");
            }
        });
    }

    /**
     * Adds the "booking -f" command to the provided commands map. The command prompts the user to enter a filter type
     * (date, user, or room), then retrieves and displays the bookings filtered by the specified type.
     *
     * @param commands the map to which the "booking -f" command is added
     */
    private void addCommandBookingFilter(Map<String, Runnable> commands) {
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
                    Optional<Room> room = roomRepository.findByName(roomName);
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
    }

    /**
     * Adds the "exit" command to the provided commands map. The command exits the application.
     *
     * @param commands the map to which the "exit" command is added
     */
    private void addCommandExit(Map<String, Runnable> commands) {
        commands.put("exit", () -> System.exit(0));
    }

    /**
     * Checks if a user is currently logged in.
     *
     * @return true if a user is logged in, false otherwise
     */
    private boolean isUserLoggedIn() {
        return currentUser != null;
    }

}
