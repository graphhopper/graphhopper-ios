# How to contribute

This is an experimental project so please feel free to help in any way.
Besides what the sample app does, that is routing between 2 points,
this hasn't been tested further.

This project wraps [graphhopper](https://github.com/graphhopper/graphhopper),
specifically the [ios-compat](https://github.com/graphhopper/graphhopper/tree/ios-compat)
branch, and translates the [.java sources](class.list) into Objective-C using j2objc.

There is some [removed / commented out code](https://github.com/graphhopper/graphhopper/compare/0.5.0...ios-compat)
in the GraphHopper sources in order for it to work with j2objc and iOS. Ideally,
all these changes will be resolved in the future so that there won't be a need for a separate branch.

## Community

Feel free to raise problems or questions in [our forum](https://discuss.graphhopper.com/c/graphhopper/graphhopper-ios-and-android).

## Upgrade GraphHopper

The version of GraphHopper used is the tip of the
[ios-compat](https://github.com/graphhopper/graphhopper/tree/ios-compat) branch.
To update GraphHopper inside this project, you can do the following steps
(each step assumes the working directory is the root of graphhopper-ios):

* Make sure GraphHopper is updated:

```sh
cd graphhopper
git pull
```

* Merge an updated branch (eg. `master` or a tag `0.5.0`) into `ios-compat`,
resolving any conflicts:

```sh
cd graphhopper
git merge --no-commit -X patience master
# if any conflicts you can use mergetool (or any other tool):
git mergetool
# after all conflicts are resolved (git status), commit:
git commit
```

* Clean all previous build files and re-generate class.list:

```sh
make cleanall
make class.list
```

If using Xcode, make sure you clean the project at this time (Product -> Clean).

* Build and run the project again, eg. in Xcode; if everything still works,
then you can commit the new tip of GraphHopper:

```sh
git add graphhopper
git commit -m "Update GraphHopper to ..."
```

**Note:** It's possible the graph data needs to be
[generated](https://github.com/graphhopper/graphhopper-ios/tree/master/graphhopper-ios-sample#import-data)
again, using the new version of GraphHopper.

**Note:** It's also possible that after the update new translations become available, so make sure you have all the translations from */graphhopper-ios/build/translations* added into Xcode. This is the case if you get this error while running in Xcode: *"No input stream found in class path!?"*, in util/TranslationMap.java.
