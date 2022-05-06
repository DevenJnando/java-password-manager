#!/bin/bash

function check_args {
  if [ $# -eq 0 ]
    then
      echo "No arguments provided. Exiting."
  fi
}

function open_download_location {
  if [[ "$OSTYPE" == "linux-gnu" ]]
    then
        xdg-open $1
  elif [[ "$OSTYPE" == "darwin" ]]
    then
        open $1
  fi
}

check_args $@
open_download_location $@
