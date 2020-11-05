// (c) 2020 under LGPL by CrossMobile plugin tools

// crossmobile_ios_webkit_WKSecurityOrigin definition

#import "xmlvm.h"
#import <WebKit/WebKit.h>
@class java_lang_String;

CM_EXPORT_CLASS
@interface crossmobile_ios_webkit_WKSecurityOrigin$Ext : WKSecurityOrigin
@end

#define crossmobile_ios_webkit_WKSecurityOrigin WKSecurityOrigin
@interface WKSecurityOrigin (cm_crossmobile_ios_webkit_WKSecurityOrigin)
- (NSString*) host__;
- (int) port__;
- (NSString*) protocol__;
@end