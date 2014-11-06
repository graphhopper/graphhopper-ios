//
//  Directions.m
//  graphhopper-ios-sample
//
//  Created by Calin on 11/5/14.
//  Copyright (c) 2014 Calin Seciu. All rights reserved.
//

#import "Directions.h"

#import "com/graphhopper/GraphHopper.h"
#import "com/graphhopper/routing/util/EncodingManager.h"
#import "com/graphhopper/GHRequest.h"
#import "com/graphhopper/GHResponse.h"
#import "com/graphhopper/util/PointList.h"
#import "com/graphhopper/storage/GraphStorage.h"

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
        
        CLLocationCoordinate2D brasov = CLLocationCoordinate2DMake(45.651796, 25.6125);
        [_mapView mbx_setCenterCoordinate:brasov zoomLevel:12 animated:NO];
        
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
        
        NSString *location = [[NSBundle mainBundle] pathForResource:@"graph-data" ofType:@"osm-gh"];
        
        _hopper = [[GraphHopper alloc] init];
        [_hopper setCHEnableWithBoolean:YES];
        [_hopper setAllowWritesWithBoolean:NO];
        [_hopper setMemoryMapped];
        [_hopper setEncodingManagerWithEncodingManager:[[EncodingManager alloc] initWithNSString:@"car"]];
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
    self.textView.text = @"Calculating...";
    
    MKPointAnnotation *point1 = [[_mapView annotations] objectAtIndex:0],
    *point2 = [[_mapView annotations] objectAtIndex:1];
    
    GHRequest *request = [[GHRequest alloc] initWithDouble:point1.coordinate.latitude
                                                withDouble:point1.coordinate.longitude
                                                withDouble:point2.coordinate.latitude
                                                withDouble:point2.coordinate.longitude];
    GHResponse *response = [self.hopper routeWithGHRequest:request];
    
    NSString *routeInfo = @"";
    if ([response hasErrors]) {
        NSLog(@"%@", [response getErrors]);
        routeInfo = [routeInfo stringByAppendingString:[NSString stringWithFormat:@"%@\n", [response getErrors]]];
    }
    routeInfo = [routeInfo stringByAppendingString:[NSString stringWithFormat:@"%@\n", [response getDebugInfo]]];
    
    PointList *points = [response getPoints];
    NSLog(@"Route consists of %d points.", [points getSize]);
    routeInfo = [routeInfo stringByAppendingString:[NSString stringWithFormat:@"Route consists of %d points.\n", [points getSize]]];
    
    CLLocationCoordinate2D coords[[points getSize]];
    for (int i = 0; i < [points getSize]; i++) {
        CLLocationCoordinate2D coord = CLLocationCoordinate2DMake([points getLatitudeWithInt:i], [points getLongitudeWithInt:i]);
        coords[i] = coord;
    }
    
    _currentRoute = [MKPolyline polylineWithCoordinates:coords count:[points getSize]];
    [self.mapView addOverlay:_currentRoute];
    
    self.textView.text = routeInfo;
}

@end
