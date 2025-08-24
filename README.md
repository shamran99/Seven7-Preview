# Seven7-Preview

## Overview

Seven7-Preview is a Java-based application designed to manage your personal finance with so many features like report generation, gogle drive sync and multiple language and currency support.
It allows you to easily manage and preview your data with simple commands.

## Prerequisites

Before setting up Seven7-Preview, ensure you have the following installed:

* **Java 8** (higher versions may also work)
* **Apache Maven**
* Access to modify configuration files (`externalData.properties` and `jdriveSync.properties`)

## Setup Instructions

### 1. Clone the Repository

```bash
git clone https://github.com/shamran99/Seven7-Preview.git
cd Seven7-Preview
```

### 2. Configure Properties

Seven7-Preview requires configuration via two properties files:

* `externalData.properties` – contains external data paths and related settings. Update the file to match your environment. Example:

```properties
data.path=/path/to/your/data
output.path=/path/to/output
```

* `jdriveSync.properties` – contains Google Drive synchronization settings. Example:

```properties
client_id=YOUR_CLIENT_ID
client_secret=YOUR_CLIENT_SECRET
refresh_token=YOUR_REFRESH_TOKEN
folder_id=YOUR_FOLDER_ID
```

**Note:** These files must be updated with valid values before building and running the application.

### 3. Generate Google Drive Sync Tokens

To enable Google Drive synchronization, run the included script:

```bash
./generate_gdrivesync_tokens.sh
```

This script will generate the required authentication tokens. Refer to `guideToGenerateSyncToken.txt` for step-by-step instructions.

### 4. Build the Project

Use Maven to build the project and generate the executable JAR:

```bash
mvn clean install
```

After building, you will find `seven7.jar` in the `target/` directory.

### 5. Run the Application

Execute the JAR file with:

```bash
java -jar target/seven7.jar
```

You can also pass optional command-line arguments depending on your use case. Example commands:

* Sync data with Google Drive:

```bash
java -jar target/seven7.jar --sync
```

* Preview data:

```bash
java -jar target/seven7.jar --preview
```

## Usage

After running the JAR, the application will perform tasks based on your configuration and commands.

* The `--sync` option uploads or synchronizes data to Google Drive.
* The `--preview` option generates a preview of your data locally.

Ensure the properties files are correctly set up before using these commands.

## Troubleshooting

| Issue                    | Resolution                                                                                                                                                         |
| ------------------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| Maven build fails        | Ensure Java 8+ and Maven are installed and on PATH. Run `mvn -version` to verify.                                                                                  |
| Missing properties error | Double-check the values in `externalData.properties` and `jdriveSync.properties`.                                                                                  |
| Token generation fails   | Ensure the script has execute permissions (`chmod +x generate_gdrivesync_tokens.sh`) and run it in the correct environment. Follow `guideToGenerateSyncToken.txt`. |
| Application not syncing  | Verify your Google Drive credentials and folder ID in `jdriveSync.properties`.                                                                                     |

## Project Structure

```
├── src/main/java         – Java source code
├── pom.xml               – Maven configuration
├── externalData.properties
├── jdriveSync.properties
├── generate_gdrivesync_tokens.sh
├── guideToGenerateSyncToken.txt
└── README.md
```

## Contributing

Contributions are welcome!

* Fork the repository and create a new branch for your feature or bugfix.
* Submit a pull request with clear description and testing steps.
* Report issues via the GitHub issue tracker.

## License



## Contact

For questions or support, open an issue in this repository or contact \[your email or GitHub handle].
