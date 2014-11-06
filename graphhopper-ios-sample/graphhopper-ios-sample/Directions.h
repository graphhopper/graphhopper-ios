//
//  Directions.h
//  graphhopper-ios-sample
//
//  Created by Calin on 11/5/14.
//  Copyright (c) 2014 Calin Seciu. All rights reserved.
//

#import <Foundation/Foundation.h>
@import MapKit;
#import "MBXMapKit.h"

@interface Directions : NSObject

- (id)initWithMapView:(MKMapView *)view andTextView:(UITextView *)textView;
- (void)route;

@end
