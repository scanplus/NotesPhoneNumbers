FROM openjdk:8-jre-alpine

MAINTAINER Tobias Genannt <t.genannt@scanplus.de>
LABEL Description="This image is used to run the NotesPhoneNumbers updater" Vendor="ScanPlus GmbH" Version="1.0"

RUN mkdir /ScanPlus
ADD target/NotesPhoneNumbers-jar-with-dependencies.jar /ScanPlus/

ENTRYPOINT ["java", "-jar", "/ScanPlus/NotesPhoneNumbers-jar-with-dependencies.jar"]