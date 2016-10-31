#!/usr/bin/env bash

cd graphhopper
if [ -z ${FILE+x} ]; then
  FILE="romania-latest.osm.pbf"
fi
GRAPH_FILE="graph-data.osm.pbf"
if [ ! -e $FILE ] && [ ! -e $GRAPH_FILE ]; then
  echo "Downloading http://download.geofabrik.de/europe/$FILE"
  curl -O http://download.geofabrik.de/europe/$FILE
fi
if [ ! -e $GRAPH_FILE ]; then
  mv $FILE $GRAPH_FILE
fi
rm -rf $(expr $GRAPH_FILE : '\([^\.]*\)\.')".osm-gh"
git checkout 0.8.2
./graphhopper.sh import $GRAPH_FILE
git checkout ios-compat
cd ..
touch class.list
