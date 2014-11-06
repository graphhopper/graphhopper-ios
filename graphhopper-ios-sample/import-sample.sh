cd graphhopper
FILE="romania-latest.osm.pbf"
GRAPH_FILE="graph-data.osm.pbf"
if [ ! -e $FILE ] && [ ! -e $GRAPH_FILE ]; then
  echo "Downloading http://download.geofabrik.de/europe/$FILE"
  curl -O http://download.geofabrik.de/europe/$FILE
fi
if [ ! -e $GRAPH_FILE ]; then
  mv $FILE $GRAPH_FILE
fi
git stash save "Before importing"
./graphhopper.sh import $GRAPH_FILE
git stash pop
