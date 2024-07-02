# Verwende ein Scala-SBT-Image mit JDK 17
FROM hseeberger/scala-sbt:17.0.2_1.6.2_3.1.1

# Installiere notwendige Bibliotheken
RUN apt-get update && \
    apt-get install -y \
    libxrender1 \
    libxtst6 \
    libxi6 \
    libgl1-mesa-glx \
    libgtk-3-0 \
    openjfx \
    libgl1-mesa-dri \
    libgl1-mesa-dev \
    libcanberra-gtk-module \
    libcanberra-gtk3-module \
    default-jdk

# Setze das Arbeitsverzeichnis im Container
WORKDIR /app

# Kopiere den Rest des Anwendungsquellcodes
ADD . /app

# Setze Umgebungsvariablen für die grafische Ausgabe
ENV DISPLAY=host.docker.internal:0

# Baue die Anwendung
RUN sbt update
RUN sbt compile

# Setze den Befehl zum Starten der Anwendung
CMD ["sbt", "-Djava.awt.headless=false", "-Dawt.useSystemAAFontSettings=lcd", "-Dsun.java2d.xrender=true", "run"]
