# TASKLIST.md
### Hotel Management System — Implementation Guide
> Project setup is already done. Start from Phase 1 below.

---

## Legend

| Symbol | Meaning |
|---|---|
| `[ ]` | Task not started |
| `[x]` | Task complete — mark when done |
| `⚠` | Important constraint or note |
| `→ Test:` | Manual test to run after completing the task |

---

## Overview — Phases

| Phase | What you build | Depends on |
|---|---|---|
| 1 | Models, Interface, Exceptions, IDGenerator | Nothing — start here |
| 2 | File I/O, Repositories | Phase 1 |
| 3 | Service layer, Business logic | Phase 2 |
| 4 | Swing GUI panels | Phase 3 |
| 5 | InputValidator utility | Phase 1 |
| 6 | Entry point, Testing, Polish | All phases |

---

## Phase 1 — Foundation (Models + Utilities)

Build the core data structure. No UI, no file I/O yet. Just classes, fields, and basic logic.

---

### Task 1.1 — BaseEntity

**File:** `com/hotel/model/BaseEntity.java`

- [x] Create `abstract class BaseEntity`
- [x] Add `private String id`
- [x] Add `private String createdAt`
- [x] Add public getter and setter for both fields
- [x] Declare `public abstract void display()`

> ⚠ All model classes will extend this. Keep it minimal — only shared fields go here.

---

### Task 1.2 — Room Hierarchy

**Files:** `Room.java`, `StandardRoom.java`, `DeluxeRoom.java`, `Suite.java`

**Room (abstract parent):**
- [x] Create `abstract class Room extends BaseEntity`
- [x] Add fields: `roomNumber` (String), `roomType` (String), `pricePerNight` (double), `isAvailable` (boolean)
- [x] Add public getters and setters for all fields

**StandardRoom:**
- [x] Create `class StandardRoom extends Room`
- [x] Add field: `amenities` (String) — e.g. `"Fan, TV, Bathroom"`
- [x] Add getter and setter for `amenities`
- [x] Override `display()` — print room number, type, price, amenities

**DeluxeRoom:**
- [x] Create `class DeluxeRoom extends Room`
- [x] Add fields: `hasAC` (boolean), `hasMinibar` (boolean)
- [x] Add getters and setters
- [x] Override `display()` — print room number, type, price, AC and minibar status

**Suite:**
- [x] Create `class Suite extends Room`
- [x] Add fields: `floorNumber` (int), `hasJacuzzi` (boolean), `maxOccupancy` (int)
- [x] Add getters and setters
- [x] Override `display()` — print room number, floor, jacuzzi, max occupancy, price

→ **Test:** Create one object of each type in `main()`, call `display()`, verify the output is correct for each type.

---

### Task 1.3 — Guest

**File:** `com/hotel/model/Guest.java`

- [x] Create `class Guest extends BaseEntity`
- [x] Add fields: `fullName` (String), `phone` (String), `nidOrPassport` (String), `address` (String)
- [x] Add public getters and setters for all fields
- [x] Override `display()` — print name, phone, NID

→ **Test:** Create a `Guest` object, set values, call `display()`.

---

### Task 1.4 — Booking

**File:** `com/hotel/model/Booking.java`

- [x] Create `class Booking extends BaseEntity`
- [x] Add fields: `guestId` (String), `roomId` (String), `checkInDate` (String), `checkOutDate` (String), `totalBill` (double), `status` (String)
- [x] Add public getters and setters for all fields
- [x] Override `display()` — print booking ID, guest ID, room ID, dates, bill, status

---

### Task 1.5 — Manageable Interface

**File:** `com/hotel/service/Manageable.java`

- [x] Create `interface Manageable<T>`
- [x] Declare: `void add(T entity) throws HotelException`
- [x] Declare: `T getById(String id) throws HotelException`
- [x] Declare: `List<T> getAll()`
- [x] Declare: `void update(T entity) throws HotelException`
- [x] Declare: `void delete(String id) throws HotelException`

> ⚠ Import `java.util.List`. Do not add any implementation here — this is a contract only.

---

### Task 1.6 — Custom Exceptions

**Package:** `com/hotel/exception/`

- [x] Create `class HotelException extends RuntimeException`
  - Constructor: `public HotelException(String message) { super(message); }`
- [x] Create `class RoomNotAvailableException extends HotelException`
- [x] Create `class DuplicateRecordException extends HotelException`
- [x] Create `class RecordNotFoundException extends HotelException`
- [x] Create `class FileReadException extends HotelException`
- [x] Create `class FileWriteException extends HotelException`
- [x] Create `class InvalidInputException extends HotelException`

> ⚠ Every subclass needs only one constructor that calls `super(message)`. Keep them minimal.

---

### Task 1.7 — IDGenerator Utility

**File:** `com/hotel/util/IDGenerator.java`

- [x] Create `class IDGenerator`
- [x] Add static method: `public static String generate(String prefix, String filePath)`
- [x] If file is empty or only has a header line → return `prefix + "-001"`
- [x] Otherwise → read the last line, split by `,`, take index `[0]` (the ID column)
- [x] Parse the number from the ID (e.g. `"R-007"` → `7`)
- [x] Increment and return padded string (e.g. `8` → `"R-008"`)

→ **Test:** Point it at an empty file → should return `R-001`. Add a fake row with `R-005` → should return `R-006`.

---

## Phase 2 — File I/O (Repository Layer)

Connect the model to CSV files. After this phase, data survives application restarts.

---

### Task 2.1 — FileHandler

**File:** `com/hotel/util/FileHandler.java`

- [x] Create `class FileHandler`

**Method — readAll:**
- [x] `public static List<String[]> readAll(String filePath) throws FileReadException`
- [x] Open with `BufferedReader`
- [x] Skip the first line (header)
- [x] For each remaining line: split by `,` and add the array to the list
- [x] Return the list
- [x] Catch `IOException`, throw `FileReadException` with a clear message
- [x] Use try-with-resources to ensure the reader is always closed

**Method — writeAll:**
- [x] `public static void writeAll(String filePath, String header, List<String> rows) throws FileWriteException`
- [x] Open with `BufferedWriter`
- [x] Write the header line first
- [x] Write each row followed by a newline
- [x] Catch `IOException`, throw `FileWriteException`
- [x] Use try-with-resources

**Method — ensureFileExists:**
- [x] `public static void ensureFileExists(String filePath, String header)`
- [x] Create the `/data/` directory if it does not exist (`File.mkdirs()`)
- [x] If the file does not exist, create it and write the header as the first line

→ **Test:** Call `ensureFileExists` twice on the same file — should not duplicate the header.

---

### Task 2.2 — RoomRepository

**File:** `com/hotel/repository/RoomRepository.java`

- [x] Create `class RoomRepository`
- [x] Define constant: `FILE_PATH = "data/rooms.csv"`
- [x] Define constant: `HEADER = "roomId,roomNumber,roomType,pricePerNight,isAvailable,extraData,createdAt"`

**Methods to implement:**
- [x] `void save(Room room)` — append one new row
- [x] `List<Room> findAll()` — read all rows, construct `Room` objects
  - [x] Check `roomType` field to decide which subclass to instantiate (`STANDARD` → `StandardRoom`, etc.)
  - [x] Parse each field into the correct type (String, double, boolean, int)
- [x] `Room findById(String id)` — call `findAll()`, iterate, return match or `null`
- [x] `void update(Room room)` — `findAll()` → replace matching row → `writeAll()`
- [x] `void deleteById(String id)` — `findAll()` → remove matching row → `writeAll()`
- [x] `boolean existsByRoomNumber(String roomNumber)` — iterate `findAll()`, check match

→ **Test:** Save a room, call `findAll()` — verify it appears. Delete it — verify it's gone. Reopen application — verify data persisted.

---

### Task 2.3 — GuestRepository

**File:** `com/hotel/repository/GuestRepository.java`

- [x] Create `class GuestRepository`
- [x] Define `FILE_PATH` and `HEADER` constants
- [x] Implement: `save`, `findAll`, `findById`, `update`, `deleteById`
- [x] Add: `boolean existsByNid(String nid)` — iterate `findAll()`, check `nidOrPassport` field

---

### Task 2.4 — BookingRepository

**File:** `com/hotel/repository/BookingRepository.java`

- [x] Create `class BookingRepository`
- [x] Define `FILE_PATH` and `HEADER` constants
- [x] Implement: `save`, `findAll`, `findById`, `update`, `deleteById`
- [x] Add: `boolean hasActiveBookingForRoom(String roomId)`
  - Iterate `findAll()`, return `true` if any booking matches `roomId` AND `status.equals("ACTIVE")`
- [x] Add: `boolean hasActiveBookingForGuest(String guestId)`
  - Same logic but match on `guestId`

→ **Test:** Create a booking, check `hasActiveBookingForRoom()` returns `true`. Cancel it, check it returns `false`.

---

## Phase 3 — Business Logic (Service Layer)

Services sit between UI and repositories. They validate input, enforce business rules, and throw custom exceptions. The UI layer never touches repositories directly.

---

### Task 3.1 — RoomService

**File:** `com/hotel/service/RoomService.java`
**Implements:** `Manageable<Room>`

- [x] Declare field: `private RoomRepository roomRepository`
- [x] Declare field: `private BookingRepository bookingRepository`
- [x] Constructor: initialise both repositories

**Implement add(Room room):**
- [x] Validate room number is not empty
- [x] Validate price > 0
- [x] Call `roomRepository.existsByRoomNumber()` → throw `DuplicateRecordException` if true
- [x] Call `roomRepository.save(room)`

**Implement getAll():**
- [x] Delegate to `roomRepository.findAll()`

**Implement getById(String id):**
- [x] Call `roomRepository.findById(id)`
- [x] If result is `null` → throw `RecordNotFoundException("Room not found: " + id)`
- [x] Return result

**Implement update(Room room):**
- [x] Validate price > 0
- [x] Delegate to `roomRepository.update(room)`

**Implement delete(String id):**
- [x] Call `bookingRepository.hasActiveBookingForRoom(id)`
- [x] If true → throw `HotelException("Cannot delete: room has an active booking")`
- [x] Otherwise call `roomRepository.deleteById(id)`

**Add setAvailability:**
- [x] `public void setAvailability(String roomId, boolean status)`
- [x] Get room by ID, set `isAvailable`, call `update(room)`

---

### Task 3.2 — GuestService

**File:** `com/hotel/service/GuestService.java`
**Implements:** `Manageable<Guest>`

- [x] Declare field: `private GuestRepository guestRepository`
- [x] Declare field: `private BookingRepository bookingRepository`

**Implement add(Guest guest):**
- [x] Validate `fullName` is not empty and contains only letters and spaces
- [x] Validate `phone` is exactly 11 digits and starts with `01`
- [x] Validate `nidOrPassport` is not empty
- [x] Call `guestRepository.existsByNid()` → throw `DuplicateRecordException` if true
- [x] Call `guestRepository.save(guest)`

**Implement getAll, getById, update:**
- [x] Delegate to repository (same pattern as RoomService)

**Implement delete(String id):**
- [x] Check `bookingRepository.hasActiveBookingForGuest(id)`
- [x] If true → throw `HotelException("Cannot delete: guest has an active booking")`
- [x] Otherwise call `guestRepository.deleteById(id)`

---

### Task 3.3 — BookingService

**File:** `com/hotel/service/BookingService.java`
**Implements:** `Manageable<Booking>`

- [x] Declare fields: `bookingRepository`, `roomService`

**Implement add(Booking booking):**
- [x] Validate `checkInDate` is not in the past
- [x] Validate `checkOutDate` is after `checkInDate`
- [x] Get room via `roomService.getById(roomId)`
- [x] If `!room.isAvailable()` → throw `RoomNotAvailableException("Room is not available")`
- [x] Calculate `nights = calculateNights(checkIn, checkOut)`
- [x] Set `booking.totalBill = nights * room.getPricePerNight()`
- [x] Set `booking.status = "ACTIVE"`
- [x] Call `bookingRepository.save(booking)`
- [x] Call `roomService.setAvailability(roomId, false)`

**Implement getAll, getById, update:**
- [x] Delegate to repository

**Add cancelBooking:**
- [x] `public void cancelBooking(String bookingId)`
- [x] Find booking — throw `RecordNotFoundException` if missing
- [x] If `status` is already `"CANCELLED"` → throw `HotelException("Booking already cancelled")`
- [x] Set `status = "CANCELLED"`, call `bookingRepository.update(booking)`
- [x] Call `roomService.setAvailability(booking.getRoomId(), true)`

**Add helper:**
- [x] `private long calculateNights(String checkIn, String checkOut)`
- [x] Parse both dates using `LocalDate.parse()`
- [x] Return `ChronoUnit.DAYS.between(checkIn, checkOut)`

→ **Test:** Try to book the same room twice while first booking is ACTIVE → second attempt must throw `RoomNotAvailableException`.

---

## Phase 4 — GUI (Swing UI Layer)

Build the visual application. Wire each panel to the service layer. Take it one panel at a time.

---

### Task 4.1 — MainFrame

**File:** `com/hotel/ui/MainFrame.java`

- [ ] Create `class MainFrame extends JFrame`
- [ ] Set title: `"Hotel Management System"`
- [ ] Set size: `1100 × 700`, center on screen
- [ ] Set `defaultCloseOperation` to `EXIT_ON_CLOSE`
- [ ] Use `BorderLayout`

**Header (NORTH):**
- [ ] Create `JPanel` with system name label + current date label

**Sidebar (WEST):**
- [ ] Create `JPanel` with `BoxLayout` (Y_AXIS)
- [ ] Add buttons: Dashboard, Room Management, Guest Management, Booking Management
- [ ] Fixed width: ~180px

**Center (CENTER):**
- [ ] Create `JPanel` with `CardLayout`
- [ ] Add all management panels with string keys
- [ ] Wire each sidebar button to switch the active card

**Status bar (SOUTH):**
- [ ] Create `JLabel` for displaying last action result
- [ ] Add `public void setStatus(String message, boolean isError)` — green for success, red for error

---

### Task 4.2 — Dashboard Panel

**File:** `com/hotel/ui/panels/DashboardPanel.java`

- [ ] Create `class DashboardPanel extends JPanel`
- [ ] Use `GridLayout(2, 2)` or `FlowLayout` for metric cards
- [ ] Create four stat cards (each a bordered `JPanel` with a title label + number label):
  - [ ] **Total Rooms** — `roomService.getAll().size()`
  - [ ] **Available Rooms** — count where `isAvailable == true`
  - [ ] **Total Guests** — `guestService.getAll().size()`
  - [ ] **Active Bookings** — count where `status.equals("ACTIVE")`
- [ ] Add method: `public void refresh()` — reloads all four counts
- [ ] Call `refresh()` every time the dashboard card is shown

---

### Task 4.3 — Room Management Panel

**File:** `com/hotel/ui/panels/RoomPanel.java`

**Form section (top):**
- [ ] `JTextField` for Room Number
- [ ] `JComboBox` for Type: Standard / Deluxe / Suite
- [ ] `JTextField` for Price Per Night
- [ ] Dynamic section — shown/hidden based on type selection:
  - Standard: `JTextField` for Amenities
  - Deluxe: `JCheckBox` for AC, `JCheckBox` for Minibar
  - Suite: `JTextField` for Floor Number, `JCheckBox` for Jacuzzi, `JTextField` for Max Occupancy
- [ ] Add `ItemListener` to type dropdown to show/hide dynamic fields
- [ ] Buttons: **Add Room** and **Clear**
- [ ] Status label below form (red/green text)

**Table section (bottom):**
- [ ] `JTable` with columns: Room ID · Room No · Type · Price · Status · Edit · Delete
- [ ] Load from `roomService.getAll()` on panel open
- [ ] Filter controls: type dropdown + availability dropdown
- [ ] **Edit** button per row: fills form, changes button to **Save Changes**, shows **Cancel Edit**
- [ ] **Delete** button per row: shows confirmation dialog, calls `roomService.delete()`

**Wire actions:**
- [ ] Add Room → `roomService.add()` → refresh table → clear form → show success
- [ ] Save Changes → `roomService.update()` → refresh table → reset to Add mode
- [ ] Filter change → re-render table with filtered data

---

### Task 4.4 — Guest Management Panel

**File:** `com/hotel/ui/panels/GuestPanel.java`

**Form section:**
- [ ] `JTextField` for Full Name
- [ ] `JTextField` for Phone
- [ ] `JTextField` for NID / Passport
- [ ] `JTextField` for Address (optional)
- [ ] Buttons: **Register Guest** and **Clear**
- [ ] Status label

**Table section:**
- [ ] Columns: Guest ID · Name · Phone · NID · Address · Registered Date · Edit · Delete
- [ ] **Edit** per row: pre-fill form (NID field is read-only in edit mode)
- [ ] **Delete** per row: check for active bookings, confirm, delete

**Wire actions:**
- [ ] Register → `guestService.add()` → refresh → clear
- [ ] Save Changes → `guestService.update()` → refresh

---

### Task 4.5 — Booking Management Panel

**File:** `com/hotel/ui/panels/BookingPanel.java`

**Form section:**
- [ ] `JComboBox` for Guest — populated from `guestService.getAll()`, shows `Name (G-XXX)`
- [ ] `JComboBox` for Room — populated from `roomService.getAll()` filtered to `isAvailable == true`
- [ ] `JTextField` for Check-in Date (hint: `YYYY-MM-DD`)
- [ ] `JTextField` for Check-out Date
- [ ] Read-only `JLabel` showing calculated nights + total bill (updates on date field focus-lost)
- [ ] Button: **Confirm Booking**
- [ ] Status label

**Table section:**
- [ ] Columns: Booking ID · Guest · Room · Check-in · Check-out · Nights · Total Bill · Status · Cancel
- [ ] Filter: Status dropdown (All / Active / Cancelled)
- [ ] **Cancel** button per row — only enabled for `ACTIVE` rows
  - Shows confirmation dialog
  - Calls `bookingService.cancelBooking()`
  - Refreshes table and room dropdown

**Wire actions:**
- [ ] Confirm → validate → `bookingService.add()` → refresh table → refresh room dropdown → clear form
- [ ] Panel shown → refresh room dropdown (picks up any rooms freed by cancellations)

> ⚠ The room dropdown must refresh every time the Booking panel is opened. Rooms cancelled in another session would not appear otherwise.

---

## Phase 5 — InputValidator Utility

Centralise all validation logic so it is reusable across all service classes.

**File:** `com/hotel/util/InputValidator.java`

- [x] `public static boolean isNotEmpty(String value)` — returns `!value.trim().isEmpty()`
- [x] `public static boolean isValidPhone(String phone)` — 11 digits, starts with `01`
- [x] `public static boolean isPositiveNumber(String value)` — parseable as double and > 0
- [x] `public static boolean isValidDate(String date)` — matches format `YYYY-MM-DD`, parseable by `LocalDate.parse()`
- [x] `public static boolean isDateAfter(String dateA, String dateB)` — `A` is strictly after `B`
- [x] `public static boolean isTodayOrFuture(String date)` — date is ≥ today using `LocalDate.now()`

> ⚠ All methods return `boolean` and throw nothing. Services call these and throw `InvalidInputException` themselves with specific messages.

---

## Phase 6 — Integration and Final Testing

Wire everything together, run end-to-end tests, and polish.

---

### Task 6.1 — Application Entry Point

**File:** `com/hotel/Main.java`

- [ ] Create `public static void main(String[] args)`
- [ ] Call `FileHandler.ensureFileExists()` for all three CSV files with their headers
- [ ] Wrap in `SwingUtilities.invokeLater()`:
  - [ ] Instantiate `MainFrame`
  - [ ] Call `frame.setVisible(true)`

---

### Task 6.2 — End-to-End Manual Tests

Run these tests in order. Mark each one when it passes.

**Room tests:**
- [ ] Add a Standard Room → verify it appears in the table with status Available
- [ ] Add a Deluxe Room → AC and Minibar fields must be visible in the form
- [ ] Add a Suite → Floor, Jacuzzi, and Max Occupancy fields must be visible
- [ ] Try adding a room with a duplicate room number → error shown, no duplicate saved
- [ ] Edit a room's price → change persists after application restart

**Guest tests:**
- [ ] Register a guest → appears in table with correct ID
- [ ] Try registering a second guest with the same NID → error shown, no duplicate
- [ ] Edit a guest's phone number → change persists after restart

**Booking tests:**
- [ ] Create a booking → room removed from available dropdown → booking appears in table as ACTIVE
- [ ] Attempt to book the same room again while booking is ACTIVE → error shown
- [ ] Cancel the booking → room reappears in available dropdown → booking shows as CANCELLED

**Protection tests:**
- [ ] Try to delete a guest who has an ACTIVE booking → error shown
- [ ] Try to delete a room that has an ACTIVE booking → error shown

**Persistence test:**
- [ ] Add rooms, guests, bookings → close the application → reopen → all data must still be present

---

### Task 6.3 — UI Polish

- [ ] Consistent font and size across all panels (recommend `Arial 13`)
- [ ] Table column widths set appropriately — no text overflow
- [ ] All buttons consistent in size and padding
- [ ] Success messages shown in green, error messages in red
- [ ] Form clears automatically after a successful add
- [ ] Dashboard stats update after any CRUD operation in another panel
- [ ] Application window opens centered on screen

---

## Done

All `[ ]` boxes checked = project complete.
