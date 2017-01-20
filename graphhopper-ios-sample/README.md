graphhopper-ios-sample
======================

This is a sample project that provides a starting point for running GraphHopper on iOS.
This is the easiest way to get started. It uses [mbxmapkit](https://github.com/mapbox/mbxmapkit),
so most of the code is boilerplate, the GraphHopper-related code is in
[Directions.m](graphhopper-ios-sample/Directions.m).

## Getting Started

To get started the only thing you need to do is to import data.

The sample Xcode project has a reference to the graph data at
*graphhopper-ios/graphhopper/graph-data.osm-gh*, where you can place your graph data.

#### Import Data

**Caution:** The import method described below uses the
[graphhopper master branch](https://github.com/graphhopper/graphhopper/tree/master)
in /graphhopper-ios/graphhopper to import the graph data using `./graphhopper.sh import` command.

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
and experiment with GraphHopper on iOS and OS X. You can place start and end points on the map by holding down the tap.

## Troubleshooting

##### Error: JAVA_HOME is not defined correctly

If you get errors when importing data with the above script,
make sure you have Maven installed by running `mvn -version`.
If not, you can install it with `brew install maven`.

##### Command /usr/bin/make failed with exit code 2

Most of the times this happens because of some incorrect paths to the SDK.
Try cleaning the project in Xcode (Product - Clean) and build again.

## Requirements

Everything that [graphhopper-ios](https://github.com/graphhopper/graphhopper-ios)
requires but the sample project was only tested on iOS 8.0+.

Also for the automatic import method described above you'll need [Maven](http://maven.apache.org).
