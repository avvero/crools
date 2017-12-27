FROM anapsix/alpine-java:8_server-jre_unlimited

ADD build/install/crools /crools
ENTRYPOINT ["/crools/bin/crools"]