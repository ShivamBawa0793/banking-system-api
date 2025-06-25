# --- Stage 1: Build the Application ---
# Use eclipse-temurin:17-jdk-focal as the builder image
# This image contains Java Development Kit (JDK) but not Maven by default.
FROM eclipse-temurin:17-jdk-focal as builder

# Set the working directory inside this builder container
WORKDIR /app

# Install Maven within this stage.
# First, update package lists and install necessary tools like wget for downloading.
RUN apt-get update && apt-get install -y wget unzip && rm -rf /var/lib/apt/lists/*

# Define Maven version and installation directory
ENV MAVEN_VERSION 3.9.6
ENV MAVEN_HOME /usr/local/apache-maven-$MAVEN_VERSION

# Download, extract, and set up Maven.
# Maven binaries are downloaded, extracted to /usr/local, and then a symlink
# or direct move is used to establish the MAVEN_HOME.
# Finally, clean up the downloaded archive to keep the image size down.
RUN wget https://archive.apache.org/dist/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.zip -P /tmp/ \
    && unzip -q /tmp/apache-maven-$MAVEN_VERSION-bin.zip -d /usr/local/ \
    && rm /tmp/apache-maven-$MAVEN_VERSION-bin.zip

# Add Maven's 'bin' directory to the PATH so the 'mvn' command can be found
ENV PATH $MAVEN_HOME/bin:$PATH

# Verify Maven installation (optional, but good for debugging build issues)
RUN mvn -v

# Copy the Maven project files (pom.xml and src directory) into the working directory.
# Copying pom.xml first allows Docker to cache the dependency download step
# if only source code changes, speeding up subsequent builds.
COPY pom.xml .
COPY src src/

# Build the Spring Boot application using Maven.
# This command compiles the code, runs tests (unless -DskipTests is used),
# and packages the application into a JAR file.
# The JAR file will be located in the 'target/' directory within this stage.
# -DskipTests is used here to skip running unit tests during the Docker build,
# which can significantly speed up the image creation process.
RUN mvn clean package -DskipTests

# --- Stage 2: Create the Final Image (Runtime Image) ---
# Use eclipse-temurin:17-jre-focal as the base for the final image.
# This image contains only the Java Runtime Environment (JRE), which is smaller
# and more suitable for running the application in production.
FROM eclipse-temurin:17-jre-focal

# Set the working directory inside this runtime container
WORKDIR /app

# Copy the built JAR file from the 'builder' stage to the current runtime stage.
# The 'builder' stage is aliased as 'builder' at the top.
# The JAR file is expected to be at '/app/target/bankingsystem-1.0.0-SNAPSHOT.jar'
# within the 'builder' stage, as Maven typically puts it in the 'target/' directory
# relative to the project root.
# It is copied to 'app.jar' in the current working directory of the runtime stage.
COPY --from=builder /app/target/bankingsystem-1.0.0-SNAPSHOT.jar app.jar

# Expose the port that your Spring Boot application will listen on.
# The default port for Spring Boot is 8080.
EXPOSE 8080

# Define the command to run the application when the container starts.
# It executes the Java Runtime Environment with the '-jar' option to run the
# packaged Spring Boot application.
ENTRYPOINT ["java", "-jar", "app.jar"]

# Optional: If your Spring Boot application uses a file-based H2 database (e.g.,
# configured with 'jdbc:h2:file:./data/bankingsystem'), it's highly recommended
# to define a VOLUME. This ensures that the database file (and any other persistent
# data) is stored outside the container, so it persists even if the container is
# removed or recreated. This helps prevent data loss.
# VOLUME /data
