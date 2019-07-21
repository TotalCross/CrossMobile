// (c) 2019 under LGPL by CrossMobile plugin tools

// crossmobile.ios.uikit.UIPickerView definition

#import "xmlvm.h"
#import <CoreGraphics/CoreGraphics.h>
#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
@class crossmobile_ios_coregraphics_CGRect;
@class crossmobile_ios_coregraphics_CGSize;
@class crossmobile_ios_uikit_UIPickerViewAppearance;
@protocol crossmobile_ios_uikit_UIPickerViewDataSource;
@protocol crossmobile_ios_uikit_UIPickerViewDelegate;
@class crossmobile_ios_uikit_UIView;
@protocol java_util_List;

CM_EXPORT_CLASS
@interface crossmobile_ios_uikit_UIPickerView$Ext : UIPickerView
@end

#define crossmobile_ios_uikit_UIPickerView UIPickerView
@interface UIPickerView (cm_crossmobile_ios_uikit_UIPickerView)
+ (instancetype) appearance__;
+ (instancetype) appearanceWhenContainedInInstancesOfClasses___java_util_List:(NSArray*) containerTypes ;
- (instancetype) __init_crossmobile_ios_uikit_UIPickerView__;
- (instancetype) __init_crossmobile_ios_uikit_UIPickerView___crossmobile_ios_coregraphics_CGRect:(crossmobile_ios_coregraphics_CGRect*) frame ;
- (void) setDataSource___crossmobile_ios_uikit_UIPickerViewDataSource:(id<UIPickerViewDataSource>) dataSource ;
- (id<UIPickerViewDataSource>) dataSource__;
- (void) setDelegate___crossmobile_ios_uikit_UIPickerViewDelegate:(id<UIPickerViewDelegate>) delegate ;
- (id<UIPickerViewDelegate>) delegate__;
- (int) numberOfComponents__;
- (void) setShowsSelectionIndicator___boolean:(BOOL) showsSelectionIndicator ;
- (BOOL) showsSelectionIndicator__;
- (int) numberOfRowsInComponent___int:(int) component ;
- (void) reloadAllComponents__;
- (void) reloadComponent___int:(int) component ;
- (crossmobile_ios_coregraphics_CGSize*) rowSizeForComponent___int:(int) component ;
- (void) selectRow___int_int_boolean:(int) row :(int) component :(BOOL) animated ;
- (int) selectedRowInComponent___int:(int) component ;
- (UIView*) viewForRow___int_int:(int) row :(int) component ;
@end
