# Hotel Management System

A desktop application built with **Java Swing** for managing hotel operations.

---

## Prerequisites

Before you begin, make sure you have the following installed on your machine.

### 1. Install Java Development Kit (JDK)

You need **JDK 17 or higher**.

**Windows:**
1. Download JDK from [https://adoptium.net](https://adoptium.net) (Temurin is recommended)
2. Run the installer and follow the setup wizard
3. Add Java to your system PATH:
   - Open **System Properties** → **Advanced** → **Environment Variables**
   - Under **System Variables**, find `Path` and add your JDK `bin` folder (e.g., `C:\Program Files\Eclipse Adoptium\jdk-17\bin`)
4. Verify installation:
   ```bash
   java -version
   javac -version
   ```

**macOS:**
```bash
# Using Homebrew
brew install --cask temurin

# Verify
java -version
javac -version
```

**Linux (Ubuntu/Debian):**
```bash
sudo apt update
sudo apt install openjdk-17-jdk

# Verify
java -version
javac -version
```

Expected output (version may vary):
```
openjdk version "17.x.x" ...
```

---

## Getting Started

### 2. Clone the Repository

```bash
git clone https://github.com/your-username/Hotel-Management-Java.git
cd Hotel-Management-Java
```

> Replace `your-username` with your actual GitHub username.

### 3. Project Structure

```
Hotel-Management-Java/
├── src/          # Java source files
├── bin/          # Compiled .class files (auto-generated, git-ignored)
├── lib/          # External .jar dependencies (add your JARs here)
└── .vscode/      # VS Code project settings
```

---

## Running the Project

### Option A: Using VS Code (Recommended)

1. Install [Visual Studio Code](https://code.visualstudio.com/)
2. Install the **Extension Pack for Java** from the VS Code marketplace:
   - Open VS Code → Extensions (`Ctrl+Shift+X` / `Cmd+Shift+X`)
   - Search for `Extension Pack for Java` by Microsoft and install it
3. Open the project folder:
   ```
   File → Open Folder → select Hotel-Management-Java
   ```
4. Open `src/App.java` and click the **Run** button (▶) at the top right, or press `F5`

### Option B: Using the Terminal

**Compile:**
```bash
javac -d bin src/*.java
```

If you have JARs in `lib/`:
```bash
# macOS/Linux
javac -cp "lib/*" -d bin src/*.java

# Windows
javac -cp "lib\*" -d bin src\*.java
```

**Run:**
```bash
# macOS/Linux
java -cp bin App

# Windows
java -cp bin App
```

With library JARs:
```bash
# macOS/Linux
java -cp "bin:lib/*" App

# Windows
java -cp "bin;lib\*" App
```

---

## Adding Dependencies

Place any required `.jar` files inside the `lib/` folder. VS Code will automatically pick them up via the project settings in `.vscode/settings.json`.

---

## Troubleshooting

| Problem | Solution |
|---|---|
| `java: command not found` | JDK is not installed or not added to PATH |
| `error: cannot find symbol` | Make sure all source files are in `src/` and recompile |
| Blank/invisible window on macOS | Run with `-Dapple.awt.application.appearance=system` flag |
| `ClassNotFoundException` | Ensure `bin/` is on the classpath when running |
