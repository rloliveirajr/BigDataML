#!/bin/bash


SERVERS=(`cat "$2"`)
if [ $1 -eq "-send" ]
    then
        
        for server in ${SERVERS[@]}; 
            do 
                echo "scp $3 $server:/tmp/"
            done
    elif [$1 -eq "-clear"]
        for server in ${SERVERS[@]}; 
            do 
                scp "echo $server rm /tmp/$3"
            done  
    fi
