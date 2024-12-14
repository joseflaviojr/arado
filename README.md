# Arado

Utility that makes it easy to save the contents of the clipboard to files.

## How to use

To use this utility, run the command below with Java 17 (or later). It is recommended to configure a keyboard shortcut to activate it.

```sh
java -jar arado-1.0.0.jar
```

## Development of this code

You can import this directly as a Gradle project, or optionally generate specific artifacts for each IDE:

```sh
gradle cleanEclipse eclipse
gradle cleanIdea idea
```

### Development requirements

* Git >= 2.47
* Java >= 17
* Gradle >= 8.11

### Test

Run the unit tests:

```sh
gradle clean test
```

### Distribution

Compile into distribution files (JAR):

```sh
gradle clean build
```

## License

Arado is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software
Foundation, either version 3 of the License, or (at your option) any later version.

Arado is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with Arado. If not, see <https://www.gnu.org/licenses/>.
