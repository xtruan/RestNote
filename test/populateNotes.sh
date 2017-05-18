#/bin/bash

while read p; do
  curl -i -H "Content-Type: application/json" -X POST -d "$p" http://localhost/api/notes
done <slogans.txt