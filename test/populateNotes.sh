#/bin/bash

while read body; do
  curl -i -H "Content-Type: application/json" -X POST -d "$body" "http://localhost/api/notes"
done <slogans.txt