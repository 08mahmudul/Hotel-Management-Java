# Hotel Management System — Project Overview

A GUI-based Hotel Management System built in Java using Swing. It lets staff manage rooms,
register guests, create bookings, and cancel bookings — all data is saved to CSV files so
nothing is lost when the application restarts.

---

## Table of Contents
1. [How to Run](#how-to-run)
2. [Architecture](#architecture)
3. [Package Structure](#package-structure)
4. [OOP Concepts](#oop-concepts)
5. [Class-by-Class Reference](#class-by-class-reference)
6. [Data Flow: End-to-End Example](#data-flow-end-to-end-example)
7. [File I/O](#file-io)
8. [Exception Handling](#exception-handling)
9. [GUI Layout](#gui-layout)
10. [Common Professor Questions](#common-professor-questions)

---

## How to Run

```bash
# Compile (from project root)
javac -cp ".:lib/*" -d bin $(find src -name "*.java")

# Run
java -cp "bin:lib/*" com.hotel.Main
```

The `data/` folder is created automatically on first launch. Three CSV files are created if they
do not already exist: `rooms.csv`, `guests.csv`, `bookings.csv`.

---

## Architecture

The project follows a **3-layer architecture**:

```
┌─────────────────────────────────────────────┐
│              UI Layer (Swing)               │  ← What the user sees and clicks
│   MainFrame, RoomPanel, GuestPanel,         │
│   BookingPanel, DashboardPanel              │
└────────────────────┬────────────────────────┘
                     │ calls
┌────────────────────▼────────────────────────┐
│            Service Layer                    │  ← Business rules, validation
│   RoomService, GuestService, BookingService │
└────────────────────┬────────────────────────┘
                     │ calls
┌────────────────────▼────────────────────────┐
│          Repository Layer                   │  ← Read / write CSV files
│   RoomRepository, GuestRepository,          │
│   BookingRepository                         │
└────────────────────┬────────────────────────┘
                     │ reads/writes
┌────────────────────▼────────────────────────┐
│           CSV Files (data/)                 │  ← Persistent storage
│   rooms.csv   guests.csv   bookings.csv     │
└─────────────────────────────────────────────┘
```

Each layer only talks to the layer directly below it. The UI never touches a file; the
repository never validates business rules.

---

## Package Structure

```
src/
└── com/hotel/
    ├── Main.java                    ← Entry point
    ├── model/                       ← Entity classes (data holders)
    │   ├── BaseEntity.java
    │   ├── Room.java
    │   ├── StandardRoom.java
    │   ├── DeluxeRoom.java
    │   ├── Suite.java
    │   ├── Guest.java
    │   └── Booking.java
    ├── service/                     ← Business logic
    │   ├── Manageable.java          ← CRUD interface
    │   ├── RoomService.java
    │   ├── GuestService.java
    │   └── BookingService.java
    ├── repository/                  ← File access (CSV)
    │   ├── RoomRepository.java
    │   ├── GuestRepository.java
    │   └── BookingRepository.java
    ├── exception/                   ← Custom exceptions
    │   ├── HotelException.java
    │   ├── InvalidInputException.java
    │   ├── DuplicateRecordException.java
    │   ├── RecordNotFoundException.java
    │   ├── RoomNotAvailableException.java
    │   ├── FileReadException.java
    │   └── FileWriteException.java
    ├── util/                        ← Helper utilities
    │   ├── FileHandler.java
    │   ├── IDGenerator.java
    │   └── InputValidator.java
    └── ui/                          ← Swing GUI
        ├── MainFrame.java
        ├── PlaceholderTextField.java
        └── panels/
            ├── DashboardPanel.java
            ├── RoomPanel.java
            ├── GuestPanel.java
            └── BookingPanel.java
```

---

## OOP Concepts

### 1. Encapsulation

Every field in every model class is `private`. Outside code must use getters and setters.

```java
// BaseEntity.java
public abstract class BaseEntity {
    private String id;           // ← private — not directly accessible
    private String createdAt;

    public String getId() { return id; }          // ← controlled read
    public void setId(String id) { this.id = id; } // ← controlled write
}
```

The same pattern is followed in `Room`, `Guest`, and `Booking`. This protects internal data
from being accidentally corrupted by outside code.

---

### 2. Inheritance

The project has a two-level inheritance tree:

```
BaseEntity   (abstract)
├── Room     (abstract)
│   ├── StandardRoom
│   ├── DeluxeRoom
│   └── Suite
├── Guest
└── Booking
```

- `BaseEntity` holds fields shared by every entity: `id` and `createdAt`.
- `Room` extends `BaseEntity` and adds `roomNumber`, `roomType`, `pricePerNight`, `isAvailable`.
- `StandardRoom`, `DeluxeRoom`, and `Suite` each extend `Room` and add their own unique fields.

```java
// Room.java
public abstract class Room extends BaseEntity {   // inherits id, createdAt
    private String roomNumber;
    private double pricePerNight;
    ...
}

// Suite.java
public class Suite extends Room {                 // inherits everything above + adds:
    private int floorNumber;
    private boolean hasJacuzzi;
    private int maxOccupancy;
    ...
}
```

---

### 3. Abstraction

**Abstract class:** `BaseEntity` declares `display()` as abstract — it defines *what* must exist
but leaves *how* to the subclasses.

```java
// BaseEntity.java
public abstract class BaseEntity {
    public abstract void display();   // no body — subclasses must provide it
}
```

**Abstract class:** `Room` is also abstract, so you can never do `new Room()`. You must use a
concrete subtype (`StandardRoom`, `DeluxeRoom`, or `Suite`).

**Interface:** `Manageable<T>` defines the CRUD contract that all three services must fulfill.

```java
// Manageable.java
public interface Manageable<T> {
    void add(T entity) throws HotelException;
    T    getById(String id) throws HotelException;
    List<T> getAll();
    void update(T entity) throws HotelException;
    void delete(String id) throws HotelException;
}

// RoomService.java
public class RoomService implements Manageable<Room> { ... }

// GuestService.java
public class GuestService implements Manageable<Guest> { ... }

// BookingService.java
public class BookingService implements Manageable<Booking> { ... }
```

---

### 4. Polymorphism

**Method overriding (runtime polymorphism):** Every concrete class overrides `display()` from
`BaseEntity`. The correct version is selected at runtime based on the actual object type.

```java
// StandardRoom.java
@Override
public void display() {
    System.out.println("Standard Room | No: " + getRoomNumber()
            + " | Amenities: " + amenities + " | Available: " + isAvailable());
}

// Suite.java
@Override
public void display() {
    System.out.println("Suite | No: " + getRoomNumber()
            + " | Floor: " + floorNumber + " | Jacuzzi: " + hasJacuzzi + " ...");
}
```

**Polymorphic collection:** `RoomService.getAll()` returns `List<Room>`. The list can hold
`StandardRoom`, `DeluxeRoom`, and `Suite` objects at the same time. When the repository reads
a CSV row it calls `buildRoom()` which returns the correct subtype — the rest of the code works
with the `Room` reference without needing to know the concrete type:

```java
// RoomRepository.java — factory method deciding the subtype at runtime
private Room buildRoom(String[] cols) {
    switch (cols[2]) {           // cols[2] = roomType
        case "STANDARD": return new StandardRoom(...);
        case "DELUXE":   return new DeluxeRoom(...);
        case "SUITE":    return new Suite(...);
    }
}
```

---

## Class-by-Class Reference

### Model Classes (`com.hotel.model`)

| Class | Type | Extends | Key Fields |
|-------|------|---------|------------|
| `BaseEntity` | Abstract | — | `id`, `createdAt` |
| `Room` | Abstract | `BaseEntity` | `roomNumber`, `roomType`, `pricePerNight`, `isAvailable` |
| `StandardRoom` | Concrete | `Room` | `amenities` (e.g. "Fan, TV, WiFi") |
| `DeluxeRoom` | Concrete | `Room` | `hasAC`, `hasMinibar` |
| `Suite` | Concrete | `Room` | `floorNumber`, `hasJacuzzi`, `maxOccupancy` |
| `Guest` | Concrete | `BaseEntity` | `fullName`, `phone`, `nidOrPassport`, `address` |
| `Booking` | Concrete | `BaseEntity` | `guestId`, `roomId`, `checkInDate`, `checkOutDate`, `totalBill`, `status` |

### Service Classes (`com.hotel.service`)

Each service implements `Manageable<T>` and enforces business rules before delegating to a repository.

| Service | Validates |
|---------|-----------|
| `RoomService` | Room number not empty, price > 0, no duplicate room numbers. Blocks delete if room has an active booking. |
| `GuestService` | Name letters-only ≥ 3 chars, phone 11 digits starting with "01", NID required, no duplicate NID. Blocks delete if guest has an active booking. |
| `BookingService` | Dates are valid, check-in not in the past, check-out after check-in, room is available. Computes `totalBill = nights × pricePerNight`. Sets room unavailable on booking, available again on cancel. |

### Repository Classes (`com.hotel.repository`)

Handle all file reading and writing. Each repository knows:
- The CSV file path
- The CSV header row
- How to convert a CSV row → object (`findAll`, `findById`)
- How to convert an object → CSV row (`save`, `update`)

### Utility Classes (`com.hotel.util`)

| Class | Purpose |
|-------|---------|
| `FileHandler` | `readAll()` — reads all data rows, skipping the header. `writeAll()` — overwrites the file with a new list. `ensureFileExists()` — creates the file with header if it does not exist. |
| `IDGenerator` | Reads the last ID in a CSV file and increments it. Produces IDs like `R-001`, `G-002`, `B-003`. Zero-padded to 3 digits. |
| `InputValidator` | Static helper methods: `isNotEmpty`, `isValidPhone`, `isValidDate`, `isDateAfter`, `isTodayOrFuture`. |

---

## Data Flow: End-to-End Example

**Scenario: User confirms a new booking.**

```
1. User fills the Booking form and clicks "Confirm Booking"
       ↓
2. BookingPanel.handleConfirm()
   — reads guestId from guestCombo, roomId from roomCombo,
     checkInDate and checkOutDate from text fields
   — creates a new Booking object
       ↓
3. BookingService.add(booking)
   — validates dates (format, not in past, checkout after checkin)
   — calls RoomService.getById(roomId) to fetch the Room
   — checks room.isAvailable() — throws RoomNotAvailableException if false
   — calculates nights = ChronoUnit.DAYS.between(checkIn, checkOut)
   — sets totalBill = nights × room.getPricePerNight()
   — sets status = "ACTIVE"
   — calls IDGenerator.generate("B", "data/bookings.csv") → "B-004"
   — calls BookingRepository.save(booking)  → appends row to bookings.csv
   — calls RoomService.setAvailability(roomId, false) → updates rooms.csv
       ↓
4. BookingPanel refreshes the table and the room dropdown
   — the booked room disappears from the "available rooms" dropdown
```

**Scenario: User cancels an existing booking.**

```
1. User clicks "Cancel" button on a row in the Booking table
       ↓
2. BookingPanel.handleCancel(row)
   — reads bookingId from the table model
       ↓
3. BookingService.cancelBooking(bookingId)
   — fetches the booking, checks it is not already CANCELLED
   — sets status = "CANCELLED", updates bookings.csv
   — calls RoomService.setAvailability(roomId, true) → updates rooms.csv
       ↓
4. The room re-appears in the available rooms dropdown
```

---

## File I/O

Data is stored in three plain-text CSV files inside the `data/` folder.

### CSV Format

**rooms.csv**
```
roomId,roomNumber,roomType,pricePerNight,isAvailable,extraData,createdAt
R-001,101,STANDARD,1500.0,true,Fan|TV|Attached Bathroom,2024-01-01
R-005,201,DELUXE,3500.0,true,AC=true|Minibar=true,2024-01-01
R-008,301,SUITE,8000.0,true,Floor=3|Jacuzzi=true|MaxOcc=4,2024-01-01
```

The `extraData` column stores type-specific fields using `|` as a separator (to avoid conflict
with the CSV comma separator):
- STANDARD: amenity names joined with `|`
- DELUXE: `AC=true|Minibar=false`
- SUITE: `Floor=3|Jacuzzi=true|MaxOcc=4`

**guests.csv**
```
guestId,fullName,phone,nidOrPassport,address,createdAt
G-001,Rahim Uddin,01711234567,1234567890123,Dhaka Bangladesh,2024-01-05
```

**bookings.csv**
```
bookingId,guestId,roomId,checkInDate,checkOutDate,totalBill,status,createdAt
B-001,G-001,R-003,2026-05-10,2026-05-15,9000.0,ACTIVE,2026-05-09
```

### How Reading Works (`FileHandler.readAll`)

```java
BufferedReader reader = new BufferedReader(new FileReader(filePath));
reader.readLine();         // skip the header line
while ((line = reader.readLine()) != null) {
    rows.add(line.split(","));   // split each line into a String array
}
```

### How Writing Works (`FileHandler.writeAll`)

Overwrites the entire file — writes the header first, then every row:
```java
BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
writer.write(header);
for (String row : rows) { writer.write(row); }
```

For **appending** a new record (insert), the repositories open the file in append mode:
```java
new FileWriter(FILE_PATH, true)   // true = append
```

---

## Exception Handling

A custom exception hierarchy means every error carries a meaningful message to the UI.

```
RuntimeException
└── HotelException                 ← base for all application errors
    ├── InvalidInputException      ← bad user input (empty field, wrong format)
    ├── DuplicateRecordException   ← NID or room number already exists
    ├── RecordNotFoundException    ← getById found nothing
    ├── RoomNotAvailableException  ← booking a room that is already booked
    ├── FileReadException          ← CSV could not be opened for reading
    └── FileWriteException         ← CSV could not be written
```

All exceptions extend `RuntimeException`, so they do not need to be declared with `throws` at
every level — but the service methods declare `throws HotelException` in their signatures to
make it explicit.

**How exceptions reach the UI:**

```java
// BookingService.java — throws if room not free
if (!room.isAvailable())
    throw new RoomNotAvailableException("Room is not available");

// BookingPanel.java — catches and shows red message to user
try {
    bookingService.add(b);
    setStatus("Booking confirmed!", false);      // green
} catch (HotelException ex) {
    setStatus(ex.getMessage(), true);            // red
}
```

The UI only needs one `catch (HotelException)` block because all custom exceptions are subtypes
of it — a good demonstration of polymorphism applied to exception handling.

---

## GUI Layout

```
┌──────────────────────────────────────────────────────────┐
│  HEADER — "Hotel Management System"         2026-05-15   │  (dark blue bar)
├────────────┬─────────────────────────────────────────────┤
│            │                                             │
│ SIDEBAR    │            CENTER (CardLayout)              │
│            │                                             │
│ Dashboard  │  One panel is visible at a time:           │
│ Room Mgmt  │  • DashboardPanel — 4 stat cards           │
│ Guest Mgmt │  • RoomPanel      — add/edit rooms + table │
│ Booking    │  • GuestPanel     — register guests + table│
│ Management │  • BookingPanel   — book rooms + table     │
│            │                                             │
├────────────┴─────────────────────────────────────────────┤
│  STATUS BAR — "Ready"                                    │
└──────────────────────────────────────────────────────────┘
```

- **`MainFrame`** extends `JFrame` and holds everything. Window size: 1100 × 700 px, centered.
- **CardLayout** in the center swaps panels when a sidebar button is clicked.
- Each management panel has two sections: a **form** (top) for adding/editing records and a
  **table** (bottom, inside a `JScrollPane`) showing all records.
- Table action buttons (Edit, Delete, Cancel) are rendered using a custom
  `TableCellRenderer` that paints a `JButton` inside each cell. Clicks are detected via a
  `MouseListener` that maps the click column to the correct action.

---

## Common Professor Questions

**Q: What is the role of `BaseEntity`?**
It is an abstract superclass that centralises fields shared by all entities (`id`, `createdAt`)
so we do not repeat them in every class. It also declares the abstract method `display()`,
forcing every entity to implement its own print representation.

**Q: Why is `Room` abstract if it already extends an abstract class?**
`Room` adds fields shared by all room types (number, price, availability) but there is no such
thing as a "generic room" in this system — every room must be Standard, Deluxe, or Suite.
Making `Room` abstract prevents accidentally creating an incomplete room object.

**Q: How is polymorphism shown in the repository?**
`RoomRepository.buildRoom()` returns a `Room` reference. At runtime the actual object is a
`StandardRoom`, `DeluxeRoom`, or `Suite` depending on the `roomType` column in the CSV. The
rest of the application works with the `Room` reference — that is runtime polymorphism.

**Q: Why use a `Manageable<T>` interface?**
It enforces a consistent CRUD contract across all three services. Any code that only needs
basic CRUD can depend on `Manageable<T>` instead of a concrete service class, making the
design loosely coupled and easier to extend.

**Q: Where does validation happen and why not in the UI?**
All business validation is in the service layer (`RoomService`, `GuestService`,
`BookingService`). The UI only displays the error message it receives from the exception.
This means the rules are in one place — if validation needs to change you edit the service,
not every form in the GUI.

**Q: How does the system keep data after the program closes?**
Every add / update / delete immediately writes to a CSV file via the repository layer.
`FileHandler.readAll()` loads data from the file on every `getAll()` call, so the in-memory
state always reflects what is on disk.

**Q: How are IDs generated?**
`IDGenerator.generate(prefix, filePath)` opens the CSV, reads the last ID in the file,
parses its numeric part, and returns `prefix + "-" + (number + 1)` zero-padded to 3 digits.
Example: last ID is `R-005` → new ID is `R-006`.

**Q: What happens if you try to delete a guest who has an active booking?**
`GuestService.delete()` calls `bookingRepository.hasActiveBookingForGuest(id)` first. If the
result is `true` it throws `HotelException("Cannot delete: guest has an active booking")` and
the UI shows that message in red. The record is not deleted.

**Q: How does cancelling a booking free up the room?**
`BookingService.cancelBooking()` sets the booking status to `"CANCELLED"`, saves it, then
calls `RoomService.setAvailability(roomId, true)`, which fetches the room and sets its
`isAvailable` flag to `true` before saving it back to the CSV. The room then reappears in
the available-rooms dropdown immediately.

**Q: What Look and Feel does the application use and why?**
Java's Cross-Platform (Metal) Look and Feel is set in `Main.java` before the window opens.
This is necessary because macOS's default Aqua Look and Feel ignores custom button colors,
making all buttons invisible. Metal renders `setBackground()` and `setForeground()` correctly
on all operating systems.
