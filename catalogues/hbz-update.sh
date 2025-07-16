#!/usr/bin/env bash

. ./setdir.sh

NAME=hbz-update
TYPE=xml
if [[ $TYPE == "marc" ]]; then
  echo "marc"
  TYPE_PARAMS="--marcVersion HBZ --fixAlma"
  MARC_DIR=${BASE_INPUT_DIR}/${NAME}/marc
  MASK=*.mrc
elif [[ $TYPE == "xml" ]]; then
  echo "xml"
  # In contrast to hbz-lobid-publishing this does exclude the lobid publishing profile fields e.g. ITM, HOL etc..
  TYPE_PARAMS="--emptyLargeCollectors --marcVersion HBZ --marcxml --fixAlma --ignorableRecords DEL$a=Y  --ignorableFields 964,940,941,942,944,945,946,947,948,949,950,951,952,955,956,957,958,959,966,967,970,971,972,973,974,975,976,977,978,978,979,GEL,GGN,GKS,GKT,GPN,GSI,GST,H16,H24,H35,H42,H43,H52,H56,H62,H66,H69,H80,H83,H89,H92,H93,H94,H95,HAD,HOL,ITM,MBD,MNG,POC,POE,POR"
  MARC_DIR=${BASE_INPUT_DIR}/${NAME}/marc
  MASK=*.gz
else
  echo "else: ${TYPE}"
fi

. ./common-script
