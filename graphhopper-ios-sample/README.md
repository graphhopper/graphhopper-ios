graphhopper-ios-sample
======================

This is a sample project that provides a starting point for running GraphHopper on iOS. 
This is the easiest way to get started.

## Getting Started

To get started the only thing you need to do is to import data.

#### Import Data

The Xcode project has a reference to the graph data at 
*graphhopper-ios/graphhopper/graph-data.osm-gh*.

You can import a sample graph by running this command in Terminal 
(while in the root of [graphhopper-ios](https://github.com/graphhopper/graphhopper-ios)):

```sh
./graphhopper-ios-sample/import-sample.sh
```

This will import romania-latest.osm.pbf from http://download.geofabrik.de 
and you'll be able to create routes inside Romania.

To import another country you can use something like:

```sh
FILE=germany-latest.osm.pbf JAVA_OPTS="-Xmx4096m -Xms1000m -server" ./graphhopper-ios-sample/import-sample.sh
# if a large file is imported, JAVA_OPTS may need to be changed
```

You're done! Open *graphhopper-ios-sample/graphhopper-ios-sample.xcodeproj* in Xcode, build & run 
and experiment with GraphHopper on iOS and OS X.

## Requirements

Everything that [graphhopper-ios](https://github.com/graphhopper/graphhopper-ios) 
requires but the sample project was only tested on iOS 8.0+.

Also for the import you'll need [Maven](http://maven.apache.org).
