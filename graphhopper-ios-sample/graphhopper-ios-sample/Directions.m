//
//  Directions.m
//  graphhopper-ios-sample
//
//  Created by Calin on 11/5/14.
//  Copyright (c) 2014 Calin Seciu. All rights reserved.
//

#import "Directions.h"
#import "MBXMapKit.h"

#import "com/graphhopper/GraphHopper.h"
#import "com/graphhopper/config/Profile.h"
#import "com/graphhopper/config/CHProfile.h"
#import "com/graphhopper/routing/ch/CHPreparationHandler.h"
#import "com/graphhopper/ResponsePath.h"
#import "com/graphhopper/routing/util/EncodingManager.h"
#import "com/graphhopper/GHRequest.h"
#import "com/graphhopper/GHResponse.h"
#import "com/graphhopper/util/PointList.h"
#import "com/graphhopper/storage/GraphStorage.h"
#import "com/graphhopper/util/TranslationMap.h"
#include "java/io/File.h"

@interface Directions ()

@property (weak, nonatomic) MKMapView *mapView;
@property (weak, nonatomic) UITextView *textView;
@property (nonatomic) GraphHopper *hopper;
@property (nonatomic) MKPolyline *currentRoute;

@end

@implementation Directions

- (id)initWithMapView:(MKMapView *)view andTextView:(UITextView *)textView
{
    if (self = [super init]) {
        
        _mapView = view;
        _textView = textView;
        
        CLLocationCoordinate2D tuebingen = CLLocationCoordinate2DMake(48.523, 9.048);
        [_mapView mbx_setCenterCoordinate:tuebingen zoomLevel:10 animated:NO];
        
        UILongPressGestureRecognizer *gesture = [[UILongPressGestureRecognizer alloc] initWithTarget:self action:@selector(handleLongPress:)];
        gesture.minimumPressDuration = 1;
        [_mapView addGestureRecognizer:gesture];
        
        return self;
    }
    return nil;
}

- (void)handleLongPress:(UILongPressGestureRecognizer *)gesture
{
    if (gesture.state != UIGestureRecognizerStateBegan)
        return;
    
    CGPoint point = [gesture locationInView:_mapView];
    CLLocationCoordinate2D coordinate = [_mapView convertPoint:point toCoordinateFromView:_mapView];
    
    [self addMarkerAt:coordinate];
}

- (void)addMarkerAt:(CLLocationCoordinate2D)coordinate
{
    if ([_mapView.annotations count] == 2)
        [self clearRoute];
    
    MKPointAnnotation *point = [[MKPointAnnotation alloc] init];
    point.coordinate = coordinate;
    point.title = [NSString stringWithFormat:@"%f, %f", coordinate.latitude, coordinate.longitude];
    [_mapView addAnnotation:point];
    
    if ([_mapView.annotations count] == 2)
        [self route];
}

- (GraphHopper *)hopper
{
    if (!_hopper) {
      
        NSString *folderPath = [[NSBundle mainBundle] resourcePath];
        JavaIoFile *localizationsFolder = create_JavaIoFile_initWithNSString_(JreStrcat("$", folderPath));
        TranslationMap *translationMap = [create_TranslationMap_init() doImportWithJavaIoFile: localizationsFolder];
      
        _hopper = [[GraphHopper alloc] initWithTranslationMap: translationMap];
        [_hopper forMobile];
        
        ComGraphhopperConfigProfile *profile = [[ComGraphhopperConfigProfile alloc] initWithNSString:@"car"];
          [ profile setVehicleWithNSString:@"car"];
          [ profile setWeightingWithNSString:@"fastest"];
        ComGraphhopperConfigProfile *profiles[] = { profile };
            
        IOSObjectArray *ar = [ IOSObjectArray newArrayWithObjects:profiles count:1 type:[ComGraphhopperConfigProfile java_getClass]];
        
        [ _hopper setProfilesWithComGraphhopperConfigProfileArray:ar];
        ComGraphhopperConfigCHProfile *chPC[] = {[[ComGraphhopperConfigCHProfile alloc ] initWithNSString:@"car" ]};
        CHPreparationHandler * chPH = [ _hopper getCHPreparationHandler];
        
        IOSObjectArray *CHar = [ IOSObjectArray newArrayWithObjects:chPC count:1 type:[ComGraphhopperConfigCHProfile java_getClass]];
        [ chPH setCHProfilesWithComGraphhopperConfigCHProfileArray: CHar];
      
        [_hopper setAllowWritesWithBoolean: false];
      
        NSString *location = [[NSBundle mainBundle] pathForResource:@"graph-data" ofType:@"osm-gh"];
        NSLog(@"LOC %@\n", location);
        [_hopper load__WithNSString:location];
    }
    return _hopper;
}

- (void)clearRoute
{
    [self.mapView removeOverlay:_currentRoute];
    [self.mapView removeAnnotations:self.mapView.annotations];
    self.textView.text = @"Drop pins to create a route";
}

- (void)route
{
    self.textView.text = @"Calculating route...";
    
    id<MKAnnotation> point1 = [[_mapView annotations] objectAtIndex:0],
    point2 = [[_mapView annotations] objectAtIndex:1];
    
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        GHRequest *request = [[GHRequest alloc] initWithDouble:point1.coordinate.latitude
                                                    withDouble:point1.coordinate.longitude
                                                    withDouble:point2.coordinate.latitude
                                                    withDouble:point2.coordinate.longitude];
        [[ request setAlgorithmWithNSString:@"dijkstrabi"] setProfileWithNSString:@"car" ];
        GHResponse *response = [self.hopper routeWithGHRequest:request];
            
        NSString *routeInfo = @"";
        if ([response hasErrors]) {
            NSLog(@"%@", [response getErrors]);
            routeInfo = [routeInfo stringByAppendingString:[NSString stringWithFormat:@"%@\n", [response getErrors]]];
        }
        routeInfo = [routeInfo stringByAppendingString:[NSString stringWithFormat:@"%@", [response getDebugInfo]]];
        
        ResponsePath *best = [response getBest];
        PointList *points = [best getPoints];
        NSLog(@"Route consists of %d points.", [points getSize]);
        
        CLLocationCoordinate2D coords[[points getSize]];
        for (int i = 0; i < [points getSize]; i++) {
            CLLocationCoordinate2D coord = CLLocationCoordinate2DMake([points getLatitudeWithInt:i], [points getLongitudeWithInt:i]);
            coords[i] = coord;
        }
        
        _currentRoute = [MKPolyline polylineWithCoordinates:coords count:[points getSize]];
        
        dispatch_async(dispatch_get_main_queue(), ^{
            [self.mapView addOverlay:_currentRoute];
            self.textView.text = routeInfo;
        });
    });
}

@end
