graphhopper-ios-sample
======================

This is a sample project that provides a starting point for 
[graphhopper-ios](https://github.com/clns/graphhopper-ios). 
This is the easiest way to get started.

## Getting Started

To get started run the following commands in Terminal:

```sh
git clone https://github.com/clns/graphhopper-ios-sample.git
cd graphhopper-ios-sample
./prepare.sh
```

#### Import Data

For the routing to work, you need to import graph data. The Xcode project 
has a reference to the graph data at *graphhopper-ios/graphhopper/graph-data.osm-gh*.

You can import a sample graph by running this command in Terminal:

```sh
./import-sample.sh
```

You're done! Open the Xcode project, build & run and experiment with GraphHopper on iOS.