graphhopper-ios
===============

graphhopper-ios wraps [graphhopper](https://github.com/graphhopper/graphhopper/) 
and creates the `libgraphhopper.a` library to be used on iOS and OS X. 
It uses [j2objc](https://github.com/google/j2objc) to translate the .java sources 
into Objective-C.

*This is experimental so treat it accordingly.*

## Getting Started

To get started run the following commands in Terminal:

```sh
git clone --recursive https://github.com/graphhopper/graphhopper-ios.git
```

This will clone the repository and all its submodules. Now you are ready 
to use GraphHopper on iOS or OS X.

You have two options:

1. Head over to [graphhopper-ios-sample](https://github.com/graphhopper/graphhopper-ios/tree/master/graphhopper-ios-sample) 
and follow the instructions there. This is the easiest way to get started.

2. Manually add *graphhopper.xcodeproj* to your Xcode project. See the Usage section below.

## Usage

You can either add *graphhopper.xcodeproj* to your project and let Xcode compile the library 
or you can compile it from the Terminal and then add the library and sources
to your Xcode project.

### Xcode

To configure your project to use *graphhopper.xcodeproj* follow the steps below:

- Drag&drop *graphhopper.xcodeproj* into your project (or use the menu File -> Add Files to...)
- Expand graphhopper.xcodeproj and drag&drop the **Translations** and **Libraries** 
groups into your project (make sure you check "Create folder references" and have 
your target selected in "Add to targets:")
- In the Build Settings of your project:
    - add `-ObjC` to your target's Other Linker Flags
    - add `{path-to-graphhopper-ios}/j2objc/include` and `{path-to-graphhopper-ios}/src` 
    to your target's User Header Search Paths
- In the Build Phases of your project:
    - in Target Dependencies add the **graphhopper** target
    - in Link Binary With Libraries add **Security.framework** and **libz.dylib**

You're now ready to use GraphHopper on iOS and OS X.

> You are responsible for importing graph data. For an example check out 
[graphhopper-ios-sample](https://github.com/graphhopper/graphhopper-ios/tree/master/graphhopper-ios-sample).

### Terminal

Alternatively, you can translate and compile the library by invoking `make` 
in the Terminal. You can then link the library 
`graphhopper-ios/build/libgraphhopper.a` and it's header files at `graphhopper-ios/src` 
manually into your project. For all the other configurations see the Xcode section above.

> This method compiles the library for the following architectures: 
macosx, simulator, iphoneos, so using Xcode instead is recommended.

## Example

![iPhone-offline-routing](screenshots/iPhone-offline-routing.gif)

## Requirements

* iOS 7.0+ or OS X 10.10 (it might work on older versions but haven't tested)
* JDK 1.7 or higher
* Xcode 6.0 or higher
