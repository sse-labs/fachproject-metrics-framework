#!/bin/bash
set -eu

# folder containing jars
JARFOLDER="jars"
# output folder
OUT="reports"
# analyses to execute
ANALYSES=("class.NumberOfFunctions" "classes.LCOM" "methods.CRef" "class.AVGFanIn" "class.AVGFanOut")
# how many rounds
ROUNDS=10

# build --include-analysis parameter
INCLUDE=""
for an in ${ANALYSES[@]}; do INCLUDE="$INCLUDE --include-analysis $an"; done
mkdir -p $OUT

# run performance analyses
for count in $(seq -w 1 $ROUNDS)
do
  echo "Measure performance, round $count"
  java -jar analysis-application.jar \
    $INCLUDE \
    --evaluate-performance \
    --no-jre-classes \
    --out-file /dev/null \
    --batch-mode $JARFOLDER

  mv performance-report.csv $OUT/performance-report-$count.csv
done

exit 0
