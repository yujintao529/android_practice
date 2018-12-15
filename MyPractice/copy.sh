#!/bin/bash
if [[ $# != 3 ]];then
    echo "sorry,you must set fileName , search dictionary and target dictionary"
    exit 0
fi
echo "you copy $1 to $2"

find $1 -type f -iname $2 -print -exec cp {} $3 \;