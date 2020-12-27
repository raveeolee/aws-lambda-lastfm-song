#!/bin/bash

WORKSPACE=${WORKSPACE:-`eval echo "~$USER"`/eclipse-workspace}
FOLDER=${FOLDER:-`pwd`/}

echo "Developer certificate keys generated here: $WORKSPACE"
echo "Folder: $FOLDER"

if [ -f ${WORKSPACE}/developer_key.der ]; then
    echo "Certificate keys already created"
else
    echo "Generating Certificate keys"
    openssl genrsa -out ${WORKSPACE}/developer_key.pem 4096
    openssl pkcs8 -topk8 -inform PEM -outform DER -in ${WORKSPACE}/developer_key.pem -out ${WORKSPACE}/developer_key.der -nocrypt
fi

docker run -it --rm \
    -v $WORKSPACE:/home/developer/eclipse-workspace \
    -v $FOLDER:/home/developer/project \
    kalemena/connectiq:latest \
    monkeyc -f /home/developer/project/monkey.jungle \
    -o /home/developer/project/aws_song.prg \
    -y /home/developer/eclipse-workspace/developer_key.der

echo 'Now connect the watch and place: aws_song.prg in GARMIN/APPS'    
