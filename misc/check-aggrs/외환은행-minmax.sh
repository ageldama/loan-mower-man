#!/bin/bash
python3 csv-select-cols.py ../../src/test/resources/example01-utf8.csv 0 1 9 \
| sed 1d \
| awk 'BEGIN {FS=",";OFS=","} {years[$1] += $3} END{ for(i in years){ print(i, years[i]) } }' \
| sort -t, -n -k1 \
| awk 'NR==1; END{print}'

