#!/bin/bash

temp_file=$(mktemp)
echo ${temp_file} >&2

python3 csv-select-cols.py ../../src/test/resources/example01-utf8.csv $(seq 0 10) \
| sed 1d \
| egrep '^2005\,' > ${temp_file}

col_no=`python3 csv-select-cols.py ${temp_file} $(seq 2 10) \
| awk 'BEGIN {FS=",";OFS=","} {for(i=1;i<(NF+1);i++){sums[i]+=$i}} END {for(i in sums){print(i, sums[i])}}' \
| sort -t, -n -k2 -r \
| head -n1 \
| awk 'BEGIN{FS=","} {print $1 + 2 - 1}'`
echo ${col_no} >&2

python3 csv-select-cols.py ../../src/test/resources/example01-utf8.csv $col_no | head -n1

rm ${temp_file}
