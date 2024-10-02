import java.util.*;

// Observer Pattern
interface Observer {
    void update(String message);
}

class User implements Observer {
    private String username;
    
    public User(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public void update(String message) {
        System.out.println(username + " received: " + message);
    }
}

// Singleton Pattern for managing chat rooms
class ChatRoom {
    private static Map<String, ChatRoom> rooms = new HashMap<>();
    private String roomId;
    private List<User> users;
    private List<String> messageHistory;

    private ChatRoom(String roomId) {
        this.roomId = roomId;
        users = new ArrayList<>();
        messageHistory = new ArrayList<>();
    }

    public static ChatRoom getRoom(String roomId) {
        return rooms.computeIfAbsent(roomId, ChatRoom::new);
    }

    public void joinRoom(User user) {
        users.add(user);
        broadcastMessage(user.getUsername() + " has joined the chat.");
        sendHistory(user);
    }

    public void leaveRoom(User user) {
        users.remove(user);
        broadcastMessage(user.getUsername() + " has left the chat.");
    }

    public void broadcastMessage(String message) {
        messageHistory.add(message);
        for (User user : users) {
            user.update(message);
        }
    }

    private void sendHistory(User user) {
        System.out.println("Sending chat history to " + user.getUsername() + "...");
        for (String message : messageHistory) {
            user.update(message);
        }
    }

    public void privateMessage(User fromUser, User toUser, String message) {
        String privateMessage = "(Private) " + fromUser.getUsername() + " to " + toUser.getUsername() + ": " + message;
        fromUser.update(privateMessage);
        toUser.update(privateMessage);
    }

    public List<User> getActiveUsers() {
        return users;
    }
}

// Adapter Pattern for communication protocols
interface CommunicationProtocol {
    void connect();
}

class WebSocketProtocol implements CommunicationProtocol {
    @Override
    public void connect() {
        System.out.println("Connected via WebSocket.");
    }
}

class HTTPProtocol implements CommunicationProtocol {
    @Override
    public void connect() {
        System.out.println("Connected via HTTP.");
    }
}

class CommunicationAdapter {
    private CommunicationProtocol protocol;

    public CommunicationAdapter(CommunicationProtocol protocol) {
        this.protocol = protocol;
    }

    public void connect() {
        protocol.connect();
    }
}

// Main class with dynamic user input
public class DynamicChatApplication {
    private static Map<String, User> allUsers = new HashMap<>();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        // Choose protocol
        CommunicationAdapter adapter = chooseProtocol();
        adapter.connect();

        while (true) {
            System.out.println("\n1. Create User\n2. Create/Join Chat Room\n3. Send Message\n4. Private Message\n5. View Active Users\n6. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    createUser();
                    break;
                case 2:
                    joinChatRoom();
                    break;
                case 3:
                    sendMessage();
                    break;
                case 4:
                    sendPrivateMessage();
                    break;
                case 5:
                    viewActiveUsers();
                    break;
                case 6:
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    // Choosing communication protocol
    private static CommunicationAdapter chooseProtocol() {
        System.out.println("Select communication protocol:");
        System.out.println("1. WebSocket");
        System.out.println("2. HTTP");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        if (choice == 1) {
            return new CommunicationAdapter(new WebSocketProtocol());
        } else {
            return new CommunicationAdapter(new HTTPProtocol());
        }
    }

    // Create a user
    private static void createUser() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        if (allUsers.containsKey(username)) {
            System.out.println("User already exists.");
        } else {
            allUsers.put(username, new User(username));
            System.out.println("User " + username + " created.");
        }
    }

    // Join or create a chat room
    private static void joinChatRoom() {
        System.out.print("Enter your username: ");
        String username = scanner.nextLine();
        User user = allUsers.get(username);
        if (user == null) {
            System.out.println("User does not exist. Create the user first.");
            return;
        }

        System.out.print("Enter Chat Room ID: ");
        String roomId = scanner.nextLine();
        ChatRoom chatRoom = ChatRoom.getRoom(roomId);
        chatRoom.joinRoom(user);
    }

    // Send a message in the chat room
    private static void sendMessage() {
        System.out.print("Enter your username: ");
        String username = scanner.nextLine();
        User user = allUsers.get(username);
        if (user == null) {
            System.out.println("User does not exist. Create the user first.");
            return;
        }

        System.out.print("Enter Chat Room ID: ");
        String roomId = scanner.nextLine();
        ChatRoom chatRoom = ChatRoom.getRoom(roomId);

        System.out.print("Enter your message: ");
        String message = scanner.nextLine();
        chatRoom.broadcastMessage(user.getUsername() + ": " + message);
    }

    // Send a private message to another user
    private static void sendPrivateMessage() {
        System.out.print("Enter your username: ");
        String fromUsername = scanner.nextLine();
        User fromUser = allUsers.get(fromUsername);
        if (fromUser == null) {
            System.out.println("User does not exist. Create the user first.");
            return;
        }

        System.out.print("Enter recipient's username: ");
        String toUsername = scanner.nextLine();
        User toUser = allUsers.get(toUsername);
        if (toUser == null) {
            System.out.println("Recipient does not exist.");
            return;
        }

        System.out.print("Enter your private message: ");
        String message = scanner.nextLine();
        ChatRoom chatRoom = ChatRoom.getRoom("Private"); // Simulate private chat room
        chatRoom.privateMessage(fromUser, toUser, message);
    }

    // View active users in a chat room
    private static void viewActiveUsers() {
        System.out.print("Enter Chat Room ID: ");
        String roomId = scanner.nextLine();
        ChatRoom chatRoom = ChatRoom.getRoom(roomId);

        List<User> activeUsers = chatRoom.getActiveUsers();
        if (activeUsers.isEmpty()) {
            System.out.println("No active users.");
        } else {
            System.out.println("Active Users in Room " + roomId + ":");
            for (User user : activeUsers) {
                System.out.println(user.getUsername());
            }
        }
    }
}
