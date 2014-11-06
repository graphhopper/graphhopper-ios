graphhopper-ios-sample
======================

This is a sample project that provides a starting point for [[graphhopper-ios|]]. 
This is the easiest way to get started.

## Getting Started

To get started the only thing you need to do is to import data.

#### Import Data

For the routing to work, you need to import graph data. The Xcode project 
has a reference to the graph data at *graphhopper-ios/graphhopper/graph-data.osm-gh*.

You can import a sample graph by running this command in Terminal:

```sh
./graphhopper-ios-sample/import-sample.sh
```

This will import romania-latest.osm.pbf from http://download.geofabrik.de 
and you'll be able to create routes inside Romania.

You're done! Open *graphhopper-ios-sample.xcodeproj* in Xcode, build & run 
and experiment with GraphHopper on iOS and OS X.

## Requirements

Everything that graphhopper-ios requires but was only tested on iOS 8.0+.
