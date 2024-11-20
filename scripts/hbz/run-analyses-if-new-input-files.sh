#!/bin/bash

# run this script with name of your catalogue as argument
# if no argument given defaults to test

APPDIR="/home/qa-catalogue/qa-catalogue"
CATALOGUE=${1:-test}
TASKS=${2:-all}
DATADIR="${APPDIR}/input/${CATALOGUE}/marc"
CHECKFILE="${DATADIR}/check_file.txt"
RUNNINGFILE="${APPDIR}/running_${CATALOGUE}_analyses.txt"
MAILRECIPIENT="phu.tu@hbz-nrw.de,tobias.buelte@hbz-nrw.de"

find ${DATADIR} -newer "$CHECKFILE" >/tmp/newfiles$$

if [[ -s "/tmp/newfiles$$" ]] ; then
  echo "files changed: "
  cat "/tmp/newfiles$$"
  echo "$(date) Now start analysis for ${CATALOGUE}" | tee | mail -s "QA catalogue: start analysis ${CATALOGUE}" $MAILRECIPIENT
  # analysis still running?
  if [[ -f "$RUNNINGFILE" ]]; then
    echo "$(date) Oops, previous analysis is still running! Try again later." | tee | mail -s "QA catalogue: try again later" $MAILRECIPIENT
  else
    touch "$RUNNINGFILE"
    cd $APPDIR
    docker exec -i metadata-qa-marc ./catalogues/${CATALOGUE}.sh ${TASKS}
    echo "$(date) Finished analysis for ${CATALOGUE}!" | tee | mail -s "QA catalogue: Finished!" $MAILRECIPIENT
    rm "$RUNNINGFILE"
    touch "$CHECKFILE"
    rm "/tmp/newfiles$$"
  fi
else
  echo "No files changed"
  touch "$CHECKFILE"
  rm "/tmp/newfiles$$"
fi
