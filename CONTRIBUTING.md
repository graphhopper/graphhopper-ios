# How to contribute

This is an experimental project so please feel free to help in any way.

This project wraps [graphhopper](https://github.com/graphhopper/graphhopper),
specifically the [ios-compat](https://github.com/graphhopper/graphhopper/tree/ios-compat)
branch, and translates the [.java sources](class.list) into Objective-C using [j2objc](https://github.com/google/j2objc).

There is some [removed / commented out code](https://github.com/graphhopper/graphhopper/compare/master...ios-compat)
in the GraphHopper sources in order for it to work with j2objc and iOS. Ideally,
all these changes will be resolved in the future so that there won't be a need for a separate branch.

## Community

Feel free to raise problems or questions in [our forum](https://discuss.graphhopper.com/c/graphhopper/graphhopper-ios-and-android).

## Upgrade GraphHopper

The version of GraphHopper used is the tip of the
[ios-compat](https://github.com/graphhopper/graphhopper/tree/ios-compat) branch.
To update GraphHopper inside this project, you can do the following steps
(each step assumes the **working directory is the root of graphhopper-ios**):

* Make sure GraphHopper is updated:

```sh
cd graphhopper
git fetch --all
```

* Merge a remote branch or tag (eg. `origin/master`, `0.5.0`) into `ios-compat`,
resolving any conflicts:

```sh
cd graphhopper
git merge --no-commit -X patience 0.5.0
# if any conflicts you can use mergetool (or any other tool):
git mergetool
# after all conflicts are resolved (git status), commit:
git commit
```

* Update `graphhopper-ios-sample` import script to the same tag (branch) you just updated graphhopper to. To do this edit [graphhopper-ios-sample/import-sample.sh](graphhopper-ios-sample/import-sample.sh#L16) and [graphhopper-ios-sample/README.md](graphhopper-ios-sample/README.md#import-data). Commit the changes:

```sh
cd graphhopper-ios-sample
git add .
git commit -m "Update import script to use GraphHopper X.X.X"
```

* Clean all previous build files and re-generate `class.list`:

```sh
make cleanall
make class.list
```

If using Xcode, make sure you clean the project at this time (Product -> Clean).

* Build and run the project again (eg. in Xcode). If everything still works,
you can push and commit the new tip of GraphHopper:

```sh
cd graphhopper
git push
cd ..
git add .
git commit -m "Update GraphHopper to X.X.X"
```

**Note:** It's possible the graph data needs to be
[generated](https://github.com/graphhopper/graphhopper-ios/tree/master/graphhopper-ios-sample#import-data)
again, using the new version of GraphHopper.

**Note:** It's also possible that after the update new translations become available, so make sure you have all the translations from */graphhopper-ios/build/translations* added into Xcode. This is the case if you get this error while running in Xcode: *"No input stream found in class path!?"*, in util/TranslationMap.java.
