FROM ubuntu:latest
LABEL authors="Khem"

ENTRYPOINT ["top", "-b"]