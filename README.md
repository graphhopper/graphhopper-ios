graphhopper-ios
===============

graphhopper-ios wraps [graphhopper](https://github.com/graphhopper/graphhopper/) 
and creates the `libgraphhopper.a` library to be used on iOS and OS X. 
It uses [j2objc](https://github.com/google/j2objc) to translate the .java sources 
into Objective-C.

Take a look at [graphhopper-ios-sample](https://github.com/clns/graphhopper-ios-sample) 
for the easiest way to get started.

## Getting Started

Run the following commands in Terminal:

```sh
git clone https://github.com/clns/graphhopper-ios.git
cd graphhopper-ios
make -f make/prepare.mk
```

## Usage

At this point you are ready to translate and compile the library. 
You can do this manually from the Terminal or using Xcode.

### Xcode

The easiest way to use the library is by including *graphhopper.xcodeproj* 
into your own project. Follow the steps below:

- Drag&drop **graphhopper.xcodeproj** into your project (or use the menu File -> Add Files to...)
- Expand graphhopper.xcodeproj and drag&drop the **Translations** and **Libraries** 
groups into your project (make sure you check "Create folder references" and have 
your target selected in "Add to targets:")
- In the Build Settings of your project:
    - add `-ObjC` to your target's Other Linker Flags
    - add `{path-to-graphhopper-ios}/j2objc/include` and `{path-to-graphhopper-ios}/src` 
    to your target's Header Search Paths
- In the Build Phases of your project:
    - in Target Dependencies add the **graphhopper** target
    - in Link Binary With Libraries add **Security.framework** and **libz.dylib**

You're now ready to use GraphHopper on iOS and OS X.

### Terminal

Alternatively, you can translate and compile the library by invoking `make` 
in the Terminal. Note that this will compile the library for the following 
architectures: macosx, simulator, iphoneos. You can then link the library 
`graphhopper-ios/build/libgraphhopper.a` and it's header files at `graphhopper-ios/src` 
manually into your project. For all other requirements see the Xcode section above.
