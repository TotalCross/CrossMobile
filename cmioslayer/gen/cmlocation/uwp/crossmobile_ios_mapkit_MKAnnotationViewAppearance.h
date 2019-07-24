// (c) 2019 under LGPL by CrossMobile plugin tools

// crossmobile.ios.mapkit.MKAnnotationViewAppearance definition

#import "xmlvm.h"
#import <CoreLocation/CoreLocation.h>
#import <MapKit/MapKit.h>
#import "crossmobile_ios_foundation_NSObject.h"
@class crossmobile_ios_uikit_UIColor;

@interface crossmobile_ios_mapkit_MKAnnotationViewAppearance : crossmobile_ios_foundation_NSObject {
@public id $reference;
}

- (void) setBackgroundColor___crossmobile_ios_uikit_UIColor:(UIColor*) backgroundColor ;
- (instancetype) initWithMKAnnotationViewAppearance:(id) reference;
@end