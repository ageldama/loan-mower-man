import sys
import csv

def sanitize_as_int(s):
    try:
        return int(s.replace(",", ""))
    except:
        return s

def select_cols(filename, column_numbers):
    with open(filename, 'r') as f:
        reader = csv.reader(f)
        writer = csv.writer(sys.stdout)
        for row in reader:
            new_row = [ sanitize_as_int(row[col_no]) for col_no in column_numbers ]
            writer.writerow(new_row)

if __name__ == '__main__':
    if len(sys.argv) < 3:
        print("Usage: {} [filename] [colnum-number]+".format(sys.argv[0]),
            file=sys.stderr)
        sys.exit(-1)
    else:
        filename = sys.argv[1]
        col_nos = [ int(i) for i in sys.argv[2:] ]
        select_cols(filename, col_nos)
