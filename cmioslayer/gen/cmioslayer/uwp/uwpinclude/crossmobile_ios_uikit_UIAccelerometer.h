// (c) 2021 under LGPL by CrossMobile plugin tools

// crossmobile_ios_uikit_UIAccelerometer definition

#import "xmlvm.h"
#import <CoreGraphics/CoreGraphics.h>
#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
@protocol crossmobile_ios_uikit_UIAccelerometerDelegate;

CM_EXPORT_CLASS
@interface crossmobile_ios_uikit_UIAccelerometer$Ext : UIAccelerometer
@end

#define crossmobile_ios_uikit_UIAccelerometer UIAccelerometer
@interface UIAccelerometer (cm_crossmobile_ios_uikit_UIAccelerometer)
+ (UIAccelerometer*) sharedAccelerometer__;
- (void) setDelegate___crossmobile_ios_uikit_UIAccelerometerDelegate:(id<UIAccelerometerDelegate>) delegate ;
- (void) setUpdateInterval___double:(double) updateInterval ;
@end
