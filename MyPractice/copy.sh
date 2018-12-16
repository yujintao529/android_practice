#!/bin/bash
if [[ $# != 3 ]];then
    echo "sorry,you must set fileName , search dictionary and target dictionary"
    exit 0
fi
#echo "you copy $1 from $2 to $3"


find $2 -type f -iname *$1* -print -exec echo "copy {} to $3" \;
find $2 -type f -iname *$1* -print -exec cp -r {} $3 \;
